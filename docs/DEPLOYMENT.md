# Deployment & CI/CD

This document describes the deployment architecture, CI/CD pipelines, infrastructure-as-code, and operational procedures for Expense Tally v2.

## Deployment Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                        GitHub Repository                         │
│                                                                  │
│  Push to feature/v2 ──┐              ┌── Push to main            │
│                       │              │                           │
└───────────────────────┼──────────────┼───────────────────────────┘
                        │              │
                        ▼              ▼
              ┌──────────────────────────────────┐
              │     GitHub Actions Workflow       │
              │        (deploy-v2.yml)            │
              │                                   │
              │  Job 1: build-and-test            │
              │  ├── Go test                      │
              │  ├── Go build (API + Sync)        │
              │  └── npm install + build          │
              │                                   │
              │  Job 2: deploy                    │
              │  ├── AWS OIDC auth                │
              │  ├── SAM build + deploy           │
              │  ├── Frontend build               │
              │  ├── S3 sync                      │
              │  └── CloudFront invalidation      │
              └──────────────┬───────────────────┘
                             │
                    ┌────────┴────────┐
                    │                 │
                    ▼                 ▼
          ┌──────────────┐  ┌──────────────┐
          │  dev env      │  │  prod env     │
          │  (feature/v2) │  │  (main)       │
          └──────────────┘  └──────────────┘
```

## Environments

| Environment | Branch | Stack Name | DynamoDB Prefix | Description |
|-------------|--------|------------|-----------------|-------------|
| **dev** | `feature/v2` | `expense-tally-v2` | `dev-` | Development / staging |
| **prod** | `main` | `expense-tally-v2` | `prod-` | Production |

The `Environment` parameter controls resource naming (e.g., `dev-expense-transactions` vs `prod-expense-transactions`).

## Infrastructure as Code (AWS SAM)

### SAM Template (`template.yaml`)

The entire infrastructure is defined in a single SAM template:

```
template.yaml
├── Parameters
│   ├── Environment (dev/prod)
│   ├── CognitoDomainPrefix
│   ├── FrontendDomainName (optional)
│   └── CallbackURL
├── Globals
│   └── Function defaults (timeout, memory, runtime, env vars)
├── Resources
│   ├── Cognito (UserPool, Client, Domain)
│   ├── API Gateway (HTTP API + JWT Authorizer)
│   ├── Lambda (ApiFunction, SyncFunction)
│   ├── DynamoDB (5 tables)
│   ├── S3 (Frontend bucket + policy)
│   └── CloudFront (Distribution + OAI)
└── Outputs
    ├── ApiUrl
    ├── CloudFrontUrl
    ├── CloudFrontDistributionId
    ├── CognitoUserPoolId
    ├── CognitoClientId
    ├── CognitoDomain
    └── FrontendBucketName
```

### SAM Configuration (`samconfig.toml`)

```toml
stack_name = "expense-tally-v2"
resolve_s3 = true
s3_prefix = "expense-tally-v2"
region = "us-east-1"
capabilities = "CAPABILITY_IAM CAPABILITY_AUTO_EXPAND"
confirm_changeset = true
```

### Lambda Build Process

SAM uses Makefile-based builds defined in `backend/Makefile`:

```
sam build
  ├── build-ApiFunction
  │   └── GOOS=linux GOARCH=arm64 go build -tags lambda.norpc -o bootstrap ./cmd/api
  └── build-SyncFunction
      └── GOOS=linux GOARCH=arm64 go build -tags lambda.norpc -o bootstrap ./cmd/sync
