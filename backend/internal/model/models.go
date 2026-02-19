package model

// Transaction represents a financial transaction.
type Transaction struct {
	PK                  string  `json:"id"`
	Date                string  `json:"date"`
	TransactedAt        string  `json:"transactedAt,omitempty"`
	Source              string  `json:"source"`
	Amount              float64 `json:"amount"`
	Currency            string  `json:"currency"`
	Description         string  `json:"description"`
	Merchant            string  `json:"merchant"`
	Payee               string  `json:"payee,omitempty"`
	Memo                string  `json:"memo,omitempty"`
	TransactionType     string  `json:"transactionType,omitempty"`
	ProviderCategory    string  `json:"providerCategory,omitempty"`
	CounterpartyType    string  `json:"counterpartyType,omitempty"`
	RunningBalance      *string `json:"runningBalance,omitempty"`
	InstitutionName     string  `json:"institutionName,omitempty"`
	InstitutionID       string  `json:"institutionId,omitempty"`
	CategoryID          *string `json:"categoryId,omitempty"`
	SuggestedCategoryID *string `json:"suggestedCategoryId,omitempty"`
	IsConfirmed         bool    `json:"isConfirmed"`
	Pending             bool    `json:"pending"`
	PaymentMethod       string  `json:"paymentMethod"`
	AccountID           string  `json:"accountId,omitempty"`
	AccountName         string  `json:"accountName,omitempty"`
	RawPayload          string  `json:"rawPayload,omitempty"`
	CreatedAt           string  `json:"createdAt"`
	UpdatedAt           string  `json:"updatedAt"`
	GSI1PK              string  `json:"-"` // YEAR#2026
	GSI2PK              string  `json:"-"` // UNCONFIRMED (sparse)
}

// Category represents an expense category.
type Category struct {
	PK       string `json:"id"`
	Name     string `json:"name"`
	ParentID *string `json:"parentId,omitempty"`
}

// KeywordAssociation links a keyword to a category with frequency.
type KeywordAssociation struct {
	PK         string `json:"pk"`
	CategoryID string `json:"categoryId"`
	Frequency  int    `json:"frequency"`
	LastSeenAt string `json:"lastSeenAt"`
}

// ProviderConnection represents a bank connection.
type ProviderConnection struct {
	PK               string  `json:"id"`
	Provider         string  `json:"provider"`
	AccessTokenRef   string  `json:"accessTokenRef"`
	SyncCursor       string  `json:"syncCursor,omitempty"`
	LastSyncedAt     *string `json:"lastSyncedAt,omitempty"`
	AccountName      string  `json:"accountName,omitempty"`
	AccountType      string  `json:"accountType,omitempty"`
	AccountSubtype   string  `json:"accountSubtype,omitempty"`
	InstitutionName  string  `json:"institutionName,omitempty"`
	InstitutionID    string  `json:"institutionId,omitempty"`
	Currency         string  `json:"currency,omitempty"`
	LastFour         string  `json:"lastFour,omitempty"`
	Balance          *string `json:"balance,omitempty"`
	AvailableBalance *string `json:"availableBalance,omitempty"`
	BalanceUpdatedAt *string `json:"balanceUpdatedAt,omitempty"`
	Status           string  `json:"status,omitempty"`
}

// SyncLog represents a sync run log.
type SyncLog struct {
	PK               string `json:"source"`
	SK               string `json:"timestamp"`
	Status           string `json:"status"`
	TransactionCount int    `json:"transactionCount"`
	ErrorMessage     string `json:"errorMessage,omitempty"`
}

// CreateTransactionRequest for manual cash entry.
type CreateTransactionRequest struct {
	Date          string  `json:"date"`
	Amount        float64 `json:"amount"`
	Currency      string  `json:"currency"`
	Description   string  `json:"description"`
	Merchant      string  `json:"merchant"`
	CategoryID    *string `json:"categoryId,omitempty"`
	PaymentMethod string  `json:"paymentMethod"`
}

// UpdateTransactionRequest for updating a transaction.
type UpdateTransactionRequest struct {
	CategoryID *string `json:"categoryId,omitempty"`
}

// CreateCategoryRequest for creating a category.
type CreateCategoryRequest struct {
	Name     string  `json:"name"`
	ParentID *string `json:"parentId,omitempty"`
}

// UpdateCategoryRequest for updating a category.
type UpdateCategoryRequest struct {
	Name     string  `json:"name,omitempty"`
	ParentID *string `json:"parentId,omitempty"`
}

// CategorySpend represents spend for a category.
type CategorySpend struct {
	CategoryID   string  `json:"categoryId"`
	CategoryName string  `json:"categoryName"`
	Total        float64 `json:"total"`
}

// MonthlySpend represents spend for a month.
type MonthlySpend struct {
	Month string  `json:"month"`
	Total float64 `json:"total"`
}

// PaymentMethodSpend represents spend by payment method.
type PaymentMethodSpend struct {
	Method string  `json:"method"`
	Total  float64 `json:"total"`
}

// DashboardResponse for GET /api/dashboard.
type DashboardResponse struct {
	TotalSpend       float64             `json:"totalSpend"`
	ByCategory       []CategorySpend     `json:"byCategory"`
	ByMonth          []MonthlySpend      `json:"byMonth"`
	ByPaymentMethod  []PaymentMethodSpend `json:"byPaymentMethod"`
	RecentTransactions []Transaction     `json:"recentTransactions"`
}

// SimpleFINSetupRequest for POST /api/connections/simplefin.
type SimpleFINSetupRequest struct {
	SetupToken string `json:"setupToken"`
}

// SimpleFINAccount represents an account from SimpleFIN Bridge.
type SimpleFINAccount struct {
	ID               string  `json:"id"`
	Name             string  `json:"name"`
	Currency         string  `json:"currency"`
	InstitutionName  string  `json:"institutionName"`
	Balance          *string `json:"balance,omitempty"`
	AvailableBalance *string `json:"availableBalance,omitempty"`
	BalanceDate      *string `json:"balanceDate,omitempty"`
	OrgDomain        string  `json:"orgDomain,omitempty"`
	OrgURL           string  `json:"orgUrl,omitempty"`
	OrgID            string  `json:"orgId,omitempty"`
}

// TellerAccount represents an account from Teller API.
type TellerAccount struct {
	ID              string `json:"id"`
	EnrollmentID    string `json:"enrollment_id"`
	Name            string `json:"name"`
	Type            string `json:"type"`
	Subtype         string `json:"subtype"`
	Status          string `json:"status"`
	Currency        string `json:"currency"`
	LastFour        string `json:"last_four"`
	InstitutionID   string `json:"institution_id,omitempty"`
	InstitutionName string `json:"institution_name,omitempty"`
}
