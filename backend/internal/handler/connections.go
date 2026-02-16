package handler

import (
	"encoding/json"
	"net/http"

	"expense-tally-v2/internal/model"
	"expense-tally-v2/internal/repository"
)

// ConnectionsHandler handles connection HTTP requests.
type ConnectionsHandler struct {
	repo *repository.ConnectionRepository
}

// NewConnectionsHandler creates a new ConnectionsHandler.
func NewConnectionsHandler(repo *repository.ConnectionRepository) *ConnectionsHandler {
	return &ConnectionsHandler{repo: repo}
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

// CreateTeller creates a Teller connection.
func (h *ConnectionsHandler) CreateTeller(w http.ResponseWriter, r *http.Request) {
	var req model.TellerEnrollRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		writeError(w, http.StatusBadRequest, "invalid request body")
		return
	}
	if req.AccountID == "" || req.AccessToken == "" {
		writeError(w, http.StatusBadRequest, "accountId and accessToken required")
		return
	}
	// Store access token in Secrets Manager and save ARN in connection
	// For now, use a placeholder - SAM template would set up Secrets Manager
	// accessTokenRef could be the Secrets Manager ARN after storing the token
	secretARN := "arn:aws:secretsmanager:us-east-1:123456789012:secret:placeholder"
	// In production: create secret, get ARN
	_ = secretARN

	pk := "CONN#teller#" + req.AccountID
	conn := &model.ProviderConnection{
		PK:             pk,
		Provider:       "teller",
		AccessTokenRef: req.AccessToken, // Simplified: store token directly for dev; use Secrets Manager ARN in prod
		SyncCursor:     req.AccountID,  // Initial cursor contains account_id for Teller
	}
	if err := h.repo.Put(r.Context(), conn); err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusCreated, conn)
}
