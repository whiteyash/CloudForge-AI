# CloudForge AI — Enterprise Cloud Operations & AIOps Platform (v1.0.0)

[![Release](https://img.shields.io/badge/release-v1.0.0-3DD9C4?style=flat-square&logo=github)](https://github.com/cloudforge-ai/cloudforge-ai)
[![Java](https://img.shields.io/badge/Java-24-ED8B00?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-16.2-000000?style=flat-square&logo=nextdotjs)](https://nextjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.x-3178C6?style=flat-square&logo=typescript)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.style=flat-square)](LICENSE)

An enterprise-grade, AI-augmented internal developer platform and AIOps control plane that unifies CI/CD, repository governance, observability, root cause analysis, predictive operations, and human-approved autonomous operations.

---

## 🏛️ Platform Architecture

```mermaid
flowchart TD
    subgraph Client ["Mission Control Frontend (Next.js 16 / React 19)"]
        UI[Mission Control Dashboard]
        CopilotUI[Enterprise AI Copilot UI]
        OpsUI[Human Approval Operations UI]
    end

    subgraph Gateway ["API Layer (Spring Boot 3.5 / Spring Security)"]
        JWT[JWT & RBAC Security Filter]
        REST[REST Controllers - 39 Endpoints]
        Audit[AuditLogger & Personal Audit Stream]
    end

    subgraph Core ["Business Platforms (Phases 1 – 5)"]
        AuthService[Identity & Organization Platform]
        ProjectService[Project & Environment Platform]
        GitService[Git Provider Sync & Webhook Platform]
        PipelineService[CI/CD Execution Engine & DAG Resolver]
        RunnerService[Runner Orchestration Engine]
        DeployService[Deployment Engine Service]
    end

   subgraph AI ["Enterprise AI Suite (Phases 6.0 – 6.6)"]
        AIOps[Phase 6.0 AIOps & Incident Intelligence]
        Logs[Phase 6.1 Log Intelligence & Stack Trace Parsing]
        RCA[Phase 6.2 Root Cause Intelligence & Causal Graph]
        Prediction[Phase 6.3 Predictive Operations & Capacity Forecast]
        Knowledge[Phase 6.4 Knowledge Base & Postmortems]
        Copilot[Phase 6.5 Mission Control AI Copilot]
        Operations[Phase 6.6 Human-Approved Autonomous Operations]
    end

    subgraph Data [Persistence & Telemetry]
        DB[(PostgreSQL 16 - Flyway V1 to V41)]
        Redis[(Redis 7 - Cache & Session)]
        LogsDB[(Loki / ANSI Log Storage)]
    end

    UI --> Gateway
    CopilotUI --> Gateway
    OpsUI --> Gateway

    Gateway --> AuthService
    Gateway --> ProjectService
    Gateway --> GitService
    Gateway --> PipelineService
    Gateway --> DeployService

    Gateway --> AIOps
    AIOps --> Logs
    AIOps --> RCA
    RCA --> Prediction
    Prediction --> Knowledge
    Knowledge --> Copilot
    Copilot --> Operations

    Operations -.->|Mandatory Approval Gate| Gate{Human Approval Gate}
    Gate -->|Approved Execution| DeployService
    Gate -->|Approved Execution| PipelineService
    Gate -->|Approved Execution| RunnerService

    Core --> Data
    AI --> Data
```

---

## ✨ Enterprise Capabilities

- **Identity & Organization Platform (Phases 1 - 2)**: Multi-tenant organization isolation, JWT rotation, MFA, granular RBAC (`@RequirePermission`), user preferences, and audit logging.
- **Project & Repository Platform (Phases 3 - 4)**: GitHub/GitLab/Bitbucket OAuth 2.0 integration, AES-256-GCM token encryption, branch sync, commit stream analytics, PR governance, and secret scanning.
- **CI/CD Platform & Execution Engine (Phase 5)**: DAG stage resolution, Docker/K8s/Self-Hosted runner orchestration, live ANSI log streaming, artifact repositories, and zero-downtime deployment adapters.
- **Enterprise AI Platform (Phases 6.0 - 6.6)**:
  - **Phase 6.0 AIOps**: Incident detection and pre-flight risk prediction.
  - **Phase 6.1 Log Intelligence**: Intelligent log parsing, error clustering, and stack trace analysis.
  - **Phase 6.2 Root Cause Intelligence**: Cross-service dependency graphs and causal failure chains.
  - **Phase 6.3 Predictive Operations**: Failure forecasting, capacity prediction, and runner utilization metrics.
  - **Phase 6.4 Knowledge & Runbooks**: Versioned playbooks, historical incident similarity matching, and AI postmortem generation.
  - **Phase 6.5 Enterprise Copilot**: Multi-domain operational assistant with intent routing and context aggregation.
  - **Phase 6.6 Autonomous Operations**: 7-component remediation planning, Human Approval Gate enforcement, and execution delegation.

---

## 🚀 Quick Start (Local Docker Compose)

```bash
# 1. Clone repository
git clone https://github.com/cloudforge-ai/cloudforge-ai.git
cd cloudforge-ai

# 2. Configure environment
cp .env.example .env

# 3. Start complete stack
docker compose up --build -d

# Access endpoints:
# Mission Control UI: http://localhost:3000
# Backend API Health: http://localhost:8000/health
# OpenAPI Docs:       http://localhost:8000/swagger-ui/index.html
```

---

## 🧪 Automated Testing & Verification

```bash
# Backend Automated Test Suite (156 passing tests across 313 Java files)
cd services/api
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home ./mvn_dist/apache-maven-3.9.9/bin/mvn clean test -Dmaven.repo.local=./.m2/repository

# Frontend Typecheck, Lint & Next.js Production Build (55 SPA routes)
cd apps/web
npm run lint && npm run typecheck && npm run build
```

---

## 📄 License & Versioning

CloudForge AI is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.  
Current Version: **v1.0.0 (Officially Released)**
