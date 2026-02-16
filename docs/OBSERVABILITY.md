# Observability

This document describes the current observability setup for Expense Tally v2, including logging, monitoring, and recommendations for improvement.

## Current State

Expense Tally v2 relies primarily on AWS-managed observability through CloudWatch Logs, with application-level request logging via Chi middleware. There is no custom metrics, distributed tracing, or alerting configured.

## Logging

### Lambda Logs (CloudWatch Logs)

All Lambda function output is automatically captured in CloudWatch Logs:

| Function | Log Group | Content |
|----------|-----------|---------|
| API Lambda | `/aws/lambda/{ApiFunction}` | Chi request logs, application errors, panics |
| Sync Lambda | `/aws/lambda/{SyncFunction}` | Sync operation logs, provider errors |

### Chi Middleware Logging

The API Lambda uses Chi's built-in middleware stack for request-level observability:

| Middleware | Log Output |
|-----------|------------|
| `middleware.Logger` | HTTP method, path, status code, response time, bytes written |
| `middleware.RequestID` | Unique request ID in `X-Request-Id` header (for correlation) |
| `middleware.RealIP` | Real client IP extracted from `X-Forwarded-For` / `X-Real-IP` |
| `middleware.Recoverer` | Stack trace on panic recovery (returns 500) |

**Example log output** (Chi Logger format):
```
"GET /api/transactions HTTP/1.1" from 203.0.113.1 - 200 1234B in 45.2ms
```

### Application Logging

| Component | Method | Example |
|-----------|--------|---------|
| Sync Lambda | `log.Printf` / `log.Fatalf` | `log.Printf("synced %d transactions from %s", count, provider)` |
| API Lambda init | `log.Fatalf` | Fatal on DynamoDB client creation failure |
| Sync Service | Silent failures | Errors during sync are logged to DynamoDB sync_logs table |

### Sync Audit Logs (DynamoDB)

The sync service writes structured audit logs to the `{env}-expense-sync-logs` table:

| Field | Type | Description |
|-------|------|-------------|
| `PK` | String | `SYNC#{source}` (e.g., `SYNC#teller`) |
| `SK` | String | ISO 8601 timestamp |
| `status` | String | `success` or `error` |
| `transactionCount` | Number | Number of transactions synced |
| `errorMessage` | String | Error details (if status is `error`) |

These logs are queryable via the API (`GET /api/sync/logs`) and visible in the frontend.

## Monitoring

### Built-in AWS Metrics

The following metrics are automatically collected by AWS services:

#### Lambda Metrics (CloudWatch)

| Metric | Description |
|--------|-------------|
| `Invocations` | Number of function invocations |
| `Duration` | Execution time per invocation |
| `Errors` | Number of invocation errors |
| `Throttles` | Number of throttled invocations |
| `ConcurrentExecutions` | Number of concurrent executions |
| `ColdStarts` | (via Init Duration) Cold start frequency and duration |

#### API Gateway Metrics

| Metric | Description |
|--------|-------------|
| `Count` | Total API requests |
| `4XXError` | Client error rate |
| `5XXError` | Server error rate |
| `Latency` | End-to-end latency |
| `IntegrationLatency` | Lambda invocation time |

#### DynamoDB Metrics

| Metric | Description |
|--------|-------------|
| `ConsumedReadCapacityUnits` | Read consumption |
| `ConsumedWriteCapacityUnits` | Write consumption |
| `ThrottledRequests` | Throttling events |
| `SystemErrors` | Internal DynamoDB errors |
| `SuccessfulRequestLatency` | Request latency |

#### CloudFront Metrics

| Metric | Description |
|--------|-------------|
| `Requests` | Total requests |
| `BytesDownloaded` | Data transfer |
| `4xxErrorRate` | Client error rate |
| `5xxErrorRate` | Server error rate |
| `CacheHitRate` | CDN cache efficiency |

### CloudWatch Dashboards

No custom CloudWatch dashboards are currently configured. Consider creating dashboards for:

1. **Application Health**: Lambda errors, API Gateway 5xx rate, latency p50/p95/p99
2. **Business Metrics**: Sync success rate, transactions synced per day
3. **Cost Tracking**: Lambda invocations, DynamoDB consumed capacity

