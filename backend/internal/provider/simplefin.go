package provider

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"strconv"
	"strings"
	"time"

	"expense-tally/internal/model"
)

// SimpleFINAdapter implements ProviderAdapter for SimpleFIN Bridge.
type SimpleFINAdapter struct {
	client *http.Client
}

// NewSimpleFINAdapter creates a SimpleFINAdapter.
func NewSimpleFINAdapter() *SimpleFINAdapter {
	return &SimpleFINAdapter{
		client: &http.Client{Timeout: 30 * time.Second},
	}
}

// simpleFINAccountSet is the top-level response from GET /accounts.
type simpleFINAccountSet struct {
	Errors   []string             `json:"errors"`
	Accounts []simpleFINAccount   `json:"accounts"`
}

// simpleFINAccount represents an account in the SimpleFIN response.
type simpleFINAccount struct {
	Org              simpleFINOrg          `json:"org"`
	ID               string                `json:"id"`
	Name             string                `json:"name"`
	Currency         string                `json:"currency"`
	Balance          string                `json:"balance"`
	AvailableBalance string                `json:"available-balance"`
	BalanceDate      int64                 `json:"balance-date"`
	Transactions     []simpleFINTransaction `json:"transactions"`
}

// simpleFINOrg represents the organization (institution) in SimpleFIN.
type simpleFINOrg struct {
	Domain  string `json:"domain"`
	SfinURL string `json:"sfin-url"`
	URL     string `json:"url"`
	Name    string `json:"name"`
	ID      string `json:"id"`
}

// simpleFINTransaction represents a transaction in the SimpleFIN response.
type simpleFINTransaction struct {
	ID           string          `json:"id"`
	Posted       int64           `json:"posted"`
	Amount       string          `json:"amount"`
	Description  string          `json:"description"`
	Payee        string          `json:"payee"`
	Memo         string          `json:"memo"`
	Pending      bool            `json:"pending"`
	TransactedAt int64           `json:"transacted_at"`
	Extra        json.RawMessage `json:"extra,omitempty"`
}

