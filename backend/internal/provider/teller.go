package provider

import (
	"context"
	"crypto/tls"
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
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
	ID             string  `json:"id"`
	AccountID      string  `json:"account_id"`
	Amount         string  `json:"amount"`
	Currency       string  `json:"currency"`
	Description    string  `json:"description"`
	Date           string  `json:"date"`
	Status         string  `json:"status"`
	Type           string  `json:"type"`
	RunningBalance *string `json:"running_balance"`
	Details        *struct {
		CounterpartyName string `json:"counterparty_name"`
		CounterpartyType string `json:"counterparty_type"`
		ProcessingStatus string `json:"processing_status"`
		Category         string `json:"category"`
	} `json:"details"`
}

// tellerAccountResponse represents an account from the Teller API response.
type tellerAccountResponse struct {
	ID           string `json:"id"`
	EnrollmentID string `json:"enrollment_id"`
	Name         string `json:"name"`
	Type         string `json:"type"`
	Subtype      string `json:"subtype"`
	Status       string `json:"status"`
	Currency     string `json:"currency"`
	LastFour     string `json:"last_four"`
	Institution  struct {
		ID   string `json:"id"`
		Name string `json:"name"`
	} `json:"institution"`
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

	var raw []tellerAccountResponse
	if err := json.NewDecoder(resp.Body).Decode(&raw); err != nil {
		return nil, err
	}
	accounts := make([]model.TellerAccount, len(raw))
	for i, r := range raw {
		accounts[i] = model.TellerAccount{
			ID:              r.ID,
			EnrollmentID:    r.EnrollmentID,
			Name:            r.Name,
			Type:            r.Type,
			Subtype:         r.Subtype,
			Status:          r.Status,
			Currency:        r.Currency,
			LastFour:        r.LastFour,
			InstitutionID:   r.Institution.ID,
			InstitutionName: r.Institution.Name,
		}
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
		var providerCategory, counterpartyType string
		if tt.Details != nil {
			merchant = tt.Details.CounterpartyName
			providerCategory = tt.Details.Category
			counterpartyType = tt.Details.CounterpartyType
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

		amount, _ := strconv.ParseFloat(tt.Amount, 64)

		rawPayload, _ := json.Marshal(tt)

		pk := "TXN#teller#" + tt.ID
		txn := model.Transaction{
			PK:               pk,
			Date:             date,
			Source:           "teller",
			Amount:           amount,
			Currency:         tt.Currency,
			Description:      tt.Description,
			Merchant:         merchant,
			TransactionType:  tt.Type,
			ProviderCategory: providerCategory,
			CounterpartyType: counterpartyType,
			RunningBalance:   tt.RunningBalance,
			AccountID:        tt.AccountID,
			IsConfirmed:      false,
			Pending:          tt.Status == "pending",
			PaymentMethod:    "card",
			RawPayload:       string(rawPayload),
			CreatedAt:        now,
			UpdatedAt:        now,
			GSI1PK:           "YEAR#" + year,
			GSI2PK:           "UNCONFIRMED",
		}
		txns = append(txns, txn)
	}

	newCursor := accountID
	if len(tellerTxns) > 0 {
		newCursor = accountID + "|" + tellerTxns[len(tellerTxns)-1].ID
	}
	return txns, newCursor, nil
}
