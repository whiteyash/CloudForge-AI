# CloudForge AI — Enterprise Testing Strategy

## Overview
CloudForge AI employs a multi-layered verification strategy combining automated unit tests, Spring Boot integration tests, Next.js static build checks, ESLint analysis, and Enterprise Release Gate audits.

---

## Test Automation Layers

### 1. Backend Service Unit & Repository Tests (`services/api`)
- **Framework**: JUnit 5 + Spring Boot Test + Mockito + H2 In-Memory DB.
- **Coverage**:
  - `ProjectServiceTest`: Multi-tenant project workspace isolation.
  - `HealthControllerTest`: System telemetry health probes.
  - `OrgInvitationServiceTest`: Tokenized invitation issuance, resend TTL extension, and token validation.

```bash
cd services/api
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home ./mvn_dist/apache-maven-3.9.9/bin/mvn clean test
```

### 2. Frontend Type & Build Verification (`apps/web`)
- **Framework**: ESLint + TypeScript Compiler (`tsc --noEmit`) + Next.js Production Build Worker.
- **Coverage**: All 19 application routes must compile statically with 0 errors and 0 warnings.

```bash
cd apps/web
npm run lint && npm run typecheck && npm run build
```

### 3. Enterprise Release Gate Verification Checklist
- [x] Backend compilation: 68 source files compiled with 0 errors.
- [x] Frontend routes: 19 static routes prerendered with 0 warnings.
- [x] Database migrations: Flyway V1 through V7 executed cleanly.
- [x] Security controls: Single-use refresh tokens, `TenantContext` isolation, 6-role RBAC matrix, and last-owner safeguards active.
