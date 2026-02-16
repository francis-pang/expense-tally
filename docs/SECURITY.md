# Security

This document describes the security architecture, controls, and considerations for Expense Tally v2.

## Authentication

### Overview

Authentication is managed by **Amazon Cognito** with the OAuth 2.0 Authorization Code flow via Cognito Hosted UI. Users never enter credentials directly into the application.

### Cognito User Pool Configuration

| Setting | Value | Purpose |
|---------|-------|---------|
| Username attribute | `email` | Users sign in with email address |
| Auto-verified attributes | `email` | Email is verified during registration |
| Admin-only user creation | `true` | Users cannot self-register; admin must create accounts |
| Min password length | 8 | Minimum password complexity |
| Require uppercase | Yes | Password policy |
| Require lowercase | Yes | Password policy |
| Require numbers | Yes | Password policy |
| Require symbols | No | Password policy |

### OAuth 2.0 Flow

```
┌─────────────┐     ┌─────────────────────┐     ┌───────────────┐
│ React SPA   │     │ Cognito Hosted UI   │     │ Cognito       │
│             │     │ (Login Page)        │     │ Token Endpoint│
└──────┬──────┘     └──────────┬──────────┘     └───────┬───────┘
       │                       │                        │
       │  1. signInWithRedirect()                       │
       │──────────────────────▶│                        │
       │                       │                        │
       │  2. User enters       │                        │
       │     email + password  │                        │
       │                       │                        │
       │  3. Redirect with     │                        │
       │     authorization code│                        │
       │◀──────────────────────│                        │
       │                       │                        │
       │  4. Amplify exchanges code for tokens          │
       │───────────────────────────────────────────────▶│
       │                                                │
       │  5. Returns ID token, access token, refresh    │
       │◀───────────────────────────────────────────────│
       │                                                │
       │  6. Tokens stored in browser (Amplify)         │
       │                                                │
```

- **Auth flow**: `ALLOW_USER_SRP_AUTH` (Secure Remote Password) and `ALLOW_REFRESH_TOKEN_AUTH`
- **Identity provider**: `COGNITO` (built-in user directory)
- **OAuth scopes**: `openid`, `email`, `profile`
- **Response type**: `code` (authorization code flow, not implicit)
- **Callback/Logout URLs**: Configurable via `CallbackURL` parameter

### Token Usage

| Token | Used For | Lifetime |
|-------|----------|----------|
| **ID Token** | API Gateway authorization (Bearer token) | 1 hour (default) |
| **Access Token** | Not directly used by app | 1 hour (default) |
| **Refresh Token** | Amplify auto-refreshes expired tokens | 30 days (default) |

## Authorization

### API Gateway JWT Authorizer

All API endpoints (except where excluded) are protected by a Cognito JWT authorizer at the API Gateway level:

```yaml
Auth:
  DefaultAuthorizer: CognitoAuthorizer
  Authorizers:
    CognitoAuthorizer:
      JwtConfiguration:
        issuer: https://cognito-idp.{region}.amazonaws.com/{userPoolId}
        audience:
          - {userPoolClientId}
      IdentitySource: $request.header.Authorization
```

- JWT signature is validated against Cognito's JWKS
- Token expiry is enforced
- Audience (client ID) is validated
- Requests without a valid token receive **401 Unauthorized**

### Application-Level Authorization

Currently, the application does not implement role-based access control (RBAC). All authenticated users have full access to all features. This is acceptable for a single-user or small-team application.

**Considerations for future RBAC**:
- Add Cognito groups (e.g., `admin`, `viewer`)
- Pass groups claim from JWT to Lambda
- Implement middleware to check group membership per route

### Frontend Authorization

- `AuthGuard` component wraps all protected routes
- Checks authentication status via `useAuth()` hook
- Redirects unauthenticated users to `/login`
- Axios response interceptor catches 401 and redirects to `/login`

## Transport Security

### HTTPS Enforcement

| Component | TLS Configuration |
|-----------|------------------|
| **CloudFront** | `ViewerProtocolPolicy: redirect-to-https` (HTTP redirected to HTTPS) |
| **API Gateway origin** | `HTTPSOnly: true`, `OriginProtocolPolicy: https-only`, `TLSv1.2` |
| **S3 access** | Via CloudFront OAI only (no direct public access) |

All client-to-server communication is encrypted with TLS 1.2+.

### CORS Configuration

**API Gateway level**:
```yaml
CorsConfiguration:
  AllowOrigins: ['*']
  AllowMethods: ['*']
  AllowHeaders: ['*']
```

