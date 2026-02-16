package repository

import (
	"context"

	"expense-tally/internal/model"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

// ConnectionRepository handles DynamoDB operations for provider connections.
type ConnectionRepository struct {
	client *dynamodb.Client
	table  string
}

// NewConnectionRepository creates a new ConnectionRepository.
func NewConnectionRepository(client *dynamodb.Client, table string) *ConnectionRepository {
	return &ConnectionRepository{client: client, table: table}
}

// List returns all connections (Scan).
func (r *ConnectionRepository) List(ctx context.Context) ([]model.ProviderConnection, error) {
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

	var connections []model.ProviderConnection
	for _, item := range items {
		conn, err := itemToConnection(item)
		if err != nil {
			return nil, err
		}
		connections = append(connections, *conn)
	}
	return connections, nil
}

// GetByPK retrieves a connection by its partition key.
func (r *ConnectionRepository) GetByPK(ctx context.Context, pk string) (*model.ProviderConnection, error) {
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
	return itemToConnection(out.Item)
}

// Put inserts or replaces a connection.
func (r *ConnectionRepository) Put(ctx context.Context, conn *model.ProviderConnection) error {
	item := connectionToItem(conn)
	_, err := r.client.PutItem(ctx, &dynamodb.PutItemInput{
		TableName: aws.String(r.table),
		Item:      item,
	})
	return err
}

// Update updates a connection.
func (r *ConnectionRepository) Update(ctx context.Context, pk string, updates map[string]types.AttributeValue) error {
	if len(updates) == 0 {
		return nil
	}
	expr := "SET "
	first := true
	names := make(map[string]string)
	values := make(map[string]types.AttributeValue)
	for k, v := range updates {
		if k == "PK" {
			continue
		}
		if !first {
			expr += ", "
		}
		placeholder := "#" + k
		expr += placeholder + " = :" + k
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

func connectionToItem(c *model.ProviderConnection) map[string]types.AttributeValue {
	item := map[string]types.AttributeValue{
		"PK":             &types.AttributeValueMemberS{Value: c.PK},
		"provider":       &types.AttributeValueMemberS{Value: c.Provider},
		"accessTokenRef": &types.AttributeValueMemberS{Value: c.AccessTokenRef},
		"syncCursor":     &types.AttributeValueMemberS{Value: c.SyncCursor},
	}
	if c.LastSyncedAt != nil {
		item["lastSyncedAt"] = &types.AttributeValueMemberS{Value: *c.LastSyncedAt}
	}
	return item
}

func itemToConnection(item map[string]types.AttributeValue) (*model.ProviderConnection, error) {
	c := &model.ProviderConnection{}
	if v, ok := item["PK"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			c.PK = s.Value
		}
	}
	if v, ok := item["provider"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			c.Provider = s.Value
		}
	}
	if v, ok := item["accessTokenRef"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			c.AccessTokenRef = s.Value
		}
	}
	if v, ok := item["syncCursor"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			c.SyncCursor = s.Value
		}
	}
	if v, ok := item["lastSyncedAt"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			c.LastSyncedAt = &s.Value
		}
	}
	return c, nil
}
