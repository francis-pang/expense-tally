# API Reference

All API endpoints are served under the `/api` prefix. Requests require a valid Cognito JWT token in the `Authorization: Bearer <token>` header (except as noted).

**Base URL**: `https://<cloudfront-domain>/api`

## Health

### GET `/api/health`

Health check endpoint.

**Authorization**: Required (API Gateway level)

**Response**: `200 OK`

```json
{
  "status": "ok"
}
```

---

## Categories

### GET `/api/categories`

List all expense categories.

**Response**: `200 OK`

```json
[
  {
    "id": "CAT#a1b2c3d4",
    "name": "Groceries",
    "parentId": null
  },
  {
    "id": "CAT#e5f6g7h8",
    "name": "Organic",
    "parentId": "CAT#a1b2c3d4"
  }
]
```

### POST `/api/categories`

Create a new category.

**Request Body**:

```json
{
  "name": "Dining Out",
  "parentId": "CAT#a1b2c3d4"  // optional
}
```

**Response**: `201 Created`

```json
{
  "id": "CAT#new-uuid",
  "name": "Dining Out",
  "parentId": "CAT#a1b2c3d4"
}
```

### GET `/api/categories/{id}`

Get a single category by ID.

**Path Parameters**:
- `id` (string, required): Category ID (e.g., `CAT#a1b2c3d4`)

**Response**: `200 OK`

```json
{
  "id": "CAT#a1b2c3d4",
  "name": "Groceries",
  "parentId": null
}
```

**Errors**:
- `404`: `{"error": "category not found"}`

### PUT `/api/categories/{id}`

Update a category.

**Path Parameters**:
- `id` (string, required): Category ID

**Request Body**:

```json
{
  "name": "Food & Groceries",
  "parentId": null
}
```

**Response**: `200 OK` (updated category object)

### DELETE `/api/categories/{id}`

Delete a category.

**Path Parameters**:
- `id` (string, required): Category ID

**Response**: `204 No Content`

---

## Transactions

### GET `/api/transactions`

List transactions with optional filters.

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `year` | integer | No | Filter by year (e.g., `2026`) |
| `startDate` | string | No | Start date (ISO 8601, e.g., `2026-01-01`) |
| `endDate` | string | No | End date (ISO 8601, e.g., `2026-12-31`) |
| `categoryId` | string | No | Filter by category ID |
| `confirmed` | boolean | No | Filter by confirmation status |

**Response**: `200 OK`

```json
[
  {
    "id": "TXN#manual#uuid-1234",
    "date": "2026-02-15",
    "source": "manual",
    "amount": 42.50,
    "currency": "USD",
    "description": "Whole Foods Market",
    "merchant": "Whole Foods",
    "categoryId": "CAT#a1b2c3d4",
    "suggestedCategoryId": null,
    "isConfirmed": true,
    "paymentMethod": "credit_card",
    "createdAt": "2026-02-15T10:30:00Z",
    "updatedAt": "2026-02-15T10:30:00Z"
  }
]
```

### POST `/api/transactions`

Create a manual transaction.

**Request Body**:

```json
{
  "date": "2026-02-15",
  "amount": 42.50,
  "currency": "USD",
  "description": "Whole Foods Market",
  "merchant": "Whole Foods",
  "categoryId": "CAT#a1b2c3d4",
  "paymentMethod": "credit_card"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `date` | string | Yes | Transaction date (YYYY-MM-DD) |
| `amount` | number | Yes | Transaction amount |
| `currency` | string | Yes | Currency code (e.g., `USD`) |
| `description` | string | Yes | Transaction description |
| `merchant` | string | No | Merchant name |
| `categoryId` | string | No | Category to assign |
| `paymentMethod` | string | Yes | Payment method (e.g., `cash`, `credit_card`, `debit_card`) |

**Response**: `201 Created` (transaction object)

### GET `/api/transactions/unconfirmed`

List unconfirmed transactions (pending review).

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `startDate` | string | No | Start date filter |
| `endDate` | string | No | End date filter |

**Response**: `200 OK` (array of transaction objects with `isConfirmed: false`)

### GET `/api/transactions/{id}`

Get a single transaction by ID.

**Path Parameters**:
- `id` (string, required): Transaction ID (e.g., `TXN#manual#uuid-1234`)

**Response**: `200 OK` (transaction object)

**Errors**:
- `404`: `{"error": "transaction not found"}`

### PUT `/api/transactions/{id}`

Update a transaction (typically to assign/change category).

**Path Parameters**:
- `id` (string, required): Transaction ID

**Request Body**:

```json
{
  "categoryId": "CAT#a1b2c3d4"
}
```

**Response**: `200 OK` (updated transaction object)

**Side Effects**:
- When `categoryId` is set, `KeywordService.LearnFromTagging()` is called to record keyword-category associations for future auto-suggestions.

### DELETE `/api/transactions/{id}`

Delete a transaction.

**Path Parameters**:
- `id` (string, required): Transaction ID

**Response**: `204 No Content`

### PUT `/api/transactions/{id}/confirm`

