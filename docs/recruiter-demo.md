# Three-Minute Recruiter Demo

1. Open `http://localhost:8080` and use the customer demo.
2. Show two seeded accounts and explain that services own separate databases.
3. Submit `$250`; explain the immediate `PENDING_REVIEW` response and Kafka event.
4. Watch the transfer become `COMPLETED` after an automatic fraud approval.
5. Submit `$5,000`; show it becoming `FLAGGED` without moving money.
6. Sign out and use the fraud analyst demo.
7. Approve the flagged transaction and show the customer balance update.
8. Generate an AI case brief; explain that it uses structured evidence, falls back locally without a key, and never changes transaction state.
9. Show the audit stream and Grafana service metrics at `http://localhost:3001`.
10. Open the architecture diagram and point out transactional outbox, at-least-once delivery, idempotent event processing, deterministic locking, and atomic ledger posting.

Do not spend the demo clicking through every screen. Explain one business workflow and the engineering decisions that protect it.
