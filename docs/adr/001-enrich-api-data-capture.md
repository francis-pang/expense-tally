# ADR-001: Enrich Database to Capture Full Provider API Data

**Date**: 2026-02-19

**Status**: Accepted

## Context

Expense Tally integrates with two financial data providers -- SimpleFIN and Teller -- to sync bank transactions. The original implementation captured only a minimal subset of the data returned by each provider's API:

- **SimpleFIN**: The `payee` field (clean merchant name) and `memo` field (raw bank memo) were ignored. The `merchant` field on transactions was populated with the institution's `org.name` rather than the actual payee. Account-level fields like `balance`, `available-balance`, and additional org metadata (`url`, `id`) were also dropped.

- **Teller**: Transaction fields including `type` (card_payment, ach, atm, etc.), `status` (pending/posted as a string), `running_balance`, `details.category`, and `details.counterparty.type` were not captured. The `rawPayload` was stored as an empty string. Account metadata like `type`, `subtype`, `status`, `last_four`, and `institution` were not mapped to the domain model. The `TellerAccount` struct referenced in the adapter did not exist in the model package.

This data loss made it harder to build features like smart categorization (which benefits from provider-assigned categories), merchant name disambiguation (payee vs. institution), and account detail display in the UI.

## Decision

Enrich the domain models, provider adapters, and repository layer to capture the full set of useful fields from both provider APIs. Specifically:

### Transaction enrichment

Add fields to `model.Transaction` that are meaningful across providers:

| Field | Source: SimpleFIN | Source: Teller |
|-------|-------------------|----------------|
| `Payee` | `payee` (clean merchant name) | -- |
| `Memo` | `memo` (raw bank memo) | -- |
| `TransactionType` | -- | `type` (card_payment, ach, atm, credit, transfer) |
| `ProviderCategory` | -- | `details.category` (dining, shopping, etc.) |
| `CounterpartyType` | -- | `details.counterparty.type` (organization, person) |
| `RunningBalance` | -- | `running_balance` (nullable) |
| `InstitutionName` | `org.name` | -- |
| `InstitutionID` | `org.id` | -- |

The `Merchant` field semantics change: it is now set from the payee/counterparty name (the actual merchant), falling back to institution name only when payee is unavailable.

### Account/connection enrichment

Add fields to `model.ProviderConnection` for account metadata common across providers:

`AccountName`, `AccountType`, `AccountSubtype`, `InstitutionName`, `InstitutionID`, `Currency`, `LastFour`, `Balance`, `AvailableBalance`, `BalanceUpdatedAt`, `Status`.

Add `model.TellerAccount` struct (was referenced but missing) with full Teller account fields including institution metadata.

Enrich `model.SimpleFINAccount` with balance and org metadata fields.

### Provider adapter changes

- **SimpleFIN**: Capture `payee`, `memo` from transactions; `url`, `id` from org; `available-balance` from accounts. Use `payee` as `Merchant` with fallback to `org.name`.
- **Teller**: Parse `amount` as string (matching Teller's actual API format), capture `type`, `status`, `running_balance`, `details.category`, `details.counterparty.type`. Serialize full transaction JSON as `rawPayload`. Map `status: "pending"` to `Pending: true`.

### What we chose NOT to capture

- **Investment holdings** (SimpleFIN): A separate domain (investment tracking) with its own data model. Deferring to avoid scope creep.
- **Account numbers / routing numbers** (Teller): Security-sensitive data from Teller's `/details` endpoint. Not needed for expense tracking and storing it would increase our security surface unnecessarily.

## Consequences

### Positive

- **Better merchant identification**: Transactions now show the actual payee name instead of the institution name, improving readability and keyword-based auto-categorization accuracy.
- **Provider category hints**: Teller's built-in category can bootstrap the auto-categorization system, especially for new users with no keyword history.
- **Richer account display**: The UI can now show account type, last four digits, balance, and institution name on the connections page.
- **Full audit trail**: `rawPayload` is now populated for Teller transactions (was empty), ensuring both providers have complete raw data for debugging.
- **No schema migration required**: DynamoDB's schemaless nature means new attributes are written alongside existing ones without any table modification.

### Negative

- **Increased item size**: Each transaction and connection item stores more attributes in DynamoDB. For typical usage volumes this is negligible (items remain well under DynamoDB's 400KB limit).
- **Teller amount type change**: `TellerTransaction.Amount` changed from `float64` to `string` to match Teller's actual API response, with parsing now done in the adapter's mapping logic. This is more correct but differs from the previous (implicit) behavior.

### Neutral

- **Backward compatible**: Existing transactions in DynamoDB are unaffected. The new fields use `omitempty` in JSON tags and conditional writes in the repository, so older items simply lack the new attributes and read back as zero values.
- **Frontend impact**: The frontend TypeScript `Transaction` interface should be updated to expose the new fields, but existing fields remain unchanged so the UI will continue to work without frontend changes.
