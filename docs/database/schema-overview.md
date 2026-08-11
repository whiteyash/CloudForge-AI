# CloudForge AI — Database Schema Overview

## Flyway Migration History (V1 through V17)

| Migration Version | Description | Target Tables / Modifications |
| :--- | :--- | :--- |
| `V1__init_schema.sql` | Base Identity & Org Schema | `users`, `organizations`, `organization_members` |
| `V2__auth_tokens.sql` | JWT Refresh Tokens & Sessions | `refresh_tokens`, `active_sessions` |
| `V3__password_reset_and_email.sql` | Password Reset & Email Verification | `password_reset_tokens`, `email_verification_tokens` |
| `V4__workspace_context.sql` | Tenant Workspace State | `users.active_workspace_id` |
| `V5__permission_engine.sql` | RBAC Permission Engine | `roles`, `permissions`, `role_permissions` |
| `V6__audit_trail.sql` | Central Audit Trail | `audit_logs` |
| `V7__notifications.sql` | Notification Platform | `notifications` |
| `V8__branding_and_danger_zone.sql` | Org Branding & Soft Deletion | `organizations.logo_url`, `organizations.is_archived` |
| `V9__permission_cache_and_audit.sql` | Permission Audit Indexes | `audit_logs.permission_code` |
| `V10__user_preferences_audit_and_search.sql` | User Preferences & Favorites | `user_preferences`, `favorite_workspaces` |
| `V11__security_center_and_workspace_favorites.sql` | Account Security Center | `users.password_changed_at`, `users.mfa_enabled` |
| `V12__project_platform.sql` | Initial Projects Platform | `projects`, `project_environments`, `project_repositories` |
| `V13__enterprise_event_and_notification_platform.sql` | Enterprise Event Bus | `notifications.category`, `audit_logs.correlation_id` |
| `V14__notification_rules_and_archive.sql` | Notification Rules & Archiving | `notification_rules` |
| `V15__enterprise_project_platform_full.sql` | Project Variables & Vault References | `project_variables`, `project_secret_references` |
| `V16__project_platform_modules_full.sql` | Project Metadata & Icons | `projects.icon_url`, `projects.labels` |
| `V17__project_teams_and_settings.sql` | Project Teams & Memberships | `project_teams`, `project_team_members` |
