# System Architecture

This document describes the overall system architecture of Expense Tally v2, a serverless expense tracking application.

## High-Level Overview

Expense Tally v2 is a full-stack serverless application hosted entirely on AWS. It consists of a React single-page application served via CloudFront/S3, a Go-based API running on AWS Lambda behind API Gateway, DynamoDB for persistence, and Amazon Cognito for authentication. A scheduled Lambda function handles daily bank transaction syncing.

## Architecture Diagram

```
                                  ┌──────────────────────────────────┐
                                  │            End User              │
                                  │     (Browser / Mobile Web)       │
                                  └──────────────┬───────────────────┘
                                                 │
                                                 ▼
                               ┌─────────────────────────────────────┐
                               │     Amazon CloudFront (CDN)         │
                               │                                     │
                               │  ┌───────────┐   ┌───────────────┐  │
                               │  │ S3 Origin  │   │  API Origin   │  │
                               │  │ (default)  │   │  (/api/*)     │  │
                               │  └──────┬─────┘   └──────┬────────┘  │
                               └─────────┼────────────────┼───────────┘
                                         │                │
                        ┌────────────────┘                └────────────────┐
                        ▼                                                  ▼
              ┌───────────────────┐                     ┌──────────────────────────┐
              │   S3 Bucket       │                     │  API Gateway (HTTP API)   │
              │                   │                     │                          │
              │  React SPA        │                     │  Cognito JWT Authorizer  │
              │  (Vite build)     │                     └────────────┬─────────────┘
              └───────────────────┘                                  │
                                                                     ▼
                                                    ┌──────────────────────────────┐
                                                    │     API Lambda Function      │
                                                    │                              │
                                                    │  Go · Chi Router · arm64     │
                                                    │  provided.al2023 runtime     │
                                                    │                              │
                                                    │  Handler → Service → Repo    │
                                                    └────────────┬─────────────────┘
                                                                 │
                         ┌───────────────────────────────────────┼──────────────┐
                         │                                       │              │
                         ▼                                       ▼              ▼
              ┌────────────────────┐              ┌───────────────────┐  ┌──────────────┐
              │  Amazon DynamoDB   │              │  Amazon Cognito   │  │  Secrets Mgr │
              │                    │              │                   │  │  (planned)   │
              │  5 tables:         │              │  User Pool        │  └──────────────┘
              │  - transactions    │              │  Hosted UI        │
              │  - categories      │              │  OAuth 2.0 code   │
              │  - keywords        │              └───────────────────┘
              │  - connections     │
              │  - sync-logs       │
              └────────────────────┘
                         ▲
                         │
              ┌────────────────────┐          ┌──────────────────────┐
              │  Sync Lambda       │─────────▶│  SimpleFIN Bridge    │
              │                    │          │  Teller API          │
              │  EventBridge       │          │  (Bank Providers)    │
              │  rate(1 day)       │          └──────────────────────┘
              └────────────────────┘
```

## Component Summary

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **CDN** | Amazon CloudFront | Content delivery, HTTPS termination, SPA routing, API pass-through |
| **Frontend** | React 18 + TypeScript + Vite + Tailwind CSS | Single-page application |
| **Static Hosting** | Amazon S3 | Frontend asset storage |
| **API Gateway** | AWS HTTP API (SAM) | HTTP routing, JWT authorization, CORS |
| **API Compute** | AWS Lambda (Go, arm64) | Business logic, API handlers |
| **Sync Compute** | AWS Lambda (Go, arm64) | Scheduled bank transaction sync |
| **Database** | Amazon DynamoDB (5 tables) | Primary data store, on-demand billing |
| **Authentication** | Amazon Cognito | User management, OAuth 2.0 code flow, JWT tokens |
| **Scheduler** | Amazon EventBridge | Daily sync trigger (rate 1 day) |
| **Bank Integration** | SimpleFIN Bridge, Teller API | Financial transaction data providers (see [ADR-001](adr/001-enrich-api-data-capture.md)) |
| **Infrastructure** | AWS SAM (CloudFormation) | Infrastructure as code |
| **CI/CD** | GitHub Actions | Automated build, test, and deploy |

