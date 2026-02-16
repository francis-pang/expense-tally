package handler

import (
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/cors"
)

// NewRouter builds and returns the chi router with all routes.
func NewRouter(
	health *HealthHandler,
	categories *CategoriesHandler,
	transactions *TransactionsHandler,
	dashboard *DashboardHandler,
	sync *SyncHandler,
	connections *ConnectionsHandler,
) *chi.Mux {
	r := chi.NewRouter()
	r.Use(middleware.RequestID)
	r.Use(middleware.RealIP)
	r.Use(middleware.Logger)
	r.Use(middleware.Recoverer)
	r.Use(cors.Handler(cors.Options{
		AllowedOrigins:   []string{"*"},
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowedHeaders:   []string{"Accept", "Authorization", "Content-Type"},
		AllowCredentials: false,
		MaxAge:           300,
	}))

	r.Get("/api/health", health.Health)

	r.Route("/api/categories", func(r chi.Router) {
		r.Get("/", categories.List)
		r.Post("/", categories.Create)
		r.Get("/{id}", categories.Get)
		r.Put("/{id}", categories.Update)
		r.Delete("/{id}", categories.Delete)
	})

	r.Route("/api/transactions", func(r chi.Router) {
		r.Get("/", transactions.List)
		r.Post("/", transactions.Create)
		r.Get("/unconfirmed", transactions.ListUnconfirmed)
		r.Route("/{id}", func(r chi.Router) {
			r.Get("/", transactions.Get)
			r.Put("/", transactions.Update)
			r.Delete("/", transactions.Delete)
			r.Put("/confirm", transactions.Confirm)
		})
	})

	r.Get("/api/dashboard", dashboard.Get)

	r.Route("/api/sync", func(r chi.Router) {
		r.Post("/trigger", sync.Trigger)
		r.Get("/logs", sync.Logs)
	})

	r.Route("/api/connections", func(r chi.Router) {
		r.Get("/", connections.List)
		r.Post("/teller", connections.CreateTeller)
	})

	return r
}
