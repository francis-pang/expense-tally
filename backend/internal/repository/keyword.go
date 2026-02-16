package repository

import (
	"context"
	"fmt"

	"expense-tally-v2/internal/model"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

// KeywordRepository handles DynamoDB operations for keyword associations.
type KeywordRepository struct {
	client *dynamodb.Client
	table  string
}

// NewKeywordRepository creates a new KeywordRepository.
func NewKeywordRepository(client *dynamodb.Client, table string) *KeywordRepository {
	return &KeywordRepository{client: client, table: table}
}

// GetByKeyword retrieves all associations for a keyword (PK=KW#keyword).
func (r *KeywordRepository) GetByKeyword(ctx context.Context, keyword string) ([]model.KeywordAssociation, error) {
	pk := "KW#" + keyword
	out, err := r.client.Query(ctx, &dynamodb.QueryInput{
		TableName: aws.String(r.table),
		KeyConditionExpression: aws.String("PK = :pk"),
		ExpressionAttributeValues: map[string]types.AttributeValue{
			":pk": &types.AttributeValueMemberS{Value: pk},
		},
	})
	if err != nil {
		return nil, err
	}
	var result []model.KeywordAssociation
	for _, item := range out.Items {
		kw, err := itemToKeywordAssociation(item)
		if err != nil {
			return nil, err
		}
		result = append(result, *kw)
	}
	return result, nil
}

// BatchGetByKeywords retrieves associations for multiple keywords.
func (r *KeywordRepository) BatchGetByKeywords(ctx context.Context, keywords []string) (map[string][]model.KeywordAssociation, error) {
	if len(keywords) == 0 {
		return make(map[string][]model.KeywordAssociation), nil
	}
	result := make(map[string][]model.KeywordAssociation)
	for _, kw := range keywords {
		assocs, err := r.GetByKeyword(ctx, kw)
		if err != nil {
			return nil, err
		}
		result[kw] = assocs
	}
	return result, nil
}

// Put inserts or replaces a keyword association.
func (r *KeywordRepository) Put(ctx context.Context, kw *model.KeywordAssociation) error {
	item := keywordAssociationToItem(kw)
	_, err := r.client.PutItem(ctx, &dynamodb.PutItemInput{
		TableName: aws.String(r.table),
		Item:      item,
	})
	return err
}

// IncrementFrequency increments the frequency for a keyword-category pair.
func (r *KeywordRepository) IncrementFrequency(ctx context.Context, pk, categoryID, lastSeenAt string) error {
	_, err := r.client.UpdateItem(ctx, &dynamodb.UpdateItemInput{
		TableName: aws.String(r.table),
		Key: map[string]types.AttributeValue{
			"PK":         &types.AttributeValueMemberS{Value: pk},
			"categoryId": &types.AttributeValueMemberS{Value: categoryID},
		},
		UpdateExpression: aws.String("SET frequency = if_not_exists(frequency, :zero) + :inc, lastSeenAt = :ts"),
		ExpressionAttributeValues: map[string]types.AttributeValue{
			":zero": &types.AttributeValueMemberN{Value: "0"},
			":inc":  &types.AttributeValueMemberN{Value: "1"},
			":ts":   &types.AttributeValueMemberS{Value: lastSeenAt},
		},
	})
	return err
}

func keywordAssociationToItem(k *model.KeywordAssociation) map[string]types.AttributeValue {
	return map[string]types.AttributeValue{
		"PK":         &types.AttributeValueMemberS{Value: k.PK},
		"categoryId": &types.AttributeValueMemberS{Value: k.CategoryID},
		"frequency":  &types.AttributeValueMemberN{Value: fmt.Sprintf("%d", k.Frequency)},
		"lastSeenAt": &types.AttributeValueMemberS{Value: k.LastSeenAt},
	}
}

func itemToKeywordAssociation(item map[string]types.AttributeValue) (*model.KeywordAssociation, error) {
	k := &model.KeywordAssociation{}
	if v, ok := item["PK"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			k.PK = s.Value
		}
	}
	if v, ok := item["categoryId"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			k.CategoryID = s.Value
		}
	}
	if v, ok := item["frequency"]; ok {
		if n, ok := v.(*types.AttributeValueMemberN); ok {
			var i int
			fmt.Sscanf(n.Value, "%d", &i)
			k.Frequency = i
		}
	}
	if v, ok := item["lastSeenAt"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			k.LastSeenAt = s.Value
		}
	}
	return k, nil
}