```

- **Runtime**: `provided.al2023` (custom runtime, Go binary named `bootstrap`)
- **Architecture**: `arm64` (AWS Graviton for cost efficiency)
- **Build tag**: `lambda.norpc` (excludes deprecated RPC mode for smaller binary)

## CI/CD Pipeline

### Pipeline: Deploy v2 (`.github/workflows/deploy-v2.yml`)

**Triggers**: Push to `feature/v2` or `main`

#### Job 1: Build and Test

```
┌────────────────────────────────────────────┐
│  build-and-test                            │
│                                            │
│  1. Checkout code                          │
│  2. Setup Go 1.23                          │
│  3. Setup Node 20                          │
│  4. Go test (backend/)                     │
│  5. Go build API → /dev/null (verify)      │
│  6. Go build Sync → /dev/null (verify)     │
│  7. npm install (frontend/)                │
│  8. npm run build (frontend/)              │
└────────────────────────────────────────────┘
```

This job validates that both backend and frontend compile and tests pass. The builds output to `/dev/null` as they are only compilation checks; the actual deploy builds happen in Job 2.

#### Job 2: Deploy

```
┌────────────────────────────────────────────────────────────────┐
│  deploy (needs: build-and-test)                                │
│                                                                │
│  Conditions: push to feature/v2, main, or workflow_dispatch    │
│                                                                │
│  1. Checkout code                                              │
│  2. Setup Go 1.23 + Node 20                                   │
│  3. Configure AWS credentials (OIDC)                           │
│     └── role-to-assume: ${{ secrets.AWS_ROLE_ARN }}            │
│     └── region: us-east-1                                      │
│  4. Setup SAM CLI                                              │
│  5. SAM build                                                  │
│     └── Compiles Go Lambdas via Makefile                       │
│  6. SAM deploy                                                 │
│     └── Environment=prod (main) or dev (feature/v2)            │
│     └── CognitoDomainPrefix from secrets                       │
│  7. npm install + build (frontend)                             │
│  8. Get stack outputs (S3 bucket, CloudFront ID)               │
│  9. S3 sync frontend/dist → bucket (--delete)                  │
│ 10. CloudFront cache invalidation (/*                          │
└────────────────────────────────────────────────────────────────┘
```

### Pipeline: Build (`.github/workflows/build.yml`)

Legacy pipeline for Java/Maven builds. Runs on non-master branches. Not used for v2 development.

### Pipeline: Release (`.github/workflows/release.yml`)

Legacy pipeline for Maven releases to Nexus/Maven Central. Runs on `master` branch. Not used for v2.

## CI/CD Secrets

| Secret | Purpose | Type |
|--------|---------|------|
| `AWS_ROLE_ARN` | IAM role for OIDC authentication | GitHub Secret |
| `COGNITO_DOMAIN_PREFIX` | Cognito hosted UI domain prefix | GitHub Secret |

### OIDC Authentication

GitHub Actions authenticates to AWS using OIDC federation (no static credentials):

```yaml
permissions:
  id-token: write
  contents: read

- uses: aws-actions/configure-aws-credentials@v4
  with:
    role-to-assume: ${{ secrets.AWS_ROLE_ARN }}
    aws-region: us-east-1
```

This requires an IAM Identity Provider and Role configured in AWS to trust the GitHub OIDC provider.

## Manual Deployment

### Full Stack Deploy

```bash
# Build and deploy backend (Lambda + infrastructure)
make deploy

# Build and deploy frontend (S3 + CloudFront)
make deploy-frontend
```

### Backend Only

```bash
sam build
sam deploy
```

### Frontend Only

```bash
cd frontend
npm run build
cd ..
make deploy-frontend
```

### First-Time Setup

1. **Configure AWS CLI** with appropriate credentials
2. **Deploy infrastructure**:
   ```bash
   sam build
   sam deploy --guided
   # Follow prompts for stack name, region, parameters
   ```
3. **Note stack outputs** (Cognito IDs, CloudFront URL, S3 bucket)
4. **Create Cognito user** (admin-only registration):
   ```bash
   aws cognito-idp admin-create-user \
     --user-pool-id <pool-id> \
     --username <email> \
     --user-attributes Name=email,Value=<email> \
     --temporary-password <temp-password>
   ```
5. **Deploy frontend** with correct environment variables:
   ```bash
   cd frontend
   echo "VITE_COGNITO_USER_POOL_ID=<pool-id>" > .env
   echo "VITE_COGNITO_CLIENT_ID=<client-id>" >> .env
   echo "VITE_COGNITO_DOMAIN=<domain>" >> .env
   echo "VITE_REDIRECT_URL=<cloudfront-url>" >> .env
   npm run build
   cd ..
   make deploy-frontend
   ```

## CloudFront Configuration

### Origins

| Origin ID | Source | Path Pattern |
|-----------|--------|-------------|
| `S3Origin` | S3 frontend bucket (via OAI) | Default (`*`) |
| `ApiOrigin` | API Gateway execute-api endpoint | `/api/*` |

### Cache Behavior

| Path | Caching | Methods |
|------|---------|---------|
| Default (S3) | CloudFront default TTL | GET, HEAD, OPTIONS |
| `/api/*` | Disabled (CachingDisabled policy `4135ea2d-...`) | All HTTP methods |

### SPA Routing

Custom error responses enable client-side routing:

```yaml
CustomErrorResponses:
  - ErrorCode: 404 → ResponseCode: 200, ResponsePagePath: /index.html
  - ErrorCode: 403 → ResponseCode: 200, ResponsePagePath: /index.html
```

This ensures that deep links like `/transactions` or `/review` serve `index.html` and let React Router handle the route.

## Rollback Procedures

### Lambda Rollback

SAM/CloudFormation maintains a stack history. To rollback:

```bash
# View recent stack events
aws cloudformation describe-stack-events \
  --stack-name expense-tally-v2 \
  --max-items 20

# Rollback to previous version (CloudFormation handles this on deploy failure)
# For manual rollback, redeploy from a previous commit:
git checkout <previous-commit>
make deploy
```

### Frontend Rollback

Since frontend assets are in S3:

```bash
# Rebuild from a previous commit
git checkout <previous-commit>
cd frontend && npm install && npm run build
cd ..
make deploy-frontend
```

### DynamoDB

DynamoDB tables use `PAY_PER_REQUEST` billing and do not require capacity planning. Table schema changes through CloudFormation are limited (can add GSIs but not modify existing keys).

For data issues, DynamoDB supports point-in-time recovery (PITR) if enabled.

## Monitoring Deploys

After deployment, verify:

1. **Stack status**: `aws cloudformation describe-stacks --stack-name expense-tally-v2 --query 'Stacks[0].StackStatus'`
2. **API health**: `curl https://<cloudfront-domain>/api/health`
3. **Frontend**: Open `https://<cloudfront-domain>` in browser
4. **CloudWatch**: Check Lambda error metrics for the first few minutes

## Related Documentation

- [Architecture Overview](ARCHITECTURE.md)
- [Security](SECURITY.md)
- [Observability](OBSERVABILITY.md)
- [Developer Guide](DEVELOPER-GUIDE.md)