Confirm a transaction (mark as reviewed/accepted).

**Path Parameters**:
- `id` (string, required): Transaction ID

**Response**: `200 OK`

**Side Effects**:
- Sets `isConfirmed` to `true`
- Removes the transaction from the `UnconfirmedIndex` GSI (sparse index)

---

## Dashboard

### GET `/api/dashboard`

Get spending aggregations for the dashboard.

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `year` | integer | No | Year to aggregate (defaults to current year) |
| `month` | integer | No | Specific month (1-12); omit for full year |

**Response**: `200 OK`

```json
{
  "totalSpend": 2450.75,
  "byCategory": [
    {
      "categoryId": "CAT#a1b2c3d4",
      "categoryName": "Groceries",
      "total": 850.25
    },
    {
      "categoryId": "CAT#e5f6g7h8",
      "categoryName": "Dining Out",
      "total": 420.00
    }
  ],
  "byMonth": [
    { "month": "2026-01", "total": 1200.50 },
    { "month": "2026-02", "total": 1250.25 }
  ],
  "byPaymentMethod": [
    { "method": "credit_card", "total": 1800.00 },
    { "method": "cash", "total": 650.75 }
  ],
  "recentTransactions": [
    // Array of recent Transaction objects (same shape as GET /api/transactions)
  ]
}
```

---

## Sync

### POST `/api/sync/trigger`

Manually trigger a sync of all connected bank accounts.

**Request Body**: None

**Response**: `200 OK`

```json
{
  "message": "sync triggered"
}
```

**Process**:
1. Loads all provider connections from DynamoDB
2. For each connection, calls the provider API (e.g., Teller) to fetch new transactions
3. Applies keyword-based category suggestions to new transactions
4. Stores transactions as unconfirmed
5. Updates connection sync cursor
6. Logs sync result (success/error) to sync_logs table

### GET `/api/sync/logs`

Get recent sync operation logs.

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `source` | string | No | Filter by source (e.g., `teller`) |
| `limit` | integer | No | Number of logs to return |

**Response**: `200 OK`

```json
[
  {
    "source": "SYNC#teller",
    "timestamp": "2026-02-15T06:00:00Z",
    "status": "success",
    "transactionCount": 15,
    "errorMessage": ""
  },
  {
    "source": "SYNC#teller",
    "timestamp": "2026-02-14T06:00:00Z",
    "status": "error",
    "transactionCount": 0,
    "errorMessage": "provider API timeout"
  }
]
```

---

## Connections

### GET `/api/connections`

List all bank provider connections.

**Response**: `200 OK`

```json
[
  {
    "id": "CONN#teller#acc-12345",
    "provider": "teller",
    "lastSyncedAt": "2026-02-15T06:00:00Z"
  }
]
```

### POST `/api/connections/teller`

Register a new Teller bank connection.

**Request Body**:

```json
{
  "accountId": "acc-12345",
  "accessToken": "teller-access-token-xxx"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `accountId` | string | Yes | Teller account ID |
| `accessToken` | string | Yes | Teller access token from enrollment |

**Response**: `201 Created`

```json
{
  "id": "CONN#teller#acc-12345",
  "provider": "teller",
  "lastSyncedAt": null
}
```

---

## Error Responses

All error responses follow a consistent format:

```json
{
  "error": "descriptive error message"
}
```

### Common HTTP Status Codes

| Code | Meaning | When |
|------|---------|------|
| `200` | OK | Successful GET, PUT |
| `201` | Created | Successful POST (resource created) |
| `204` | No Content | Successful DELETE |
| `400` | Bad Request | Invalid request body, missing required fields |
| `401` | Unauthorized | Missing or invalid JWT token (API Gateway) |
| `404` | Not Found | Resource does not exist |
| `500` | Internal Server Error | Unexpected server error |

## TypeScript Types

The frontend defines these interfaces for API responses in `types/index.ts`:

```typescript
interface Transaction {
  id: string;
  date: string;
  source: string;
  amount: number;
  currency: string;
  description: string;
  merchant: string;
  categoryId: string | null;
  suggestedCategoryId: string | null;
  isConfirmed: boolean;
  paymentMethod: string;
  createdAt: string;
  updatedAt: string;
}

interface Category {
  id: string;
  name: string;
  parentId: string | null;
}

interface DashboardData {
  totalSpend: number;
  byCategory: Array<{ categoryId: string; categoryName: string; total: number }>;
  byMonth: Array<{ month: string; total: number }>;
  byPaymentMethod: Array<{ method: string; total: number }>;
  recentTransactions: Transaction[];
}

interface SyncLog {
  source: string;
  timestamp: string;
  status: string;
  transactionCount: number;
  errorMessage: string;
}

interface Connection {
  id: string;
  provider: string;
  lastSyncedAt: string;
}
```

## Related Documentation

- [Architecture Overview](ARCHITECTURE.md)
- [Backend Architecture](BACKEND.md)
- [Frontend Architecture](FRONTEND.md)
- [Security](SECURITY.md)
