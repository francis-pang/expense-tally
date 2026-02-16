# Backend Architecture

The backend is a Go application deployed as AWS Lambda functions behind API Gateway. It follows a clean layered architecture with dependency injection wired at startup.

## Technology Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Go | 1.23 | Application language |
| Chi | v5.2.5 | HTTP router and middleware |
| AWS Lambda Go | v1.52.0 | Lambda runtime adapter |
| AWS SDK Go v2 | v1.41.1 | AWS service clients |
| aws-lambda-go-api-proxy | v0.16.2 | Lambda-to-Chi adapter |
| google/uuid | v1.6.0 | UUID generation |

## Package Structure

```
backend/
├── cmd/
│   ├── api/main.go              # API Lambda entry point
│   └── sync/main.go             # Scheduled sync Lambda entry point
├── internal/
│   ├── handler/                  # HTTP request handlers
│   │   ├── router.go            # Route definitions and middleware
│   │   ├── util.go              # Response/error helpers
│   │   ├── health.go            # Health check endpoint
│   │   ├── categories.go        # Category CRUD handlers
│   │   ├── transactions.go      # Transaction CRUD handlers
│   │   ├── dashboard.go         # Dashboard aggregation handler
│   │   ├── sync.go              # Manual sync trigger + logs
│   │   └── connections.go       # Bank connection management
│   ├── model/                    # Domain models and DTOs
│   │   └── models.go            # All struct definitions
│   ├── repository/               # Data access layer (DynamoDB)
│   │   ├── dynamo.go            # DynamoDB client initialization
│   │   ├── transaction.go       # Transaction table operations
│   │   ├── category.go          # Category table operations
│   │   ├── keyword.go           # Keyword association operations
│   │   ├── connection.go        # Provider connection operations
│   │   └── synclog.go           # Sync log operations
│   ├── service/                  # Business logic layer
│   │   ├── transaction.go       # Transaction business rules
│   │   ├── category.go          # Category business rules
│   │   ├── keyword.go           # Keyword extraction + suggestion
│   │   ├── sync.go              # Sync orchestration
│   │   └── time.go              # Time formatting utilities
│   └── provider/                 # External provider adapters
│       ├── adapter.go           # ProviderAdapter interface + factory
│       └── teller.go            # Teller API implementation
├── go.mod
├── go.sum
└── Makefile
```

## Layered Architecture

```
┌────────────────────────────────────────────────────────┐
│                  cmd/api/main.go (init)                 │
│         Wires all dependencies at cold start           │
└──────────────────────┬─────────────────────────────────┘
                       │ constructs
                       ▼
┌────────────────────────────────────────────────────────┐
│                  Handler Layer                          │
│                                                        │
│  Responsibilities:                                     │
│  - Parse HTTP request (path params, query, body)       │
│  - Validate input                                      │
│  - Call service methods                                │
│  - Serialize JSON response                             │
│  - Return appropriate HTTP status codes                │
│                                                        │
│  Handlers:                                             │
│  HealthHandler, CategoriesHandler, TransactionsHandler, │
│  DashboardHandler, SyncHandler, ConnectionsHandler     │
└──────────────────────┬─────────────────────────────────┘
                       │ calls
                       ▼
┌────────────────────────────────────────────────────────┐
│                  Service Layer                          │
│                                                        │
│  Responsibilities:                                     │
│  - Business logic and validation rules                 │
│  - Cross-entity orchestration                          │
│  - Keyword extraction and category suggestion          │
│  - Sync coordination across providers                  │
│                                                        │
│  Services:                                             │
│  TransactionService, CategoryService, KeywordService,  │
│  SyncService                                           │
└──────────────────────┬─────────────────────────────────┘
                       │ calls
                       ▼
┌────────────────────────────────────────────────────────┐
│                  Repository Layer                       │
│                                                        │
│  Responsibilities:                                     │
│  - DynamoDB CRUD operations                            │
│  - Query construction (key conditions, filters)        │
│  - Marshalling/unmarshalling DynamoDB items             │
│  - GSI queries                                         │
│                                                        │
│  Repositories:                                         │
│  TransactionRepository, CategoryRepository,            │
│  KeywordRepository, ConnectionRepository,              │
│  SyncLogRepository                                     │
└──────────────────────┬─────────────────────────────────┘
                       │ uses
                       ▼
┌────────────────────────────────────────────────────────┐
│                  AWS SDK / DynamoDB                     │
└────────────────────────────────────────────────────────┘
```

## Lambda Functions

### API Lambda (`cmd/api/main.go`)

The API Lambda handles all HTTP requests via a Chi router wrapped by `aws-lambda-go-api-proxy`:

```
Lambda invocation → ChiLambda proxy → Chi Router → Handler → Service → Repository → DynamoDB
```

- **Initialization**: Dependencies are wired in `init()` to run once during cold start
- **Runtime**: `provided.al2023` (Amazon Linux 2023, custom runtime)
- **Architecture**: `arm64` (AWS Graviton for cost efficiency)
- **Memory**: 256 MB
- **Timeout**: 30 seconds
- **Events**: API Gateway HTTP API (catch-all `/api/{proxy+}`)

### Sync Lambda (`cmd/sync/main.go`)

The Sync Lambda runs on a daily schedule to pull transactions from bank providers:

