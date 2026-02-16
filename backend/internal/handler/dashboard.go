package handler

import (
	"fmt"
	"net/http"
	"sort"
	"strconv"

	"expense-tally-v2/internal/model"
	"expense-tally-v2/internal/service"
)

// DashboardHandler handles dashboard HTTP requests.
type DashboardHandler struct {
	txnSvc *service.TransactionService
	catSvc *service.CategoryService
}

// NewDashboardHandler creates a new DashboardHandler.
func NewDashboardHandler(txnSvc *service.TransactionService, catSvc *service.CategoryService) *DashboardHandler {
	return &DashboardHandler{txnSvc: txnSvc, catSvc: catSvc}
}

// Get returns dashboard aggregations.
func (h *DashboardHandler) Get(w http.ResponseWriter, r *http.Request) {
	year := r.URL.Query().Get("year")
	month := r.URL.Query().Get("month")
	if year == "" {
		year = "2026" // default
	}

	var startDate, endDate string
	if month != "" {
		m, _ := strconv.Atoi(month)
		if m >= 1 && m <= 12 {
			startDate = fmt.Sprintf("%s-%02d-01", year, m)
			endDate = fmt.Sprintf("%s-%02d-31", year, m)
		}
	}

	txns, err := h.txnSvc.List(r.Context(), year, startDate, endDate, "", nil)
	if err != nil {
		writeError(w, http.StatusInternalServerError, err.Error())
		return
	}

	// Build category name map
	cats, _ := h.catSvc.List(r.Context())
	catNames := make(map[string]string)
	for _, c := range cats {
		catNames[c.PK] = c.Name
	}

	resp := model.DashboardResponse{
		ByCategory:        []model.CategorySpend{},
		ByMonth:           []model.MonthlySpend{},
		ByPaymentMethod:   []model.PaymentMethodSpend{},
		RecentTransactions: []model.Transaction{},
	}

	catSpend := make(map[string]float64)
	monthSpend := make(map[string]float64)
	pmSpend := make(map[string]float64)
	var totalSpend float64

	for _, t := range txns {
		totalSpend += t.Amount
		if t.CategoryID != nil {
			catSpend[*t.CategoryID] += t.Amount
		}
		monthKey := t.Date[:7]
		if len(t.Date) >= 7 {
			monthSpend[monthKey] += t.Amount
		}
		pm := t.PaymentMethod
		if pm == "" {
			pm = "unknown"
		}
		pmSpend[pm] += t.Amount
	}

	for catID, amt := range catSpend {
		resp.ByCategory = append(resp.ByCategory, model.CategorySpend{
			CategoryID:   catID,
			CategoryName: catNames[catID],
			Total:        amt,
		})
	}
	for m, amt := range monthSpend {
		resp.ByMonth = append(resp.ByMonth, model.MonthlySpend{Month: m, Total: amt})
	}
	for pm, amt := range pmSpend {
		resp.ByPaymentMethod = append(resp.ByPaymentMethod, model.PaymentMethodSpend{Method: pm, Total: amt})
	}

	sort.Slice(resp.ByCategory, func(i, j int) bool {
		return resp.ByCategory[i].Total > resp.ByCategory[j].Total
	})
	sort.Slice(resp.ByMonth, func(i, j int) bool {
		return resp.ByMonth[i].Month > resp.ByMonth[j].Month
	})

	// Recent 10 transactions
	sort.Slice(txns, func(i, j int) bool {
		return txns[i].Date > txns[j].Date || (txns[i].Date == txns[j].Date && txns[i].CreatedAt > txns[j].CreatedAt)
	})
	limit := 10
	if len(txns) < limit {
		limit = len(txns)
	}
	resp.RecentTransactions = txns[:limit]
	resp.TotalSpend = totalSpend

	writeJSON(w, http.StatusOK, resp)
}
