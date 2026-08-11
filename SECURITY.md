# Security Policy

CloudForge AI takes platform security seriously. We welcome reports from security researchers and developers to help keep our platform and users safe.

---

## 🔒 Supported Versions

Only the latest release version receives security updates.

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

---

## 🛡️ Reporting a Vulnerability

Please do **NOT** report security vulnerabilities through public GitHub issues.

Instead, please report security issues by emailing `security@cloudforge-ai.org`. Include:
- A description of the vulnerability and potential impact.
- Detailed step-by-step instructions or proof-of-concept script to reproduce the issue.
- Any suggested mitigations.

You will receive an acknowledgment of your submission within 24 hours. We aim to validate and resolve critical security issues within 5 business days.

---

## 🔐 Built-in Platform Security Controls

- **Authentication & Authorization**: Stateless JWT authentication with refresh token rotation and server-enforced fine-grained RBAC (`@RequirePermission`).
- **Data Protection**: AES-256-GCM encryption for Git OAuth credentials and secrets.
- **Tenant Isolation**: Strict multi-tenant query scoping by `orgId` and `projectId`.
- **AI Safety Gate**: Mandatory Human Approval Gate (`phase6_autonomous_operations_contract.md`) preventing autonomous AI infrastructure execution.
- **Audit Stream**: Immutable prompt and API request logging via `AuditLogger`.