**Chi middleware level**:
```go
cors.Options{
    AllowedOrigins:   []string{"*"},
    AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
    AllowedHeaders:   []string{"Accept", "Authorization", "Content-Type"},
    AllowCredentials: false,
    MaxAge:           300,
}
```

**Recommendation**: In production, restrict `AllowedOrigins` to the CloudFront domain to prevent cross-origin abuse.

## Data Security

### S3 Frontend Bucket

The frontend S3 bucket is fully locked down:

```yaml
PublicAccessBlockConfiguration:
  BlockPublicAcls: true
  BlockPublicPolicy: true
  IgnorePublicAcls: true
  RestrictPublicBuckets: true
```

Access is only permitted via CloudFront Origin Access Identity (OAI).

### DynamoDB

- All tables use AWS-managed encryption at rest (default)
- Access is restricted to Lambda execution roles via `DynamoDBCrudPolicy` (SAM policy template)
- Each Lambda only has access to the tables it needs

### Secrets Management

| Secret | Current State | Target State |
|--------|--------------|-------------|
| **AWS credentials (CI)** | GitHub OIDC (short-lived, no static keys) | Current state is production-ready |
| **Cognito domain prefix** | GitHub Secret | Current state is appropriate |
| **Provider access tokens** | Stored directly in DynamoDB (`AccessTokenRef` field) | Should migrate to AWS Secrets Manager |

The Sync Lambda already has `secretsmanager:GetSecretValue` IAM permission. The TODO is to implement the Secrets Manager integration in code:

```go
// TODO: Get access token from Secrets Manager using conn.AccessTokenRef
accessToken := conn.AccessTokenRef // Simplified: use as token for now
```

**Recommendation**: Store Teller access tokens in Secrets Manager and reference them by ARN in the connection record.

## IAM Security

### Lambda Execution Roles

SAM automatically creates least-privilege execution roles:

**API Lambda**:
- `DynamoDBCrudPolicy` for all 5 tables (scoped to specific table ARNs)
- CloudWatch Logs (auto-provisioned by SAM)

**Sync Lambda**:
- `DynamoDBCrudPolicy` for all 5 tables
- `secretsmanager:GetSecretValue` on `*` (should be scoped to specific secret ARNs)
- CloudWatch Logs (auto-provisioned by SAM)

### CI/CD Authentication

- GitHub Actions uses **OIDC federation** (`role-to-assume`) instead of static AWS credentials
- Short-lived session tokens (no long-lived access keys)
- `id-token: write` permission for OIDC token generation

## Security Headers

CloudFront does not currently configure custom security headers. Consider adding:

| Header | Recommended Value | Purpose |
|--------|------------------|---------|
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains` | Enforce HTTPS |
| `X-Content-Type-Options` | `nosniff` | Prevent MIME sniffing |
| `X-Frame-Options` | `DENY` | Prevent clickjacking |
| `Content-Security-Policy` | `default-src 'self'; ...` | Prevent XSS, injection |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | Control referrer info |

These can be added via a CloudFront Response Headers Policy.

## Input Validation

### Backend

- Request bodies are JSON-decoded into typed structs (Go's type system provides basic validation)
- Path parameters are extracted via Chi router
- Query parameters are parsed with type conversion
- **Recommendation**: Add explicit validation for required fields, string lengths, and numeric ranges

### Frontend

- TypeScript interfaces enforce type safety at compile time
- Form inputs use HTML validation attributes
- **Recommendation**: Add client-side validation before API calls (required fields, format checks)

## Security Checklist

| Area | Status | Notes |
|------|--------|-------|
| Authentication (Cognito) | Implemented | OAuth 2.0 code flow, admin-only registration |
| API authorization (JWT) | Implemented | API Gateway Cognito authorizer |
| HTTPS enforcement | Implemented | CloudFront redirect-to-https |
| S3 public access blocked | Implemented | All four block settings enabled |
| OAI for S3 access | Implemented | CloudFront OAI bucket policy |
| OIDC for CI/CD | Implemented | No static AWS credentials |
| CORS restrictions | Partial | Currently allows all origins; should restrict in production |
| Security headers | Not implemented | Add CloudFront Response Headers Policy |
| Secrets Manager for tokens | Not implemented | Provider tokens stored in DynamoDB |
| Input validation | Minimal | Relies on Go struct types; add explicit validation |
| Rate limiting | Not implemented | Consider API Gateway throttling |
| WAF | Not implemented | Consider AWS WAF on CloudFront |
| Dependency scanning | Not implemented | Consider `govulncheck` and `npm audit` in CI |

## Related Documentation

- [Architecture Overview](ARCHITECTURE.md)
- [Backend Architecture](BACKEND.md)
- [Deployment](DEPLOYMENT.md)
