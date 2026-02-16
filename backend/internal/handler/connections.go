package handler

import (
	"encoding/json"
	"fmt"
	"net/http"

	"expense-tally/internal/model"
	"expense-tally/internal/provider"
	"expense-tally/internal/repository"
)

// ConnectionsHandler handles connection HTTP requests.
type ConnectionsHandler struct {
	repo           *repository.ConnectionRepository
	adapterFactory func(string) provider.ProviderAdapter
}

// NewConnectionsHandler creates a new ConnectionsHandler.
func NewConnectionsHandler(repo *repository.ConnectionRepository, adapterFactory func(string) provider.ProviderAdapter) *ConnectionsHandler {
	return &ConnectionsHandler{repo: repo, adapterFactory: adapterFactory}
}

// List returns all connections.
func (h *ConnectionsHandler) List(w http.ResponseWriter, r *http.Request) {
	conns, err := h.repo.List(r.Context())
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusOK, conns)
}

// CreateSimpleFIN creates SimpleFIN connections from a setup token.
// It claims the access URL, lists accounts, and creates a connection per account.
func (h *ConnectionsHandler) CreateSimpleFIN(w http.ResponseWriter, r *http.Request) {
	var req model.SimpleFINSetupRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		writeError(w, http.StatusBadRequest, "invalid request body")
		return
	}
	if req.SetupToken == "" {
		writeError(w, http.StatusBadRequest, "setupToken is required")
		return
	}

	adapter := h.adapterFactory("simplefin")
	if adapter == nil {
		writeError(w, http.StatusInternalServerError, "simplefin adapter not configured")
		return
	}
	sfinAdapter, ok := adapter.(*provider.SimpleFINAdapter)
	if !ok {
		writeError(w, http.StatusInternalServerError, "invalid simplefin adapter")
		return
	}

	// Exchange setup token for access URL
	accessURL, err := sfinAdapter.ClaimAccessURL(r.Context(), req.SetupToken)
	if err != nil {
		writeError(w, http.StatusBadGateway, fmt.Sprintf("failed to claim access URL: %v", err))
		return
	}

	// List accounts to discover what's available
	accounts, err := sfinAdapter.ListAccounts(r.Context(), accessURL)
	if err != nil {
		writeError(w, http.StatusBadGateway, fmt.Sprintf("failed to list accounts from SimpleFIN: %v", err))
		return
	}

	// Create one connection per account, all sharing the same access URL
	var created []model.ProviderConnection
	for _, acct := range accounts {
		pk := "CONN#simplefin#" + acct.ID
		conn := &model.ProviderConnection{
			PK:             pk,
			Provider:       "simplefin",
			AccessTokenRef: accessURL,
		}
		if err := h.repo.Put(r.Context(), conn); err != nil {
			continue
		}
		created = append(created, *conn)
	}
	writeJSON(w, http.StatusCreated, created)
}
