# Changelog

All notable changes to the CloudForge AI platform will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-08-04

### Added
- **Foundation & Auth Platform**: JWT authentication, MFA, Organization provisioning, and server-enforced RBAC.
- **Project & Repository Platform**: Multi-git provider OAuth 2.0 integration (GitHub, GitLab, Bitbucket), AES-256-GCM token encryption, PR governance, commit velocity analytics, and secret scanning.
- **CI/CD Platform & Execution Engine**: DAG stage resolution, Docker/K8s/Self-Hosted runner orchestration, live ANSI log streaming, and zero-downtime deployment adapters.
- **Observability Platform**: DORA metrics tracking (Deployment Frequency, Lead Time, MTTR, Change Failure Rate) and executive dashboards.
- **Enterprise AI Platform (Phases 6.0 - 6.6)**:
  - Phase 6.0 AIOps & Incident Intelligence
  - Phase 6.1 Log Intelligence & Stack Trace Analysis
  - Phase 6.2 Root Cause Intelligence & Causal Graphs
  - Phase 6.3 Predictive Operations & Capacity Forecasting
  - Phase 6.4 Knowledge Base, Incident Playbooks & Automated Postmortems
  - Phase 6.5 Mission Control AI Copilot Assistant & Intent Router
  - Phase 6.6 Human-Approved Autonomous Operations & Approval Workflow Gate
- **Database**: 41 Flyway schema migrations (`V1` through `V41`).
- **Frontend**: Mission Control Next.js 16 SPA featuring 55 static and dynamic routes.
