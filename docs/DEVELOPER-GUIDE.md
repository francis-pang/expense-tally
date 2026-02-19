# Developer Guide

This document covers development setup, coding conventions, project rules, and the development workflow for Expense Tally v2.

## Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| Go | 1.23+ | Backend development |
| Node.js | 20+ | Frontend development |
| npm | (bundled with Node) | Package management |
| AWS SAM CLI | Latest | Local Lambda testing, deployment |
| AWS CLI | v2 | AWS operations, frontend deployment |
| Git | Latest | Version control |

## Getting Started

### 1. Clone and Checkout

```bash
git clone <repo-url>
cd expense-tally
git checkout feature/v2
```

### 2. Backend Setup

```bash
cd backend
go mod download
go test ./...
```

### 3. Frontend Setup

```bash
cd frontend
npm install
cp .env.example .env
# Edit .env with your Cognito values (from CloudFormation outputs)
npm run dev
```

### 4. Local API (SAM)

```bash
# From project root
make local
# API available at http://localhost:3000/api/*
```

The frontend dev server (Vite) proxies `/api` to `http://localhost:3000`, so `npm run dev` and `make local` together provide a full local development environment.

### 5. Full Stack Local Development

Open two terminals:

```bash
# Terminal 1: Backend (SAM local)
make local

# Terminal 2: Frontend (Vite dev server)
cd frontend && npm run dev
```

Access the app at `http://localhost:5173`.

## Project Structure Conventions

### Backend (Go)

```
backend/
├── cmd/{lambda}/main.go    # One entry point per Lambda function
└── internal/               # Private application code
    ├── handler/            # HTTP handlers (one file per domain)
    ├── model/              # Domain models and DTOs (all in models.go)
    ├── repository/         # DynamoDB access (one file per table)
    ├── service/            # Business logic (one file per domain)
    └── provider/           # External API adapters
```

### Frontend (React/TypeScript)

```
frontend/src/
├── pages/              # One file per route/page
├── components/         # Shared/reusable UI components
├── hooks/              # Custom React hooks
├── services/           # API client functions
├── types/              # TypeScript interfaces
└── config/             # Configuration (Amplify, etc.)
```

## Coding Conventions

### Go Conventions

#### Package Naming
- Use short, lowercase, single-word package names
- Package names match their directory: `handler`, `service`, `repository`, `model`, `provider`

#### File Naming
- One file per domain/entity in each package: `categories.go`, `transactions.go`, `sync.go`
- Helper/utility files: `util.go`, `time.go`
- Interface + factory: `adapter.go`

#### Struct Naming
- Handlers: `{Domain}Handler` (e.g., `CategoriesHandler`, `TransactionsHandler`)
- Services: `{Domain}Service` (e.g., `TransactionService`, `SyncService`)
- Repositories: `{Domain}Repository` (e.g., `TransactionRepository`)
- Models: Plain domain name (e.g., `Transaction`, `Category`)
- Request DTOs: `{Action}{Entity}Request` (e.g., `CreateTransactionRequest`)

#### Constructor Pattern
- All structs use `New{Type}` constructors with explicit dependency injection
- No global state; dependencies are passed as constructor arguments

```go
func NewTransactionService(
    txnRepo *repository.TransactionRepository,
    catRepo *repository.CategoryRepository,
    keywordSvc *KeywordService,
) *TransactionService { ... }
```

#### Error Handling
- Return `error` as the last return value
- Use `writeError(w, statusCode, message)` in handlers
- Log fatal errors only in `main.go` / `init()`
- Service/repository errors propagate up to handlers

#### JSON Tags
- Use `json:"camelCase"` for API-facing fields
- Use `json:"-"` for DynamoDB-internal fields (GSI keys)
- Use `omitempty` for optional/nullable fields

#### Comments
- Exported types and functions must have a Go doc comment
- Comment format: `// TypeName does something.`

### TypeScript/React Conventions

#### File Naming
- Components: PascalCase (`Dashboard.tsx`, `TransactionRow.tsx`)
- Hooks: camelCase with `use` prefix (`useAuth.ts`)
- Services: camelCase (`api.ts`)
- Types: `index.ts` in types directory

#### Component Patterns
- Use **function components** exclusively (no class components)
- Use **named exports** (`export function Dashboard()`, not default exports)
- Pages own their data fetching (`useEffect` + `useState`)
- Pass callbacks for child-to-parent communication (`onUpdate`, etc.)

#### Imports
- Use path alias: `@/components/Layout` instead of `../../components/Layout`
- Group imports: React → third-party → app imports (components, hooks, services, types)

#### Styling
- Use Tailwind CSS utility classes directly in JSX
- No CSS modules, styled-components, or inline style objects
- Follow the component patterns documented in [Frontend Architecture](FRONTEND.md)

#### TypeScript
- Define interfaces in `types/index.ts` for shared types
- Use `type` imports for type-only imports
- Prefer `interface` for object shapes, `type` for unions/intersections
- Mark nullable fields as `string | null` (not optional `?`)

## Git Conventions

### Branch Strategy

| Branch | Purpose | Deploys To |
|--------|---------|------------|
| `feature/v2` | Active v2 development | `dev` environment |
| `main` | Production releases | `prod` environment |
| `master` | Legacy v1 (Maven-based) | N/A for v2 |

### Branching Model

```
main (prod)
  │
  └── feature/v2 (dev)
        │
        ├── feature/v2-some-feature (development branches)
        └── fix/v2-some-bug
```

