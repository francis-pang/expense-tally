package provider

import (
	"context"

	"expense-tally-v2/internal/model"
)

// ProviderAdapter fetches transactions from a financial provider.
type ProviderAdapter interface {
	FetchTransactions(ctx context.Context, accessToken string, cursor string) ([]model.Transaction, string, error)
}

// NewProviderAdapter returns the adapter for the given provider.
func NewProviderAdapter(provider string) ProviderAdapter {
	switch provider {
	case "teller":
		return &TellerAdapter{}
	default:
		return nil
	}
}
