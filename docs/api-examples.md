# API Examples

## Login

```bash
curl -s http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"customer@finguard.dev","password":"Password123!"}'
```

## Create transfer

```bash
curl -s http://localhost:8080/api/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Idempotency-Key: $(uuidgen)" \
  -H 'Content-Type: application/json' \
  -d '{
    "sourceAccountId":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "destinationAccountId":"cccccccc-cccc-cccc-cccc-cccccccccccc",
    "amount":250.00
  }'
```

## Risk scenarios

- `$250`: automatically approved.
- `$5,000`: flagged for manual review.
- `$10,000.01`: rejected automatically.
- More than five rapid submissions from one source account: rejected by the Redis velocity rule.


## Generate an analyst investigation brief

Use an administrator token. The endpoint works without an OpenAI key by returning a deterministic fallback.

```bash
curl -s http://localhost:8080/api/investigations/brief \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "transactionId":"00000000-0000-0000-0000-000000000001",
    "amount":5000.00,
    "currency":"USD",
    "status":"FLAGGED",
    "riskScore":55,
    "reasons":["Large transfer requires analyst approval"],
    "eventTypes":["TRANSACTION_INITIATED","FRAUD_DECISION_FLAGGED"]
  }'
```
