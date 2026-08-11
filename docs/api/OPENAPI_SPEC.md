# CloudForge AI — REST API Reference Specification

## API Architecture
- **Base URL**: `http://localhost:8000` (Dev) / `https://api.cloudforge.ai` (Prod)
- **Protocol**: HTTPS / REST
- **Authentication**: Stateless Bearer JWT Token in `Authorization` header
- **Content Type**: `application/json`

---

## Endpoint Inventory (28 Verified Endpoints)

### 1. Authentication Core (`/auth`)
- `POST /auth/register` — Register user, organization, and issue initial JWT pair.
- `POST /auth/login` — Authenticate credentials with 15-minute brute-force lockout checking.
- `POST /auth/refresh` — Single-use refresh token rotation issuing new access token.
- `POST /auth/logout` — Revoke active refresh token.
- `POST /auth/logout-all` — Revoke all active refresh tokens and user device sessions.
- `GET /auth/me` — Fetch current user identity, profile, and organization memberships.
- `GET /auth/sessions` — List active device sessions with browser/OS metadata.
- `DELETE /auth/sessions/{id}` — Terminate a specific active device session.
- `POST /auth/switch-workspace` — Switch active workspace context and record audit log.

### 2. Organization Platform (`/orgs`)
- `GET /orgs/{id}` — Fetch detailed organization metadata and branding settings.
- `PATCH /orgs/{id}` — Update organization name, description, website, timezone, and accent color.
- `POST /orgs/{id}/archive` — Archive organization to read-only state.
- `POST /orgs/{id}/restore` — Restore archived organization to active status.
- `DELETE /orgs/{id}` — Perform soft deletion of organization workspace.
- `GET /orgs/{id}/activity-timeline` — Stream audit log event feed.
- `GET /orgs/{id}/dashboard-summary` — Fetch dashboard KPI counters (Projects, Members, Teams, Pending Invites).

### 3. Membership & Access Control (`/orgs/{id}/members`)
- `GET /orgs/{id}/members` — List organization members with role badges and join dates.
- `POST /orgs/{id}/members` — Add existing user directly to organization.
- `PATCH /orgs/{id}/members/{userId}/role` — Change member RBAC role (enforces last-owner safeguard).
- `DELETE /orgs/{id}/members/{userId}` — Remove member from organization.
- `POST /orgs/{id}/transfer-ownership` — Transfer organization ownership to target member.

### 4. Invitation Lifecycle (`/orgs/{id}/invitations` & `/invitations`)
- `GET /orgs/{id}/invitations` — List pending, accepted, rejected, or cancelled invitations.
- `POST /orgs/{id}/invitations` — Issue tokenized invitation email.
- `POST /orgs/{id}/invitations/{id}/resend` — Resend invitation token with extended TTL.
- `DELETE /orgs/{id}/invitations/{id}` — Cancel pending invitation.
- `POST /invitations/{token}/accept` — Accept invitation token and create membership.
- `POST /invitations/{token}/reject` — Reject invitation token.

### 5. Profile & Notifications (`/profile` & `/notifications`)
- `GET /profile/me` — Fetch user profile credentials.
- `PATCH /profile/me` — Update user full name.
- `POST /profile/change-password` — Change password with history reuse prevention.
- `GET /notifications` — List user notifications.
- `GET /notifications/unread-count` — Count unread notifications.
- `PATCH /notifications/{id}/read` — Mark notification read.
- `POST /notifications/mark-all-read` — Mark all notifications read.
