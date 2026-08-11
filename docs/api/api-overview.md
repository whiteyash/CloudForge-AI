# CloudForge AI — REST API Endpoint Overview

## Core API Endpoints

### 1. Authentication (`/auth`)
- `POST /auth/register` — Register new user account.
- `POST /auth/login` — Authenticate credentials and issue JWT pair.
- `POST /auth/refresh-token` — Rotate refresh token and issue new access token.
- `POST /auth/logout` — Invalidate session and refresh token.

### 2. Organizations & Workspaces (`/orgs`)
- `GET /orgs` — List user's active organizations.
- `POST /orgs` — Provision new organization workspace.
- `POST /orgs/{id}/switch` — Switch active tenant context.
- `GET /orgs/{id}/members` — List organization roster.

### 3. Projects & Environments (`/projects`)
- `GET /orgs/{orgId}/projects` — List organization projects.
- `POST /orgs/{orgId}/projects` — Provision project workspace.
- `GET /projects/{id}/environments` — List project environment targets (`DEV`, `QA`, `STAGING`, `PROD`).
- `GET /projects/{id}/variables` — Manage project environment variables (masked & protected).
- `GET /projects/{id}/secrets` — List HashiCorp Vault secret references.

### 4. Notifications & Audit (`/notifications`, `/audit-logs`)
- `GET /notifications` — List active notifications.
- `POST /notifications/{id}/archive` — Archive notification.
- `GET /orgs/{id}/audit-logs` — Query organization audit log timeline.
- `GET /orgs/{id}/audit-logs/export/json` — Export audit trail payload in JSON format.
