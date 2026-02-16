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

// TransactionRepository handles DynamoDB operations for transactions.
type TransactionRepository struct {
	client *dynamodb.Client
	table  string
}

// NewTransactionRepository creates a new TransactionRepository.
func NewTransactionRepository(client *dynamodb.Client, table string) *TransactionRepository {
	return &TransactionRepository{client: client, table: table}
}

// GetByPK retrieves a transaction by its partition key.
func (r *TransactionRepository) GetByPK(ctx context.Context, pk string) (*model.Transaction, error) {
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
	return itemToTransaction(out.Item)
}

// ListByDateRange queries transactions by date range using GSI1 (DateIndex).
func (r *TransactionRepository) ListByDateRange(ctx context.Context, year, startDate, endDate string) ([]model.Transaction, error) {
	gsi1pk := "YEAR#" + year
	keyCond := expression.Key("gsi1pk").Equal(expression.Value(gsi1pk))
	if startDate != "" && endDate != "" {
		keyCond = expression.Key("gsi1pk").Equal(expression.Value(gsi1pk)).
			And(expression.Key("date").Between(expression.Value(startDate), expression.Value(endDate)))
	} else if startDate != "" {
		keyCond = expression.Key("gsi1pk").Equal(expression.Value(gsi1pk)).
			And(expression.Key("date").GreaterThanEqual(expression.Value(startDate)))
	} else if endDate != "" {
		keyCond = expression.Key("gsi1pk").Equal(expression.Value(gsi1pk)).
			And(expression.Key("date").LessThanEqual(expression.Value(endDate)))
	}

	expr, err := expression.NewBuilder().WithKeyCondition(keyCond).Build()
	if err != nil {
		return nil, err
	}

	var items []map[string]types.AttributeValue
	var lastKey map[string]types.AttributeValue
	for {
		input := &dynamodb.QueryInput{
			TableName:                 aws.String(r.table),
			IndexName:                 aws.String("DateIndex"),
			KeyConditionExpression:    expr.KeyCondition(),
			ExpressionAttributeNames:  expr.Names(),
			ExpressionAttributeValues: expr.Values(),
			ExclusiveStartKey:         lastKey,
		}
		out, err := r.client.Query(ctx, input)
		if err != nil {
			return nil, err
		}
		items = append(items, out.Items...)
		lastKey = out.LastEvaluatedKey
		if lastKey == nil {
			break
		}
	}

	var txns []model.Transaction
	for _, item := range items {
		txn, err := itemToTransaction(item)
		if err != nil {
			return nil, err
		}
		txns = append(txns, *txn)
	}
	return txns, nil
}

// ListUnconfirmed queries unconfirmed transactions using GSI2 (UnconfirmedIndex).
func (r *TransactionRepository) ListUnconfirmed(ctx context.Context, startDate, endDate string) ([]model.Transaction, error) {
	keyCond := expression.Key("gsi2pk").Equal(expression.Value("UNCONFIRMED"))
	if startDate != "" && endDate != "" {
		keyCond = keyCond.And(expression.Key("date").Between(expression.Value(startDate), expression.Value(endDate)))
	} else if startDate != "" {
		keyCond = keyCond.And(expression.Key("date").GreaterThanEqual(expression.Value(startDate)))
	} else if endDate != "" {
		keyCond = keyCond.And(expression.Key("date").LessThanEqual(expression.Value(endDate)))
	}

	expr, err := expression.NewBuilder().WithKeyCondition(keyCond).Build()
	if err != nil {
		return nil, err
	}

	var items []map[string]types.AttributeValue
	var lastKey map[string]types.AttributeValue
	for {
		input := &dynamodb.QueryInput{
			TableName:                 aws.String(r.table),
			IndexName:                 aws.String("UnconfirmedIndex"),
			KeyConditionExpression:    expr.KeyCondition(),
			ExpressionAttributeNames:  expr.Names(),
			ExpressionAttributeValues: expr.Values(),
			ExclusiveStartKey:         lastKey,
		}
		out, err := r.client.Query(ctx, input)
		if err != nil {
			return nil, err
		}
		items = append(items, out.Items...)
		lastKey = out.LastEvaluatedKey
		if lastKey == nil {
			break
		}
	}

	var txns []model.Transaction
	for _, item := range items {
		txn, err := itemToTransaction(item)
		if err != nil {
			return nil, err
		}
		txns = append(txns, *txn)
	}
	return txns, nil
}

// Put inserts or replaces a transaction.
func (r *TransactionRepository) Put(ctx context.Context, txn *model.Transaction) error {
	item := transactionToItem(txn)
	_, err := r.client.PutItem(ctx, &dynamodb.PutItemInput{
		TableName: aws.String(r.table),
		Item:      item,
	})
	return err
}

