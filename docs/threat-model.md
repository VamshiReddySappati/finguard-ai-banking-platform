# Threat Model

## Protected assets

- Authentication tokens
- Account balances and operation history
- Transfer state and idempotency records
- Fraud decisions
- Audit evidence
- Internal service credentials
- Optional AI provider credentials and case evidence

## Main threats and controls

| Threat | Implemented demo control | Production requirement |
|---|---|---|
| Credential theft | BCrypt password hashes, short-lived JWT | Managed identity provider, MFA, credential monitoring |
| Token forgery | HMAC signature verification | Asymmetric keys, rotation, JWKS, issuer and audience validation |
| Duplicate transfer | Required idempotency key and database uniqueness | Distributed idempotency policy and retention controls |
| Duplicate event | Unique fraud decision, terminal-state checks, idempotent balance operations | Event IDs across every consumer and replay tests |
| Unauthorized account access | Owner validation and role checks | Fine-grained policy engine and formal authorization tests |
| Service impersonation | Internal token header | mTLS and workload identity |
| Message tampering | Private Docker network | Kafka TLS, SASL, ACLs, schema registry |
| Fraud-rule bypass | Server-side risk workflow | Versioned rules, four-eyes approval, governance and monitoring |
| Audit alteration | Append-oriented audit records | WORM storage, signatures, retention policy, SIEM export |
| Secret leakage | `.env` ignored and example values only | Cloud secret manager and automated scanning |
| Prompt injection or fabricated AI evidence | Structured server-generated fields, strict system instruction, no tools, no autonomous actions | Formal model evaluation, output schema validation, red-team testing |
| AI provider outage | Deterministic local fallback | Circuit breaker, provider routing, operational SLOs |
| Sensitive data disclosure to AI | Synthetic data only; no customer PII in the prompt | Data classification, redaction, consent, retention and vendor governance |

## Explicit non-goals

The repository is not PCI DSS, SOC 2, FFIEC, GLBA, or banking-regulator compliant. Those are organizational assurance programs, not framework checkboxes.
