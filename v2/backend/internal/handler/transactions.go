package handler

import (
	"encoding/json"
	"net/http"
	"strconv"

	"expense-tally-v2/internal/model"
	"expense-tally-v2/internal/service"

	"github.com/go-chi/chi/v5"
)

// TransactionsHandler handles transaction HTTP requests.
type TransactionsHandler struct {
	svc *service.TransactionService
}

// NewTransactionsHandler creates a new TransactionsHandler.
func NewTransactionsHandler(svc *service.TransactionService) *TransactionsHandler {
	return &TransactionsHandler{svc: svc}
}

// List returns transactions with optional filters.
func (h *TransactionsHandler) List(w http.ResponseWriter, r *http.Request) {
	year := r.URL.Query().Get("year")
	if year == "" {
		year = "2026" // default to current year
	}
	startDate := r.URL.Query().Get("startDate")
	endDate := r.URL.Query().Get("endDate")
	categoryID := r.URL.Query().Get("categoryId")
	var confirmed *bool
	if c := r.URL.Query().Get("confirmed"); c != "" {
		b, err := strconv.ParseBool(c)
		if err == nil {
			confirmed = &b
		}
	}
	txns, err := h.svc.List(r.Context(), year, startDate, endDate, categoryID, confirmed)
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusOK, txns)
}

// ListUnconfirmed returns unconfirmed transactions for review.
func (h *TransactionsHandler) ListUnconfirmed(w http.ResponseWriter, r *http.Request) {
	startDate := r.URL.Query().Get("startDate")
	endDate := r.URL.Query().Get("endDate")
	txns, err := h.svc.ListUnconfirmed(r.Context(), startDate, endDate)
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusOK, txns)
}

// Get returns a single transaction.
func (h *TransactionsHandler) Get(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "id")
	txn, err := h.svc.Get(r.Context(), id)
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	if txn == nil {
		writeError(w, http.StatusNotFound, "transaction not found")
		return
	}
	writeJSON(w, http.StatusOK, txn)
}

// Create creates a manual transaction.
func (h *TransactionsHandler) Create(w http.ResponseWriter, r *http.Request) {
	var req model.CreateTransactionRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		writeError(w, http.StatusBadRequest, "invalid request body")
		return
	}
	txn, err := h.svc.CreateManual(r.Context(), req)
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusCreated, txn)
}

// Update updates a transaction.
func (h *TransactionsHandler) Update(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "id")
	var req model.UpdateTransactionRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		writeError(w, http.StatusBadRequest, "invalid request body")
		return
	}
	txn, err := h.svc.Update(r.Context(), id, req)
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusOK, txn)
}

// Delete deletes a transaction.
func (h *TransactionsHandler) Delete(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "id")
	if err := h.svc.Delete(r.Context(), id); err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

// Confirm marks a transaction as confirmed.
func (h *TransactionsHandler) Confirm(w http.ResponseWriter, r *http.Request) {
	id := chi.URLParam(r, "id")
	txn, err := h.svc.Confirm(r.Context(), id)
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusOK, txn)
}