// Update updates specific attributes of a transaction.
func (r *TransactionRepository) Update(ctx context.Context, pk string, updates map[string]types.AttributeValue) error {
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

// Delete removes a transaction.
func (r *TransactionRepository) Delete(ctx context.Context, pk string) error {
	_, err := r.client.DeleteItem(ctx, &dynamodb.DeleteItemInput{
		TableName: aws.String(r.table),
		Key: map[string]types.AttributeValue{
			"PK": &types.AttributeValueMemberS{Value: pk},
		},
	})
	return err
}

// Confirm marks a transaction as confirmed and removes gsi2pk (sparse index).
func (r *TransactionRepository) Confirm(ctx context.Context, pk string) error {
	update := expression.Set(expression.Name("isConfirmed"), expression.Value(true)).
		Remove(expression.Name("gsi2pk"))
	expr, err := expression.NewBuilder().WithUpdate(update).Build()
	if err != nil {
		return err
	}
	_, err = r.client.UpdateItem(ctx, &dynamodb.UpdateItemInput{
		TableName:                 aws.String(r.table),
		Key:                       map[string]types.AttributeValue{"PK": &types.AttributeValueMemberS{Value: pk}},
		UpdateExpression:          expr.Update(),
		ExpressionAttributeNames:  expr.Names(),
		ExpressionAttributeValues: expr.Values(),
	})
	return err
}

func transactionToItem(t *model.Transaction) map[string]types.AttributeValue {
	item := map[string]types.AttributeValue{
		"PK":            &types.AttributeValueMemberS{Value: t.PK},
		"date":          &types.AttributeValueMemberS{Value: t.Date},
		"source":        &types.AttributeValueMemberS{Value: t.Source},
		"amount":        &types.AttributeValueMemberN{Value: fmt.Sprintf("%.2f", t.Amount)},
		"currency":      &types.AttributeValueMemberS{Value: t.Currency},
		"description":   &types.AttributeValueMemberS{Value: t.Description},
		"merchant":      &types.AttributeValueMemberS{Value: t.Merchant},
		"isConfirmed":   &types.AttributeValueMemberBOOL{Value: t.IsConfirmed},
		"pending":       &types.AttributeValueMemberBOOL{Value: t.Pending},
		"paymentMethod": &types.AttributeValueMemberS{Value: t.PaymentMethod},
		"rawPayload":    &types.AttributeValueMemberS{Value: t.RawPayload},
		"createdAt":     &types.AttributeValueMemberS{Value: t.CreatedAt},
		"updatedAt":     &types.AttributeValueMemberS{Value: t.UpdatedAt},
		"gsi1pk":        &types.AttributeValueMemberS{Value: t.GSI1PK},
	}
	if t.TransactedAt != "" {
		item["transactedAt"] = &types.AttributeValueMemberS{Value: t.TransactedAt}
	}
	if t.CategoryID != nil {
		item["categoryId"] = &types.AttributeValueMemberS{Value: *t.CategoryID}
	}
	if t.SuggestedCategoryID != nil {
		item["suggestedCategoryId"] = &types.AttributeValueMemberS{Value: *t.SuggestedCategoryID}
	}
	if t.AccountID != "" {
		item["accountId"] = &types.AttributeValueMemberS{Value: t.AccountID}
	}
	if t.AccountName != "" {
		item["accountName"] = &types.AttributeValueMemberS{Value: t.AccountName}
	}
	if t.GSI2PK != "" {
		item["gsi2pk"] = &types.AttributeValueMemberS{Value: t.GSI2PK}
	}
	return item
}

func itemToTransaction(item map[string]types.AttributeValue) (*model.Transaction, error) {
	t := &model.Transaction{}
	if v, ok := item["PK"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.PK = s.Value
		}
	}
	if v, ok := item["date"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.Date = s.Value
		}
	}
	if v, ok := item["transactedAt"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.TransactedAt = s.Value
		}
	}
	if v, ok := item["source"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.Source = s.Value
		}
	}
	if v, ok := item["amount"]; ok {
		if n, ok := v.(*types.AttributeValueMemberN); ok {
			var f float64
			fmt.Sscanf(n.Value, "%f", &f)
			t.Amount = f
		}
	}
	if v, ok := item["currency"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.Currency = s.Value
		}
	}
	if v, ok := item["description"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.Description = s.Value
		}
	}
	if v, ok := item["merchant"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.Merchant = s.Value
		}
	}
	if v, ok := item["categoryId"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.CategoryID = &s.Value
		}
	}
	if v, ok := item["suggestedCategoryId"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.SuggestedCategoryID = &s.Value
		}
	}
	if v, ok := item["isConfirmed"]; ok {
		if b, ok := v.(*types.AttributeValueMemberBOOL); ok {
			t.IsConfirmed = b.Value
		}
	}
	if v, ok := item["pending"]; ok {
		if b, ok := v.(*types.AttributeValueMemberBOOL); ok {
			t.Pending = b.Value
		}
	}
	if v, ok := item["paymentMethod"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.PaymentMethod = s.Value
		}
	}
	if v, ok := item["accountId"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.AccountID = s.Value
		}
	}
	if v, ok := item["accountName"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.AccountName = s.Value
		}
	}
	if v, ok := item["rawPayload"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.RawPayload = s.Value
		}
	}
	if v, ok := item["createdAt"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.CreatedAt = s.Value
		}
	}
	if v, ok := item["updatedAt"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.UpdatedAt = s.Value
		}
	}
	if v, ok := item["gsi1pk"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.GSI1PK = s.Value
		}
	}
	if v, ok := item["gsi2pk"]; ok {
		if s, ok := v.(*types.AttributeValueMemberS); ok {
			t.GSI2PK = s.Value
		}
	}
	return t, nil
}
