package service

import (
	"context"
	"fmt"

	"expense-tally/internal/model"
	"expense-tally/internal/repository"

	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/google/uuid"
)

// CategoryService handles category business logic.
type CategoryService struct {
	repo *repository.CategoryRepository
}

// NewCategoryService creates a new CategoryService.
func NewCategoryService(repo *repository.CategoryRepository) *CategoryService {
	return &CategoryService{repo: repo}
}

// List returns all categories.
func (s *CategoryService) List(ctx context.Context) ([]model.Category, error) {
	return s.repo.List(ctx)
}

// Get retrieves a category by ID.
func (s *CategoryService) Get(ctx context.Context, id string) (*model.Category, error) {
	pk := "CAT#" + id
	return s.repo.GetByPK(ctx, pk)
}

// Create creates a new category with generated UUID.
func (s *CategoryService) Create(ctx context.Context, req model.CreateCategoryRequest) (*model.Category, error) {
	id := uuid.New().String()
	pk := "CAT#" + id
	cat := &model.Category{
		PK:       pk,
		Name:     req.Name,
		ParentID: req.ParentID,
	}
	if err := s.repo.Put(ctx, cat); err != nil {
		return nil, err
	}
	return s.repo.GetByPK(ctx, pk)
}

// Update updates a category.
func (s *CategoryService) Update(ctx context.Context, id string, req model.UpdateCategoryRequest) (*model.Category, error) {
	pk := "CAT#" + id
	cat, err := s.repo.GetByPK(ctx, pk)
	if err != nil {
		return nil, err
	}
	if cat == nil {
		return nil, fmt.Errorf("category not found")
	}
	updates := make(map[string]types.AttributeValue)
	if req.Name != "" {
		updates["name"] = &types.AttributeValueMemberS{Value: req.Name}
	}
	if req.ParentID != nil {
		updates["parentId"] = &types.AttributeValueMemberS{Value: *req.ParentID}
	}
	if len(updates) > 0 {
		if err := s.repo.Update(ctx, pk, updates); err != nil {
			return nil, err
		}
	}
	return s.repo.GetByPK(ctx, pk)
}

// Delete removes a category.
func (s *CategoryService) Delete(ctx context.Context, id string) error {
	pk := "CAT#" + id
	return s.repo.Delete(ctx, pk)
}