For v2 development:
1. Branch from `feature/v2`
2. Make changes
3. PR back to `feature/v2`
4. When ready for production, PR `feature/v2` → `main`

### Commit Messages

Follow conventional commit format:

```
<type>: <short description>

[optional body with more detail]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code change that neither fixes a bug nor adds a feature
- `docs`: Documentation changes
- `test`: Adding or updating tests
- `chore`: Build, CI, or tooling changes

### Pull Requests

- PRs from `feature/v2` or development branches target `feature/v2`
- PRs for production release target `main`
- Include a description of what changed and why
- CI must pass (build + test) before merging

## Development Workflow

### Adding a New API Endpoint

1. **Model**: Add request/response structs to `model/models.go`
2. **Repository**: Add data access methods to the appropriate repository file
3. **Service**: Add business logic to the appropriate service file
4. **Handler**: Create handler method, parse request, call service, write response
5. **Router**: Register the route in `handler/router.go`
6. **Frontend types**: Add TypeScript interface to `types/index.ts`
7. **API service**: Add API function to `services/api.ts`
8. **UI**: Create or update page/component

### Adding a New Page (Frontend)

1. Create `pages/NewPage.tsx` with named export
2. Add route in `App.tsx` as child of the Layout route
3. Add navigation item in `Layout.tsx` `navItems` array
4. Import appropriate Heroicons (outline + solid variants)

### Adding a New Bank Provider

1. Implement `ProviderAdapter` interface in `provider/newprovider.go`
2. Define provider-specific API response structs in the adapter file (see `simplefin.go` or `teller.go` for examples)
3. Map all useful API fields to `model.Transaction` -- populate provider-specific fields like `TransactionType`, `ProviderCategory`, etc. where available, and always serialize the full API response as `RawPayload`
4. If the provider has account-level data, add a corresponding model struct (e.g., `model.TellerAccount`) and populate enriched `ProviderConnection` fields
5. Register in `NewAdapterFactory()` factory function in `adapter.go`
6. Create connection handler in `connections.go` (e.g., `CreateNewProvider`)
7. Add route in `router.go`
8. Frontend: Add connection creation UI

See [ADR-001](adr/001-enrich-api-data-capture.md) for the field mapping conventions used by existing providers.

### Adding a New DynamoDB Table

1. Define table in `template.yaml` under Resources
2. Add table name to Lambda environment variables in `template.yaml` Globals
3. Create repository file in `repository/`
4. Read table name from environment variable in `cmd/api/main.go`
5. Wire repository into relevant services

## Testing

### Backend Tests

```bash
cd backend
go test ./...           # Run all tests
go test ./internal/service/...  # Run specific package tests
go test -v ./...        # Verbose output
go test -cover ./...    # Coverage report
```

### Frontend Tests

```bash
cd frontend
npm run build           # TypeScript compilation check
# Note: No test runner is currently configured.
# Consider adding Vitest for unit/component tests.
```

### Local Integration Testing

```bash
make local              # Start SAM local API
cd frontend && npm run dev  # Start Vite dev server
# Test via browser at http://localhost:5173
```

## Build Commands

### Root Makefile

| Command | Description |
|---------|-------------|
| `make build` | SAM build (compiles backend Lambdas) |
| `make deploy` | SAM build + deploy to AWS |
| `make deploy-frontend` | Build frontend, sync to S3, invalidate CloudFront |
| `make local` | Start SAM local API (http://localhost:3000) |
| `make clean` | Remove `.aws-sam` and `backend/bootstrap` |

### Backend Makefile

| Command | Description |
|---------|-------------|
| `make build-api` | Build API Lambda binary |
| `make build-sync` | Build Sync Lambda binary |
| `make test` | Run Go tests |
| `make build-ApiFunction` | SAM build target for API |
| `make build-SyncFunction` | SAM build target for Sync |

### Frontend Commands

| Command | Description |
|---------|-------------|
| `npm run dev` | Start Vite dev server (http://localhost:5173) |
| `npm run build` | Production build (type-check + bundle) |
| `npm run preview` | Preview production build locally |

## Environment Variables

### Frontend (`.env`)

Copy `.env.example` to `.env` and fill in values from CloudFormation outputs:

```bash
VITE_API_URL=/api
VITE_COGNITO_USER_POOL_ID=us-east-1_xxxxx
VITE_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
VITE_COGNITO_DOMAIN=expense-tally-dev.auth.us-east-1.amazoncognito.com
VITE_REDIRECT_URL=http://localhost:5173
```

Get values from deployed stack:
```bash
aws cloudformation describe-stacks \
  --stack-name expense-tally-v2 \
  --query 'Stacks[0].Outputs'
```

### Backend

Environment variables are injected by SAM/CloudFormation. For local development with `sam local`, they are auto-populated from the template.

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|---------|
| `sam local` fails | Ensure Docker is running; SAM local uses Docker containers |
| Frontend can't reach API | Check Vite proxy config; ensure `make local` is running on port 3000 |
| 401 on API calls | Check `.env` Cognito values match deployed stack outputs |
| Cold start timeout | Increase Lambda `Timeout` in `template.yaml` (currently 30s) |
| DynamoDB access denied | Ensure table names in env vars match deployed table names |

## Related Documentation

- [Architecture Overview](ARCHITECTURE.md)
- [Backend Architecture](BACKEND.md)
- [Frontend Architecture](FRONTEND.md)
- [Deployment](DEPLOYMENT.md)
- [API Reference](API.md)
