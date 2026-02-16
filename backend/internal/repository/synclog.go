package repository

import (
	"context"
	"fmt"

	"expense-tally/internal/model"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/expression"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

// SyncLogRepository handles DynamoDB operations for sync logs.
type SyncLogRepository struct {
	client *dynamodb.Client
	table  string
}

// NewSyncLogRepository creates a new SyncLogRepository.
func NewSyncLogRepository(client *dynamodb.Client, table string) *SyncLogRepository {
	return &SyncLogRepository{client: client, table: table}
}

// ListBySource queries sync logs by source (PK=SYNC#source), sorted by SK descending.
func (r *SyncLogRepository) ListBySource(ctx context.Context, source string, limit int) ([]model.SyncLog, error) {
	pk := "SYNC#" + source
	expr, err := expression.NewBuilder().
		WithKeyCondition(expression.Key("PK").Equal(expression.Value(pk))).
		Build()
	if err != nil {
		return nil, err
	}
	if limit <= 0 {
		limit = 20
	}
	out, err := r.client.Query(ctx, &dynamodb.QueryInput{
		TableName:                 aws.String(r.table),
		KeyConditionExpression:    expr.KeyCondition(),
		ExpressionAttributeNames:  expr.Names(),
		ExpressionAttributeValues: expr.Values(),
		ScanIndexForward:          aws.Bool(false),
		Limit:                     aws.Int32(int32(limit)),
	})
	if err != nil {
		return nil, err
	}
	var logs []model.SyncLog
	for _, item := range out.Items {
		log, err := itemToSyncLog(item)
		if err != nil {
			return nil, err
		}
		logs = append(logs, *log)
	}
	return logs, nil
}

// Put inserts a sync log.
func (r *SyncLogRepository) Put(ctx context.Context, log *model.SyncLog) error {
	item := syncLogToItem(log)
	_, err := r.client.PutItem(ctx, &dynamodb.PutItemInput{
		TableName: aws.String(r.table),
		Item:      item,
	})
	return err
}

func syncLogToItem(l *model.SyncLog) map[string]types.AttributeValue {
	item := map[string]types.AttributeValue{
		"PK":               &types.AttributeValueMemberS{Value: l.PK},
		"SK":               &types.AttributeValueMemberS{Value: l.SK},
		"status":           &types.AttributeValueMemberS{Value: l.Status},
		"transactionCount": &types.AttributeValueMemberN{Value: fmt.Sprintf("%d", l.TransactionCount)},
	}
	if l.ErrorMessage != "" {
		item["errorMessage"] = &types.AttributeValueMemberS{Value: l.ErrorMessage}
	}
	return item
}

func itemToSyncLog(item map[string]types.AttributeValue) (*model.SyncLog, error) {
	l := &model.SyncLog{}
	if v, ok := item["PK"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			l.PK = s.Value
		}
	}
	if v, ok := item["SK"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			l.SK = s.Value
		}
	}
	if v, ok := item["status"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			l.Status = s.Value
		}
	}
	if v, ok := item["transactionCount"]; ok {
		if n, ok := v.(*types.AttributeValueMemberN); ok {
			var i int
			fmt.Sscanf(n.Value, "%d", &i)
			l.TransactionCount = i
		}
	}
	if v, ok := item["errorMessage"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			l.ErrorMessage = s.Value
		}
	}
	return l, nil
}