## Alerting

No CloudWatch Alarms are currently configured. Recommended alarms:

| Alarm | Metric | Threshold | Priority |
|-------|--------|-----------|----------|
| API Error Rate | API Gateway `5XXError` | > 5% for 5 minutes | Critical |
| Lambda Errors | API Lambda `Errors` | > 0 for 5 minutes | High |
| Sync Failures | Sync Lambda `Errors` | > 0 for 1 invocation | High |
| High Latency | API Gateway `Latency` p99 | > 5000ms for 5 minutes | Medium |
| DynamoDB Throttling | `ThrottledRequests` | > 0 for 5 minutes | Medium |
| Lambda Duration | API Lambda `Duration` p95 | > 10000ms | Medium |

Alarms can be delivered via SNS to email, Slack (via Lambda), or PagerDuty.

## Distributed Tracing

Distributed tracing is not currently implemented. Options for adding it:

### AWS X-Ray (Recommended)

Enable X-Ray tracing with minimal code changes:

1. **SAM template**: Add `Tracing: Active` to Lambda functions
2. **Go instrumentation**: Use `aws-xray-sdk-go` to instrument HTTP clients and DynamoDB calls
3. **API Gateway**: Enable X-Ray tracing on the HTTP API

X-Ray provides:
- End-to-end request traces (API GW → Lambda → DynamoDB)
- Latency analysis per service
- Error and fault annotations
- Service map visualization

### Alternative: OpenTelemetry

For vendor-neutral tracing, use the AWS Distro for OpenTelemetry (ADOT) Lambda layer:
- Automatic instrumentation for AWS SDK calls
- Export to X-Ray, CloudWatch, or third-party backends

## Recommendations

### Short-Term (Low Effort)

| Recommendation | Effort | Impact |
|---------------|--------|--------|
| Add CloudWatch Alarms for Lambda errors and API 5xx | Low | High |
| Create a basic CloudWatch Dashboard | Low | Medium |
| Enable X-Ray tracing (`Tracing: Active` in SAM) | Low | High |
| Add `govulncheck` to CI pipeline | Low | Medium |

### Medium-Term (Moderate Effort)

| Recommendation | Effort | Impact |
|---------------|--------|--------|
| Replace `log.Printf` with structured logging (e.g., `slog` or `zerolog`) | Medium | High |
| Add custom CloudWatch metrics (sync count, category usage) | Medium | Medium |
| Instrument DynamoDB calls with X-Ray subsegments | Medium | Medium |
| Add error tracking (e.g., Sentry, or CloudWatch Logs Insights queries) | Medium | High |

### Long-Term (Higher Effort)

| Recommendation | Effort | Impact |
|---------------|--------|--------|
| Implement OpenTelemetry for full observability stack | High | High |
| Create CloudWatch Logs Insights saved queries for common investigations | Medium | Medium |
| Add CloudWatch Synthetics canaries for uptime monitoring | Medium | Medium |
| Implement custom business metrics dashboard | Medium | High |

## Log Investigation Patterns

### Common CloudWatch Logs Insights Queries

**Find all errors in API Lambda**:
```
fields @timestamp, @message
| filter @message like /error/i
| sort @timestamp desc
| limit 50
```

**Cold start analysis**:
```
filter @type = "REPORT"
| stats avg(@initDuration) as avgColdStart,
        max(@initDuration) as maxColdStart,
        count(@initDuration) as coldStarts,
        count(*) as totalInvocations
| display avgColdStart, maxColdStart, coldStarts, totalInvocations
```

**Slow requests** (> 1 second):
```
filter @type = "REPORT" and @duration > 1000
| fields @timestamp, @duration, @memorySize, @maxMemoryUsed
| sort @duration desc
```

**Sync Lambda failures**:
```
fields @timestamp, @message
| filter @message like /error|fail/i
| sort @timestamp desc
```

## Related Documentation

- [Architecture Overview](ARCHITECTURE.md)
- [Backend Architecture](BACKEND.md)
- [Deployment](DEPLOYMENT.md)
