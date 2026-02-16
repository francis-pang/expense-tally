package service

import (
	"context"
	"fmt"

	"expense-tally/internal/model"
	"expense-tally/internal/repository"

	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/google/uuid"
)

// TransactionService handles transaction business logic.
type TransactionService struct {
	txnRepo    *repository.TransactionRepository
	catRepo    *repository.CategoryRepository
	keywordSvc *KeywordService
}

// NewTransactionService creates a new TransactionService.
func NewTransactionService(
	txnRepo *repository.TransactionRepository,
	catRepo *repository.CategoryRepository,
	keywordSvc *KeywordService,
) *TransactionService {
	return &TransactionService{
		txnRepo:    txnRepo,
		catRepo:    catRepo,
		keywordSvc: keywordSvc,
	}
}

// List returns transactions by date range with optional filters.
func (s *TransactionService) List(ctx context.Context, year, startDate, endDate, categoryID string, confirmed *bool) ([]model.Transaction, error) {
	txns, err := s.txnRepo.ListByDateRange(ctx, year, startDate, endDate)
	if err != nil {
		return nil, err
	}
	// Filter by categoryId and confirmed in memory (could push to DynamoDB with filter expression)
	var filtered []model.Transaction
	for _, t := range txns {
		if categoryID != "" && (t.CategoryID == nil || *t.CategoryID != categoryID) {
			continue
		}
		if confirmed != nil && t.IsConfirmed != *confirmed {
			continue
		}
		filtered = append(filtered, t)
	}
	return filtered, nil
}

// ListUnconfirmed returns unconfirmed transactions for review.
func (s *TransactionService) ListUnconfirmed(ctx context.Context, startDate, endDate string) ([]model.Transaction, error) {
	return s.txnRepo.ListUnconfirmed(ctx, startDate, endDate)
}

// Get retrieves a transaction by ID.
func (s *TransactionService) Get(ctx context.Context, id string) (*model.Transaction, error) {
	return s.txnRepo.GetByPK(ctx, id)
}

// CreateManual creates a manual cash entry with keyword suggestion.
func (s *TransactionService) CreateManual(ctx context.Context, req model.CreateTransactionRequest) (*model.Transaction, error) {
	now := formatISO8601()
	id := uuid.New().String()
	pk := "TXN#manual#" + id

	// Get keyword suggestion
	suggested, _ := s.keywordSvc.SuggestCategory(ctx, req.Description, req.Merchant)
	var suggestedPtr *string
	if suggested != "" {
		suggestedPtr = &suggested
	}

	year := extractYear(req.Date)
	txn := &model.Transaction{
		PK:                  pk,
		Date:                req.Date,
		Source:              "manual",
		Amount:              req.Amount,
		Currency:            req.Currency,
		Description:         req.Description,
		Merchant:            req.Merchant,
		CategoryID:          req.CategoryID,
		SuggestedCategoryID: suggestedPtr,
		IsConfirmed:         false,
		PaymentMethod:       req.PaymentMethod,
		CreatedAt:           now,
		UpdatedAt:           now,
		GSI1PK:              "YEAR#" + year,
		GSI2PK:              "UNCONFIRMED",
	}
	if txn.PaymentMethod == "" {
		txn.PaymentMethod = "cash"
	}
	if txn.Currency == "" {
		txn.Currency = "USD"
	}

	if err := s.txnRepo.Put(ctx, txn); err != nil {
		return nil, err
	}

	// Learn from tagging if category was provided
	if req.CategoryID != nil && *req.CategoryID != "" {
		_ = s.keywordSvc.LearnFromTagging(ctx, req.Description, req.Merchant, *req.CategoryID)
	}

	return txn, nil
}

// Update updates a transaction (e.g. category). If category changed, updates keyword associations.
func (s *TransactionService) Update(ctx context.Context, id string, req model.UpdateTransactionRequest) (*model.Transaction, error) {
	txn, err := s.txnRepo.GetByPK(ctx, id)
	if err != nil {
		return nil, err
	}
	if txn == nil {
		return nil, fmt.Errorf("transaction not found")
	}
	updates := make(map[string]types.AttributeValue)
	if req.CategoryID != nil {
		// Learn from new tagging
		if *req.CategoryID != "" {
			_ = s.keywordSvc.LearnFromTagging(ctx, txn.Description, txn.Merchant, *req.CategoryID)
		}
		updates["categoryId"] = &types.AttributeValueMemberS{Value: *req.CategoryID}
	}
	if len(updates) > 0 {
		updates["updatedAt"] = &types.AttributeValueMemberS{Value: formatISO8601()}
		if err := s.txnRepo.Update(ctx, id, updates); err != nil {
			return nil, err
		}
	}
	return s.txnRepo.GetByPK(ctx, id)
}

// Delete removes a transaction.
func (s *TransactionService) Delete(ctx context.Context, id string) error {
	return s.txnRepo.Delete(ctx, id)
}

// Confirm marks a transaction as confirmed (removes from unconfirmed index).
func (s *TransactionService) Confirm(ctx context.Context, id string) (*model.Transaction, error) {
	txn, err := s.txnRepo.GetByPK(ctx, id)
	if err != nil {
		return nil, err
	}
	if txn == nil {
		return nil, fmt.Errorf("transaction not found")
	}
	if err := s.txnRepo.Confirm(ctx, id); err != nil {
		return nil, err
	}
	txn.IsConfirmed = true
	txn.GSI2PK = ""
	return txn, nil
}
