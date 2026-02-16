package handler

import (
	"net/http"
	"strconv"

	"expense-tally/internal/service"
)

// SyncHandler handles sync HTTP requests.
type SyncHandler struct {
	svc *service.SyncService
}

// NewSyncHandler creates a new SyncHandler.
func NewSyncHandler(svc *service.SyncService) *SyncHandler {
	return &SyncHandler{svc: svc}
}

// Trigger manually triggers a sync.
func (h *SyncHandler) Trigger(w http.ResponseWriter, r *http.Request) {
	if err := h.svc.TriggerSync(r.Context()); err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusOK, map[string]string{"status": "triggered"})
}

// Logs returns sync logs.
func (h *SyncHandler) Logs(w http.ResponseWriter, r *http.Request) {
	source := r.URL.Query().Get("source")
	limit := 20
	if l := r.URL.Query().Get("limit"); l != "" {
		if n, err := strconv.Atoi(l); err == nil && n > 0 {
			limit = n
		}
	}
	logs, err := h.svc.GetLogs(r.Context(), source, limit)
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}
	writeJSON(w, http.StatusOK, logs)
}
