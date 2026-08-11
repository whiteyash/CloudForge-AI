# CloudForge AI — Deployment & Infrastructure Guide

## System Requirements
- **JDK**: Java 21+ (Java 24 fully supported)
- **Node.js**: Node 20+ / Next.js 16
- **Database**: PostgreSQL 16+
- **Containerization**: Docker 25+ & Docker Compose v2

---

## Local Development Execution

### 1. Start Backend API Service (`services/api`)
```bash
cd services/api
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home ./mvn_dist/apache-maven-3.9.9/bin/mvn spring-boot:run
```
- API listens on: `http://localhost:8000`
- Swagger UI (OpenAPI): `http://localhost:8000/swagger-ui.html`

### 2. Start Mission Control Frontend (`apps/web`)
```bash
cd apps/web
npm run dev
```
- Web Dashboard listens on: `http://localhost:3000`

---

## Production Build & Verification

```bash
# Backend Test & Packaging
cd services/api
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home ./mvn_dist/apache-maven-3.9.9/bin/mvn clean package -DskipTests=false

# Frontend Production Build
cd apps/web
npm run lint && npm run typecheck && npm run build
```
