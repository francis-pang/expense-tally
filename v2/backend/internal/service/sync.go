package service

import (
	"context"

	"expense-tally-v2/internal/model"
	"expense-tally-v2/internal/provider"
	"expense-tally-v2/internal/repository"

	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

// SyncService handles syncing transactions from providers.
type SyncService struct {
	connRepo   *repository.ConnectionRepository
	txnRepo   *repository.TransactionRepository
	syncRepo  *repository.SyncLogRepository
	keywordSvc *KeywordService
	adapterFactory func(string) provider.ProviderAdapter
}

// NewSyncService creates a new SyncService.
func NewSyncService(
	connRepo *repository.ConnectionRepository,
	txnRepo *repository.TransactionRepository,
	syncRepo *repository.SyncLogRepository,
	keywordSvc *KeywordService,
	adapterFactory func(string) provider.ProviderAdapter,
) *SyncService {
	return &SyncService{
		connRepo:       connRepo,
		txnRepo:        txnRepo,
		syncRepo:       syncRepo,
		keywordSvc:     keywordSvc,
		adapterFactory: adapterFactory,
	}
}

// SyncAll runs sync for all active connections and logs results.
func (s *SyncService) SyncAll(ctx context.Context) error {
	connections, err := s.connRepo.List(ctx)
	if err != nil {
		return err
	}
	for _, conn := range connections {
		_ = s.SyncConnection(ctx, &conn)
	}
	return nil
}

// SyncConnection syncs transactions for a single connection.
func (s *SyncService) SyncConnection(ctx context.Context, conn *model.ProviderConnection) error {
	adapter := s.adapterFactory(conn.Provider)
	if adapter == nil {
		s.logSync(ctx, conn.Provider, 0, "error", "unknown provider: "+conn.Provider)
		return nil
	}
	// TODO: Get access token from Secrets Manager using conn.AccessTokenRef
	// For now we assume access token is stored in the ref or we need to fetch it
	accessToken := conn.AccessTokenRef // Simplified: use as token for now
	txns, newCursor, err := adapter.FetchTransactions(ctx, accessToken, conn.SyncCursor)
	if err != nil {
		s.logSync(ctx, conn.Provider, 0, "error", err.Error())
		return err
	}
	count := 0
	for _, txn := range txns {
		// Get keyword suggestion
		suggested, _ := s.keywordSvc.SuggestCategory(ctx, txn.Description, txn.Merchant)
		if suggested != "" {
			txn.SuggestedCategoryID = &suggested
		}
		if err := s.txnRepo.Put(ctx, &txn); err != nil {
			continue
		}
		count++
	}
	now := formatISO8601()
	conn.SyncCursor = newCursor
	conn.LastSyncedAt = &now
	_ = s.connRepo.Update(ctx, conn.PK, map[string]types.AttributeValue{
		"syncCursor":   &types.AttributeValueMemberS{Value: newCursor},
		"lastSyncedAt": &types.AttributeValueMemberS{Value: now},
	})
	s.logSync(ctx, conn.Provider, count, "success", "")
	return nil
}

func (s *SyncService) logSync(ctx context.Context, source string, count int, status, errMsg string) {
	now := formatISO8601()
	log := &model.SyncLog{
		PK:               "SYNC#" + source,
		SK:               now,
		Status:           status,
		TransactionCount: count,
		ErrorMessage:     errMsg,
	}
	_ = s.syncRepo.Put(ctx, log)
}

// TriggerSync manually triggers a sync (same as SyncAll).
func (s *SyncService) TriggerSync(ctx context.Context) error {
	return s.SyncAll(ctx)
}

// GetLogs returns recent sync logs.
func (s *SyncService) GetLogs(ctx context.Context, source string, limit int) ([]model.SyncLog, error) {
	return s.syncRepo.ListBySource(ctx, source, limit)
}
