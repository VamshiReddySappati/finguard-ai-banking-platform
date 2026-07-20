#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
EMAIL="${EMAIL:-customer@finguard.dev}"
PASSWORD="${PASSWORD:-Password123!}"
SOURCE="${SOURCE_ACCOUNT_ID:-aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa}"
DESTINATION="${DESTINATION_ACCOUNT_ID:-cccccccc-cccc-cccc-cccc-cccccccccccc}"
AMOUNT="${1:-250.00}"
IDEMPOTENCY_KEY="${IDEMPOTENCY_KEY:-$(python3 -c 'import uuid; print(uuid.uuid4())')}"

LOGIN_PAYLOAD=$(printf '{"email":"%s","password":"%s"}' "$EMAIL" "$PASSWORD")
TRANSFER_PAYLOAD=$(printf '{"sourceAccountId":"%s","destinationAccountId":"%s","amount":%s}' \
  "$SOURCE" "$DESTINATION" "$AMOUNT")

TOKEN=$(curl --fail --silent --show-error "$BASE_URL/api/auth/login" \
  -H 'Content-Type: application/json' \
  --data "$LOGIN_PAYLOAD" \
  | python3 -c 'import json,sys; print(json.load(sys.stdin)["accessToken"])')

curl --fail --silent --show-error "$BASE_URL/api/transactions" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Idempotency-Key: $IDEMPOTENCY_KEY" \
  -H 'Content-Type: application/json' \
  --data "$TRANSFER_PAYLOAD" \
  | python3 -m json.tool
