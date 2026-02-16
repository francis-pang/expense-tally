package provider

import (
	"context"

	"expense-tally/internal/model"
)

// ProviderAdapter fetches transactions from a financial provider.
type ProviderAdapter interface {
	FetchTransactions(ctx context.Context, accessToken string, cursor string) ([]model.Transaction, string, error)
}

// NewAdapterFactory returns a factory function that creates provider adapters.
func NewAdapterFactory() func(string) ProviderAdapter {
	return func(provider string) ProviderAdapter {
		switch provider {
		case "simplefin":
			return NewSimpleFINAdapter()
		default:
			return nil
		}
	}
}
