# FinGuard AI

### Event-Driven Digital Banking and Fraud Operations Platform

![CI](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF)
![Java](https://img.shields.io/badge/Java-21-ED8B00)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-Event%20Streaming-231F20)
![React](https://img.shields.io/badge/React-19-61DAFB)
![License](https://img.shields.io/badge/License-MIT-blue)

FinGuard AI is a portfolio-grade banking platform that demonstrates secure Java microservices, asynchronous fraud evaluation, idempotent atomic ledger posting, event auditing, observability, an optional OpenAI investigation assistant, and a React operations dashboard.

> **Important:** This is a synthetic portfolio demonstration. It is not a real bank, does not process real money, and is not certified for financial production use.

## Why this project exists

A basic CRUD banking application does not prove distributed-systems ability. FinGuard separates transfer acceptance, risk evaluation, balance mutation, and auditing across independently deployable services. The system intentionally handles duplicate requests, duplicate events, downstream failures, and manual review.




## Technology

- Java 21, Spring Boot, Spring Security, Spring Data JPA
- Apache Kafka with KRaft
- PostgreSQL and Redis
- React 19, TypeScript, Vite
- JWT authentication and role-based authorization
- Docker Compose and Nginx edge routing
- Prometheus and Grafana
- JUnit 5, Mockito, Maven, GitHub Actions
- OpenAPI/Swagger endpoints on each service
- Optional OpenAI Responses API integration with deterministic fallback

## Services

| Service | Port | Responsibility |
|---|---:|---|
| Edge Gateway | 8080 | React UI and API reverse proxy |
| Auth Service | 8081 | Users, BCrypt credentials, JWT issuance |
| Account Service | 8082 | Accounts, balances, atomic double-entry-style ledger posting |
| Transaction Service | 8083 | Transfer state machine and transactional outbox |
| Fraud Service | 8084 | Amount rules, Redis velocity checks, analyst resolution |
| Audit Service | 8085 | Append-oriented domain event evidence |
| Investigation Service | 8086 | Human-in-the-loop AI case briefs with safe fallback |
| Prometheus | 9090 | Metrics collection |
| Grafana | 3001 | Metrics exploration |

## Run locally

### Requirements

- Docker Engine with Docker Compose
- Approximately 6 GB of available memory for the complete stack

```bash
git clone <your-repository-url>
cd finguard-ai-banking-platform
cp .env.example .env
docker compose up --build -d
```

Open:

- Application: `http://localhost:8080`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3001` — user `admin`, password `admin` unless changed

The first container build downloads Java and Node dependencies. Follow startup logs with:

```bash
docker compose logs -f --tail=200
```

Reset all local data:

```bash
docker compose down -v
```

## Optional AI configuration

The analyst dashboard works without an external AI key. With no key, the investigation service produces a deterministic evidence summary. To enable an OpenAI-generated brief, set these values in `.env` before starting the stack:

```bash
OPENAI_API_KEY=your_api_key
OPENAI_MODEL=gpt-5.6-luna
```

The model only receives structured synthetic case facts. It cannot approve, reject, or execute a transfer; the human analyst remains responsible for the decision.

## Demo credentials

| Role | Email | Password |
|---|---|---|
| Customer | `customer@finguard.dev` | `Password123!` |
| Fraud analyst | `admin@finguard.dev` | `Admin123!` |

The dashboard contains seeded synthetic accounts. The demo destination account is `cccccccc-cccc-cccc-cccc-cccccccccccc`.

## Demonstrate the risk workflow

| Amount | Expected behavior |
|---:|---|
| `$250.00` | Automatically approved and completed |
| `$5,000.00` | Flagged; requires analyst approval |
| `$10,000.01` | Rejected; balances remain unchanged |

You can also run:

```bash
./scripts/demo-transfer.sh 250
./scripts/demo-transfer.sh 5000
./scripts/demo-transfer.sh 10000.01
```

## Reliability mechanisms

- **Request idempotency:** each transfer requires `Idempotency-Key`; a database constraint prevents duplicate creation for the same user.
- **Transactional outbox:** transfer state and outbound event are committed together before Kafka publication.
- **At-least-once tolerance:** fraud records, terminal transaction states, audit source event IDs, and account operations reject duplicate processing.
- **Deterministic balance locking:** both accounts are locked in UUID order to reduce deadlock risk.
- **Atomic ledger boundary:** debit and credit legs commit in one account-service database transaction, eliminating the uncertain remote compensation window.
- **Retry-safe uncertain outcomes:** transport and server failures roll back the transaction-service state so Kafka can redeliver; account posting remains safe through transaction-ID idempotency.
- **Manual review:** high-value transfers remain flagged and cannot mutate balances until an analyst resolves them.
- **Human-in-the-loop AI:** generated briefs summarize supplied evidence but cannot perform banking actions.

## Security mechanisms

- BCrypt password hashing
- Signed JWT access tokens
- Role-based customer and analyst endpoints
- Account ownership checks
- Private service-to-service endpoints protected with a demo internal token
- Input validation, masked account numbers, secure response headers
- No real personal, banking, or healthcare data
- Structured AI inputs, explicit non-autonomy, and a no-key deterministic fallback

Read [SECURITY.md](SECURITY.md) before exposing this stack outside a local environment.

## Build without Docker

```bash
mvn clean verify
cd frontend
npm ci
npm run build
```

Running services individually also requires PostgreSQL, Kafka, and Redis. Default configuration is in each service's `application.yml` and can be overridden with environment variables.

## API documentation

When a service is run with its port exposed, Swagger UI is available at `/swagger-ui.html`. A ready-to-import Postman collection is included at:

`postman/FinGuard-AI.postman_collection.json`

See [API Examples](docs/api-examples.md) and [GitHub Upload Instructions](docs/github-upload.md).

## Repository structure

```text
common-events/        Shared versioned domain event contracts
common-security/      Reusable JWT authentication filter
auth-service/          Authentication and JWT issuance
account-service/       Account ownership and atomic ledger posting
transaction-service/   Transfer state machine and outbox
fraud-service/         Risk rules and analyst decisions
audit-service/         Event evidence store
investigation-service/ Optional OpenAI case brief generation
frontend/             React and TypeScript operations dashboard
edge-gateway/         Nginx UI hosting and API routing
infra/                PostgreSQL, Prometheus, and Grafana configuration
docker/               Reusable multi-stage Java service image
postman/              API collection
scripts/              Repeatable demo commands
docs/                 Architecture, threat model, and demo narrative
.github/               Continuous integration and dependency updates
```

## Current limitations

This repository deliberately stops before pretending to be a real bank. A production implementation would still require asymmetric token signing, managed identity, mTLS, Kafka ACLs and schema registry, Flyway migrations, immutable external audit storage, secret management, multi-region recovery, reconciliation jobs, formal performance evidence, security testing, and regulatory governance.

## Strong resume bullets after you build and understand it

- Designed an event-driven digital banking platform using Java 21, Spring Boot, Kafka, PostgreSQL, Redis, and React, separating transfer acceptance, fraud evaluation, account mutation, and audit processing across independently deployable services.
- Implemented idempotency keys, transactional outbox publishing, at-least-once event handling, deterministic balance locking and atomic two-leg ledger posting to protect transfer consistency under duplicate delivery and downstream failure.
- Built explainable fraud rules with Redis-based velocity detection, manual analyst resolution, and an optional OpenAI Responses API investigation assistant with a deterministic fallback and mandatory human approval.
- Added Prometheus metrics, Grafana monitoring, Docker Compose deployment, OpenAPI documentation, automated tests, and GitHub Actions verification.

Only use these bullets after you can explain the architecture and demonstrate the running application.

## License

MIT — see [LICENSE](LICENSE).