```
EventBridge (rate 1 day) → Lambda → SyncService.SyncAll() → Provider APIs → DynamoDB
```

- **Trigger**: EventBridge schedule rule (`rate(1 day)`)
- **Process**: Iterates all active connections, fetches new transactions, applies keyword suggestions, stores in DynamoDB
- **Additional IAM**: `secretsmanager:GetSecretValue` for future Secrets Manager integration

## Middleware Stack

The Chi router applies the following middleware in order:

| Middleware | Purpose |
|-----------|---------|
| `middleware.RequestID` | Assigns unique request ID to each request |
| `middleware.RealIP` | Extracts real client IP from proxy headers |
| `middleware.Logger` | Logs request method, path, status, and duration |
| `middleware.Recoverer` | Recovers from panics and returns 500 |
| `cors.Handler` | CORS policy (all origins, standard methods, auth headers) |

## Domain Models

### Core Entities

| Model | Key Pattern | Description |
|-------|-------------|-------------|
| `Transaction` | `TXN#{source}#{id}` | Financial transaction with category, confirmation status |
| `Category` | `CAT#{uuid}` | Expense category with optional parent (hierarchy) |
| `KeywordAssociation` | `KW#{keyword}` + `categoryId` | Keyword-to-category mapping with frequency |
| `ProviderConnection` | `CONN#{provider}#{accountId}` | Bank connection with sync cursor |
| `SyncLog` | `SYNC#{source}` + `{timestamp}` | Sync operation audit log |

### Request/Response DTOs

| DTO | Used By |
|-----|---------|
| `CreateTransactionRequest` | POST `/api/transactions` |
| `UpdateTransactionRequest` | PUT `/api/transactions/{id}` |
| `CreateCategoryRequest` | POST `/api/categories` |
| `UpdateCategoryRequest` | PUT `/api/categories/{id}` |
| `TellerEnrollRequest` | POST `/api/connections/teller` |
| `DashboardResponse` | GET `/api/dashboard` |
| `CategorySpend`, `MonthlySpend`, `PaymentMethodSpend` | Nested in `DashboardResponse` |

## Keyword Learning System

The keyword service implements an ML-lite auto-categorization system:

```
1. User tags a transaction with a category (PUT /api/transactions/{id})
2. KeywordService.LearnFromTagging() tokenizes description + merchant
3. Each keyword gets a KW#{keyword} entry with categoryId and frequency
4. Frequency is atomically incremented per keyword-category pair

When new transactions arrive (sync or manual):
1. KeywordService.SuggestCategory() tokenizes the description + merchant
2. BatchGetByKeywords fetches all keyword associations
3. Scores are summed across all keywords for each candidate category
4. The category with the highest aggregate score is suggested
```

### Tokenization Rules

- Extract alphanumeric words using regex `[a-zA-Z0-9]+`
- Convert to lowercase
- Filter out words shorter than 3 characters
- Deduplicate

## Provider Adapter Pattern

Bank integrations follow the adapter pattern:

```go
type ProviderAdapter interface {
    FetchTransactions(ctx context.Context, accessToken string, cursor string) ([]model.Transaction, string, error)
}
```

- **Factory function**: `NewProviderAdapter(provider string)` returns the appropriate adapter
- **Current adapters**: Teller (`teller.go`)
- **Extensibility**: Add new providers by implementing the interface and registering in the factory

## Error Handling

Errors are handled per-handler using a shared utility:

```go
func writeError(w http.ResponseWriter, status int, message string)
```

- Returns JSON: `{"error": "<message>"}`
- Standard HTTP status codes: 400 (bad request), 404 (not found), 500 (internal error)
- No global error middleware; each handler manages its own error responses

## Configuration

All configuration is via environment variables injected by SAM/CloudFormation:

| Variable | Lambda | Description |
|----------|--------|-------------|
| `TRANSACTIONS_TABLE` | API, Sync | DynamoDB transactions table name |
| `CATEGORIES_TABLE` | API, Sync | DynamoDB categories table name |
| `KEYWORD_ASSOCIATIONS_TABLE` | API, Sync | DynamoDB keyword associations table name |
| `PROVIDER_CONNECTIONS_TABLE` | API, Sync | DynamoDB provider connections table name |
| `SYNC_LOGS_TABLE` | API, Sync | DynamoDB sync logs table name |
| `COGNITO_USER_POOL_ID` | API | Cognito User Pool ID (for validation) |

## Build Process

The backend builds as a single static binary for Linux arm64:

```bash
GOOS=linux GOARCH=arm64 go build -tags lambda.norpc -o bootstrap ./cmd/api
```

SAM uses a Makefile-based build:

```makefile
build-ApiFunction:
    GOOS=linux GOARCH=arm64 go build -tags lambda.norpc -o $(ARTIFACTS_DIR)/bootstrap ./cmd/api

build-SyncFunction:
    GOOS=linux GOARCH=arm64 go build -tags lambda.norpc -o $(ARTIFACTS_DIR)/bootstrap ./cmd/sync
```

## Related Documentation

- [Architecture Overview](ARCHITECTURE.md)
- [API Reference](API.md)
- [Security](SECURITY.md)
- [Developer Guide](DEVELOPER-GUIDE.md)
