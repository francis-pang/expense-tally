package provider

import (
	"context"
	"crypto/tls"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
	"time"

	"expense-tally/internal/model"
)

const tellerBaseURL = "https://api.teller.io"

// TellerAdapter implements ProviderAdapter for Teller API.
// Teller requires mTLS (mutual TLS) for all API calls in development and production.
type TellerAdapter struct {
	client   *http.Client
	certFile string
	keyFile  string
}

// NewTellerAdapter creates a TellerAdapter configured with mTLS client certificate paths.
func NewTellerAdapter(certFile, keyFile string) *TellerAdapter {
	return &TellerAdapter{certFile: certFile, keyFile: keyFile}
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

func (t *TellerAdapter) getClient() (*http.Client, error) {
	if t.client != nil {
		return t.client, nil
	}
	if t.certFile != "" && t.keyFile != "" {
		cert, err := tls.LoadX509KeyPair(t.certFile, t.keyFile)
		if err != nil {
			return nil, fmt.Errorf("failed to load Teller client certificate: %w", err)
		}
		t.client = &http.Client{
			Timeout: 30 * time.Second,
			Transport: &http.Transport{
				TLSClientConfig: &tls.Config{
					Certificates: []tls.Certificate{cert},
				},
			},
		}
	} else {
		// Sandbox mode: mTLS is optional
		t.client = &http.Client{Timeout: 30 * time.Second}
	}
	return t.client, nil
}

// ListAccounts fetches all accounts for the given access token from Teller API.
func (t *TellerAdapter) ListAccounts(ctx context.Context, accessToken string) ([]model.TellerAccount, error) {
	url := tellerBaseURL + "/accounts"
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}
	req.SetBasicAuth(accessToken, "")

	client, err := t.getClient()
	if err != nil {
		return nil, err
	}
	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("teller API error listing accounts: %d", resp.StatusCode)
	}

	var accounts []model.TellerAccount
	if err := json.NewDecoder(resp.Body).Decode(&accounts); err != nil {
		return nil, err
	}
	return accounts, nil
}

// FetchTransactions fetches transactions from Teller API.
// Cursor format: "account_id" or "account_id|last_txn_id" for pagination.
func (t *TellerAdapter) FetchTransactions(ctx context.Context, accessToken string, cursor string) ([]model.Transaction, string, error) {
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
	// Teller uses HTTP Basic Auth: access token as username, empty password
	req.SetBasicAuth(accessToken, "")

	client, err := t.getClient()
	if err != nil {
		return nil, "", err
	}
	resp, err := client.Do(req)
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
