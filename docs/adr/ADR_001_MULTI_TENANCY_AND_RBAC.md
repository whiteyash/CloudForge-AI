# ADR 001: Multi-Tenancy Isolation and Enterprise RBAC Architecture

- **Status**: Approved
- **Deciders**: Principal Software Architect & Security Engineering Team
- **Date**: 2026-07-31

---

## Context
CloudForge AI is designed as a multi-tenant enterprise Cloud Operations Platform. It requires strict isolation between organizations, fine-grained access control across 6 production roles (`OWNER`, `ADMIN`, `DEVELOPER`, `DEVOPS`, `SECURITY`, `VIEWER`), and token security preventing horizontal privilege escalation.

---

## Decision Driver Requirements
1. Prevent cross-tenant data leakage across all REST endpoints.
2. Enforce server-side RBAC validation on mutating API operations.
3. Protect against last-owner removal and self-removal.
4. Support single-use refresh token rotation with immediate token revocation.

---

## Decisions Made

1. **ThreadLocal `TenantContext` Enforcer**:
   - Implemented `TenantContext` in Spring Boot API layer.
   - Sets the active organization ID per request thread and clears context in `finally` block to prevent thread pool contamination.

2. **Server-Enforced Owner Protection**:
   - `OrganizationController` and `RbacService` count remaining `OWNER` memberships before executing role downgrades or deletions.
   - Prevents an organization from becoming orphaned without an owner.

3. **Single-Use Refresh Token Rotation**:
   - Refresh tokens are hashed, assigned a single-use UUID, and revoked immediately upon consumption during `/auth/refresh`.

---

## Consequences
- **Positive**: Complete multi-tenant isolation, immune to cross-tenant privilege escalation, full auditability in `membership_role_history` and `audit_logs`.
- **Negative**: Microsecond context switching overhead per HTTP request thread (negligible).
