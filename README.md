# Expense Tally

A serverless expense tracking application with automatic bank transaction syncing, category management, and a dashboard for spending insights.

## Architecture

- **Backend**: Go (AWS Lambda + API Gateway), DynamoDB
- **Frontend**: React 18, TypeScript, Vite, Tailwind CSS
- **Auth**: Amazon Cognito (email + OAuth)
- **Infrastructure**: AWS SAM (CloudFormation)
- **Bank Integration**: SimpleFIN Bridge, Teller API

## Project Structure

```
expense-tally/
├── template.yaml          # AWS SAM infrastructure definition
├── samconfig.toml         # SAM deployment configuration
├── Makefile               # Build and deploy orchestration
├── backend/
│   ├── cmd/
│   │   ├── api/main.go    # API Lambda handler
│   │   └── sync/main.go   # Scheduled sync Lambda handler
│   └── internal/
│       ├── handler/       # HTTP route handlers
│       ├── model/         # Domain models
│       ├── repository/    # DynamoDB data access
│       ├── service/       # Business logic
│       └── provider/      # Bank API adapters (SimpleFIN, Teller)
└── frontend/
    └── src/
        ├── pages/         # Dashboard, Transactions, Categories, ReviewQueue, ManualEntry, Login
        ├── components/    # Shared UI components
        ├── services/      # API client
        └── config/        # Amplify / auth configuration
```

## Features

- **Dashboard** - Spending aggregations by category, month, and payment method
- **Transaction Management** - View, edit, and confirm synced transactions
- **Category Management** - Organize transactions with categories and keyword-based auto-suggestions
- **Bank Sync** - Automatic transaction syncing via SimpleFIN Bridge and Teller API (daily scheduled + manual trigger)
- **Review Queue** - Review and confirm unconfirmed transactions
- **Manual Entry** - Add transactions manually

## Database

All data is stored in DynamoDB with the following tables:

| Table | Key | Purpose |
|-------|-----|---------|
| `{env}-expense-transactions` | PK (transaction ID) | Transaction records with GSIs for date range queries and unconfirmed filtering |
| `{env}-expense-categories` | PK (category ID) | Expense categories |
| `{env}-expense-keyword-associations` | PK + categoryId (SK) | Keyword-to-category mappings for auto-categorization |
| `{env}-expense-provider-connections` | PK (connection ID) | Bank provider connections and sync state |
| `{env}-expense-sync-logs` | PK (source) + SK (timestamp) | Sync operation logs |

## Prerequisites

- Go 1.23+
- Node.js 20+
- AWS SAM CLI
- AWS credentials configured

## Development

### Backend

```bash
cd backend
go test ./...
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### Local API (SAM)

```bash
make local
```

## Deployment

```bash
make deploy
```

Or deploy frontend only:

```bash
make deploy-frontend
```

## Documentation

Comprehensive documentation is available in the [`docs/`](docs/) directory:

| Document | Description |
|----------|-------------|
| [Architecture](docs/ARCHITECTURE.md) | System architecture overview with diagrams, component summary, data flow |
| [Backend](docs/BACKEND.md) | Go backend architecture, layered design, domain models, keyword learning |
| [Frontend](docs/FRONTEND.md) | React/TypeScript frontend architecture, UI guidelines, component patterns |
| [Security](docs/SECURITY.md) | Authentication, authorization, transport security, secrets management |
| [Observability](docs/OBSERVABILITY.md) | Logging, monitoring, alerting, tracing recommendations |
| [Developer Guide](docs/DEVELOPER-GUIDE.md) | Setup, coding conventions, Git workflow, development patterns |
| [Deployment](docs/DEPLOYMENT.md) | CI/CD pipelines, SAM infrastructure, deployment procedures |
| [API Reference](docs/API.md) | Full API endpoint reference with request/response examples |
| [ADR-001](docs/adr/001-enrich-api-data-capture.md) | Enrich database to capture full provider API data |

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/health` | Health check |
| GET/POST | `/api/categories` | List / create categories |
| GET/PUT/DELETE | `/api/categories/{id}` | Get / update / delete category |
| GET/POST | `/api/transactions` | List / create transactions |
| GET/PUT/DELETE | `/api/transactions/{id}` | Get / update / delete transaction |
| PUT | `/api/transactions/{id}/confirm` | Confirm a transaction |
| GET | `/api/transactions/unconfirmed` | List unconfirmed transactions |
| GET | `/api/dashboard` | Dashboard aggregations |
| POST | `/api/sync/trigger` | Trigger manual bank sync |
| GET | `/api/sync/logs` | View sync logs |
| GET | `/api/connections` | List bank connections |
| POST | `/api/connections/teller` | Register Teller connection |
