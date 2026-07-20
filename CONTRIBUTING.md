# Contributing

1. Create a branch from `main`.
2. Keep service boundaries intact; do not share databases between services.
3. Run `mvn clean verify` and `cd frontend && npm ci && npm run build`.
4. Add tests for changes to transfer state transitions, fraud rules, or atomic ledger posting.
5. Open a pull request describing the business behavior, failure cases, and evidence of testing.
