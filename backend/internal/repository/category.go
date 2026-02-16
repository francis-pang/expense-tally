package repository

import (
	"context"

	"expense-tally/internal/model"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

// CategoryRepository handles DynamoDB operations for categories.
type CategoryRepository struct {
	client *dynamodb.Client
	table  string
}

// NewCategoryRepository creates a new CategoryRepository.
func NewCategoryRepository(client *dynamodb.Client, table string) *CategoryRepository {
	return &CategoryRepository{client: client, table: table}
}

// List returns all categories (Scan, small table).
func (r *CategoryRepository) List(ctx context.Context) ([]model.Category, error) {
	var items []map[string]types.AttributeValue
	var lastKey map[string]types.AttributeValue
	for {
		input := &dynamodb.ScanInput{
			TableName:         aws.String(r.table),
			ExclusiveStartKey: lastKey,
		}
		out, err := r.client.Scan(ctx, input)
		if err != nil {
			return nil, err
		}
		items = append(items, out.Items...)
		lastKey = out.LastEvaluatedKey
		if lastKey == nil {
			break
		}
	}

	var categories []model.Category
	for _, item := range items {
		cat, err := itemToCategory(item)
		if err != nil {
			return nil, err
		}
		categories = append(categories, *cat)
	}
	return categories, nil
}

// GetByPK retrieves a category by its partition key.
func (r *CategoryRepository) GetByPK(ctx context.Context, pk string) (*model.Category, error) {
	out, err := r.client.GetItem(ctx, &dynamodb.GetItemInput{
		TableName: aws.String(r.table),
		Key: map[string]types.AttributeValue{
			"PK": &types.AttributeValueMemberS{Value: pk},
		},
	})
	if err != nil {
		return nil, err
	}
	if out.Item == nil {
		return nil, nil
	}
	return itemToCategory(out.Item)
}

// Put inserts or replaces a category.
func (r *CategoryRepository) Put(ctx context.Context, cat *model.Category) error {
	item := categoryToItem(cat)
	_, err := r.client.PutItem(ctx, &dynamodb.PutItemInput{
		TableName: aws.String(r.table),
		Item:      item,
	})
	return err
}

// Update updates a category.
func (r *CategoryRepository) Update(ctx context.Context, pk string, updates map[string]types.AttributeValue) error {
	if len(updates) == 0 {
		return nil
	}
	var names map[string]string
	var values map[string]types.AttributeValue
	// Use expression builder for safety
	// For simplicity, build update manually for small updates
	expr := "SET "
	first := true
	for k, v := range updates {
		if k == "PK" {
			continue
		}
		if !first {
			expr += ", "
		}
		placeholder := "#" + k
		expr += placeholder + " = :" + k
		if names == nil {
			names = make(map[string]string)
			values = make(map[string]types.AttributeValue)
		}
		names[placeholder] = k
		values[":"+k] = v
		first = false
	}
	if first {
		return nil
	}
	_, err := r.client.UpdateItem(ctx, &dynamodb.UpdateItemInput{
		TableName:                 aws.String(r.table),
		Key:                       map[string]types.AttributeValue{"PK": &types.AttributeValueMemberS{Value: pk}},
		UpdateExpression:          aws.String(expr),
		ExpressionAttributeNames:  names,
		ExpressionAttributeValues: values,
	})
	return err
}

// Delete removes a category.
func (r *CategoryRepository) Delete(ctx context.Context, pk string) error {
	_, err := r.client.DeleteItem(ctx, &dynamodb.DeleteItemInput{
		TableName: aws.String(r.table),
		Key: map[string]types.AttributeValue{
			"PK": &types.AttributeValueMemberS{Value: pk},
		},
	})
	return err
}

func categoryToItem(c *model.Category) map[string]types.AttributeValue {
	item := map[string]types.AttributeValue{
		"PK":   &types.AttributeValueMemberS{Value: c.PK},
		"name": &types.AttributeValueMemberS{Value: c.Name},
	}
	if c.ParentID != nil {
		item["parentId"] = &types.AttributeValueMemberS{Value: *c.ParentID}
	}
	return item
}

func itemToCategory(item map[string]types.AttributeValue) (*model.Category, error) {
	c := &model.Category{}
	if v, ok := item["PK"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			c.PK = s.Value
		}
	}
	if v, ok := item["name"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			c.Name = s.Value
		}
	}
	if v, ok := item["parentId"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			c.ParentID = &s.Value
		}
	}
	return c, nil
}