// ClaimAccessURL exchanges a base64-encoded setup token for an access URL.
func (s *SimpleFINAdapter) ClaimAccessURL(ctx context.Context, setupToken string) (string, error) {
	decoded, err := base64.StdEncoding.DecodeString(setupToken)
	if err != nil {
		return "", fmt.Errorf("failed to decode setup token: %w", err)
	}
	claimURL := string(decoded)

	req, err := http.NewRequestWithContext(ctx, http.MethodPost, claimURL, nil)
	if err != nil {
		return "", fmt.Errorf("failed to create claim request: %w", err)
	}
	req.Header.Set("Content-Length", "0")

	resp, err := s.client.Do(req)
	if err != nil {
		return "", fmt.Errorf("failed to claim access URL: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusForbidden {
		return "", fmt.Errorf("setup token already claimed or invalid (403)")
	}
	if resp.StatusCode != http.StatusOK {
		return "", fmt.Errorf("SimpleFIN claim error: HTTP %d", resp.StatusCode)
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", fmt.Errorf("failed to read claim response: %w", err)
	}
	return strings.TrimSpace(string(body)), nil
}

// ListAccounts fetches all accounts (without transactions) from SimpleFIN.
func (s *SimpleFINAdapter) ListAccounts(ctx context.Context, accessURL string) ([]model.SimpleFINAccount, error) {
	accountSet, err := s.fetchAccounts(ctx, accessURL, true, 0, 0)
	if err != nil {
		return nil, err
	}

	var accounts []model.SimpleFINAccount
	for _, acct := range accountSet.Accounts {
		orgName := acct.Org.Name
		if orgName == "" {
			orgName = acct.Org.Domain
		}
		sa := model.SimpleFINAccount{
			ID:              acct.ID,
			Name:            acct.Name,
			Currency:        acct.Currency,
			InstitutionName: orgName,
			OrgDomain:       acct.Org.Domain,
			OrgURL:          acct.Org.URL,
			OrgID:           acct.Org.ID,
		}
		if acct.Balance != "" {
			bal := acct.Balance
			sa.Balance = &bal
		}
		if acct.AvailableBalance != "" {
			avail := acct.AvailableBalance
			sa.AvailableBalance = &avail
		}
		if acct.BalanceDate > 0 {
			bd := time.Unix(acct.BalanceDate, 0).UTC().Format(time.RFC3339)
			sa.BalanceDate = &bd
		}
		accounts = append(accounts, sa)
	}
	return accounts, nil
}

// FetchTransactions fetches transactions from SimpleFIN Bridge.
// The accessToken is the full SimpleFIN access URL (with embedded credentials).
// Cursor format: empty (first sync, fetch all history) or "last_sync_unix_timestamp".
func (s *SimpleFINAdapter) FetchTransactions(ctx context.Context, accessToken string, cursor string) ([]model.Transaction, string, error) {
	var startDate int64
	if cursor != "" {
		parsed, err := strconv.ParseInt(cursor, 10, 64)
		if err == nil {
			startDate = parsed
		}
	}
	// When cursor is empty (first sync), startDate stays 0 and we omit
	// start-date from the API request to fetch all available history.

	accountSet, err := s.fetchAccounts(ctx, accessToken, false, startDate, 0)
	if err != nil {
		return nil, "", err
	}

	var txns []model.Transaction
	now := time.Now().UTC().Format(time.RFC3339)
	latestPosted := startDate

	for _, acct := range accountSet.Accounts {
		orgName := acct.Org.Name
		if orgName == "" {
			orgName = acct.Org.Domain
		}

		for _, st := range acct.Transactions {
			amount, err := strconv.ParseFloat(st.Amount, 64)
			if err != nil {
				continue
			}

			posted := time.Unix(st.Posted, 0).UTC()
			date := posted.Format("2006-01-02")
			year := posted.Format("2006")

			var transactedAt string
			if st.TransactedAt != 0 {
				transactedAt = time.Unix(st.TransactedAt, 0).UTC().Format("2006-01-02")
			}

			rawPayload, _ := json.Marshal(st)

			// Use payee as merchant when available; fall back to institution name.
			merchant := st.Payee
			if merchant == "" {
				merchant = orgName
			}

			pk := "TXN#simplefin#" + acct.ID + "#" + st.ID

			txn := model.Transaction{
				PK:              pk,
				Date:            date,
				TransactedAt:    transactedAt,
				Source:          "simplefin",
				Amount:          amount,
				Currency:        acct.Currency,
				Description:     st.Description,
				Merchant:        merchant,
				Payee:           st.Payee,
				Memo:            st.Memo,
				InstitutionName: orgName,
				InstitutionID:   acct.Org.ID,
				IsConfirmed:     false,
				Pending:         st.Pending,
				PaymentMethod:   "card",
				AccountID:       acct.ID,
				AccountName:     acct.Name,
				RawPayload:      string(rawPayload),
				CreatedAt:       now,
				UpdatedAt:       now,
				GSI1PK:          "YEAR#" + year,
				GSI2PK:          "UNCONFIRMED",
			}
			txns = append(txns, txn)

			if st.Posted > latestPosted {
				latestPosted = st.Posted
			}
		}
	}

	newCursor := strconv.FormatInt(latestPosted, 10)
	return txns, newCursor, nil
}

// fetchAccounts calls the SimpleFIN /accounts endpoint.
func (s *SimpleFINAdapter) fetchAccounts(ctx context.Context, accessURL string, balancesOnly bool, startDate, endDate int64) (*simpleFINAccountSet, error) {
	parsed, err := url.Parse(accessURL)
	if err != nil {
		return nil, fmt.Errorf("invalid access URL: %w", err)
	}

	username := ""
	password := ""
	if parsed.User != nil {
		username = parsed.User.Username()
		password, _ = parsed.User.Password()
	}

	// Build the accounts URL without credentials
	parsed.User = nil
	accountsURL := parsed.String() + "/accounts"

	params := url.Values{}
	if balancesOnly {
		params.Set("balances-only", "1")
	}
	if startDate > 0 {
		params.Set("start-date", strconv.FormatInt(startDate, 10))
	}
	if endDate > 0 {
		params.Set("end-date", strconv.FormatInt(endDate, 10))
	}
	if len(params) > 0 {
		accountsURL += "?" + params.Encode()
	}

	req, err := http.NewRequestWithContext(ctx, http.MethodGet, accountsURL, nil)
	if err != nil {
		return nil, err
	}
	req.SetBasicAuth(username, password)

	resp, err := s.client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("SimpleFIN API error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusForbidden {
		return nil, fmt.Errorf("SimpleFIN access revoked or invalid credentials (403)")
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("SimpleFIN API error: HTTP %d", resp.StatusCode)
	}

	var accountSet simpleFINAccountSet
	if err := json.NewDecoder(resp.Body).Decode(&accountSet); err != nil {
		return nil, fmt.Errorf("failed to decode SimpleFIN response: %w", err)
	}

	return &accountSet, nil
}