## Request Flow

### Authenticated API Request

```
1. User interacts with React SPA in browser
2. SPA sends request to CloudFront (e.g., GET /api/transactions)
3. CloudFront routes /api/* to API Gateway origin
4. API Gateway validates JWT token via Cognito authorizer
5. API Gateway invokes API Lambda (proxy integration)
6. Chi router dispatches to appropriate handler
7. Handler calls service layer (business logic)
8. Service calls repository (DynamoDB operations)
9. Response flows back: Repo → Service → Handler → Lambda → API GW → CloudFront → Browser
```

### Static Asset Request

```
1. Browser requests /dashboard or /index.html
2. CloudFront routes to S3 origin (default behavior)
3. For SPA routes (404/403), custom error response returns /index.html with 200
4. React Router handles client-side routing
```

### Scheduled Sync

```
1. EventBridge triggers Sync Lambda daily (rate 1 day)
2. Sync Lambda loads all provider connections from DynamoDB
3. For each connection, calls Teller API to fetch new transactions
4. Keyword service suggests categories for new transactions
5. Transactions stored in DynamoDB (unconfirmed)
6. Sync logs recorded for audit trail
7. User reviews and confirms transactions via Review Queue
```

## Data Architecture

### DynamoDB Single-Table-Inspired Design

Each entity gets its own table but uses composite key patterns for efficient querying:

| Table | Partition Key (PK) | Sort Key (SK) | GSIs |
|-------|-------------------|---------------|------|
| **Transactions** | `TXN#{source}#{id}` | — | `DateIndex` (gsi1pk=`YEAR#YYYY`, date), `UnconfirmedIndex` (gsi2pk=`UNCONFIRMED`, date) |
| **Categories** | `CAT#{uuid}` | — | — |
| **Keywords** | `KW#{keyword}` | `categoryId` | — |
| **Connections** | `CONN#{provider}#{accountId}` | — | — |
| **Sync Logs** | `SYNC#{source}` | `{ISO8601 timestamp}` | — |

### Key Design Decisions

- **Sparse GSI for unconfirmed transactions**: Only unconfirmed items have `gsi2pk` populated, keeping the index small and queries fast.
- **Year-based partitioning**: `gsi1pk = YEAR#YYYY` enables efficient date-range queries within a year while avoiding hot partitions.
- **Keyword frequency learning**: The keyword-association table records how often a keyword maps to a category, enabling ML-lite auto-categorization.
- **PAY_PER_REQUEST billing**: All tables use on-demand capacity, ideal for variable/unpredictable traffic.

## Technology Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Backend language | Go | Fast cold starts on Lambda, strong typing, single-binary deploy |
| Lambda runtime | `provided.al2023` (arm64) | ARM-based Graviton for cost efficiency |
| HTTP framework | Chi v5 | Lightweight, idiomatic Go router with middleware support |
| Frontend framework | React 18 + TypeScript | Modern, type-safe UI development |
| Build tool | Vite 5 | Fast builds and HMR for development |
| CSS framework | Tailwind CSS 3.4 | Utility-first, consistent design without CSS-in-JS overhead |
| Auth | Cognito Hosted UI | Managed OAuth 2.0 without custom auth UI |
| IaC | AWS SAM | CloudFormation-based, first-class Lambda support |
| CI/CD | GitHub Actions | Tightly integrated with source control |

## Architecture Decision Records

- [ADR-001: Enrich Database to Capture Full Provider API Data](adr/001-enrich-api-data-capture.md)

## Related Documentation

- [Backend Architecture](BACKEND.md)
- [Frontend Architecture](FRONTEND.md)
- [Security](SECURITY.md)
- [Observability](OBSERVABILITY.md)
- [Developer Guide](DEVELOPER-GUIDE.md)
- [Deployment](DEPLOYMENT.md)
- [API Reference](API.md)
