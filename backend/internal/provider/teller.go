package provider

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
	"time"

	"expense-tally-v2/internal/model"
)

const tellerBaseURL = "https://api.teller.io"

// TellerAdapter implements ProviderAdapter for Teller API.
type TellerAdapter struct {
	client *http.Client
}

// TellerTransaction represents a transaction from Teller API.
type TellerTransaction struct {
	ID          string  `json:"id"`
	Amount      float64 `json:"amount"`
	Currency    string  `json:"currency"`
	Description string  `json:"description"`
	Date        string  `json:"date"`
	Details     *struct {
		CounterpartyName string `json:"counterparty_name"`
	} `json:"details"`
}

func (t *TellerAdapter) getClient() *http.Client {
	if t.client == nil {
		t.client = &http.Client{Timeout: 30 * time.Second}
	}
	return t.client
}

// FetchTransactions fetches transactions from Teller API.
func (t *TellerAdapter) FetchTransactions(ctx context.Context, accessToken string, cursor string) ([]model.Transaction, string, error) {
	// Teller API: GET /accounts/{account_id}/transactions
	// We need account_id - for now we'll need it from the connection
	// The connection PK is CONN#provider#account_id, so we can extract account_id
	// But we don't have it here - the adapter receives accessToken and cursor
	// The connection has the full context. We need to pass account_id to the adapter.
	// Let me check the interface - it only has accessToken and cursor.
	// We could encode account_id in the cursor, or we need to change the interface.
	// For Teller, the URL is /accounts/{account_id}/transactions. So we need account_id.
	// The SyncConnection has conn - we could pass account_id. Let me add it to the adapter.
	// Actually, a simpler approach: the cursor could contain the account_id for the first request,
	// or we add a method that takes connection. For now, let's assume the cursor format is
	// "account_id" or "account_id|last_id" for pagination.
	parts := strings.SplitN(cursor, "|", 2)
	accountID := parts[0]
	if accountID == "" {
		return nil, "", fmt.Errorf("account_id required in cursor for Teller")
	}

	url := tellerBaseURL + "/accounts/" + accountID + "/transactions"
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		return nil, "", err
	}
	req.Header.Set("Authorization", "Bearer "+accessToken)

	resp, err := t.getClient().Do(req)
	if err != nil {
		return nil, "", err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, "", fmt.Errorf("teller API error: %d", resp.StatusCode)
	}

	var tellerTxns []TellerTransaction
	if err := json.NewDecoder(resp.Body).Decode(&tellerTxns); err != nil {
		return nil, "", err
	}

	var txns []model.Transaction
	now := time.Now().UTC().Format(time.RFC3339)
	for _, tt := range tellerTxns {
		merchant := ""
		if tt.Details != nil {
			merchant = tt.Details.CounterpartyName
		}
		date := tt.Date
		if len(date) > 10 {
			date = date[:10]
		}
		if date == "" {
			date = time.Now().Format("2006-01-02")
		}
		year := time.Now().Format("2006")
		if len(date) >= 4 {
			year = date[:4]
		}
		pk := "TXN#teller#" + tt.ID
		txn := model.Transaction{
			PK:            pk,
			Date:          date,
			Source:        "teller",
			Amount:        tt.Amount,
			Currency:      tt.Currency,
			Description:   tt.Description,
			Merchant:      merchant,
			IsConfirmed:   false,
			PaymentMethod: "card",
			RawPayload:    "",
			CreatedAt:     now,
			UpdatedAt:     now,
			GSI1PK:        "YEAR#" + year,
			GSI2PK:        "UNCONFIRMED",
		}
		txns = append(txns, txn)
	}

	newCursor := accountID
	if len(tellerTxns) > 0 {
		newCursor = accountID + "|" + tellerTxns[len(tellerTxns)-1].ID
	}
	return txns, newCursor, nil
}
