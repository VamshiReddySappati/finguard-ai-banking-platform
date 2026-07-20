# Security Policy

This repository is a portfolio demonstration and must never process real banking credentials, account numbers, personal data, or money.

Report vulnerabilities privately through GitHub Security Advisories. Do not open a public issue containing secrets or exploit details.

Before any real deployment:
- Replace all demo secrets and credentials.
- Use a managed identity provider and asymmetric token signing.
- Move service credentials into a secrets manager.
- Restrict Actuator and internal endpoints to private networks.
- Enable TLS everywhere, network policies, WAF controls, and database encryption.
- Complete a formal threat model, penetration test, compliance review, and disaster-recovery exercise.
