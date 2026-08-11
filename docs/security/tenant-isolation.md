# CloudForge AI — Tenant Isolation Architecture

## Absolute Multi-Tenant Boundary Protection

```mermaid
graph TD
    Request["Incoming HTTP Request"] --> Filter["TenantContextFilter"]
    Filter -->|Extract X-Tenant-Id| Context["TenantContext (ThreadLocal)"]
    Context --> Services["Domain Services & JPA Repositories"]
    Services -->|Auto-injected org_id| DB["PostgreSQL Database"]
```

### Protection Against Privilege Escalation
- **ThreadLocal Scoping**: `TenantContext` binds `org_id` strictly to the executing thread, ensuring no cross-tenant data leaks.
- **Horizontal Privilege Safeguards**: API requests attempting to manipulate resources outside the authenticated `TenantContext` are rejected with `403 Forbidden`.
