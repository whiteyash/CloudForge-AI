# Development Setup & Verification Guide

## 🔧 Fixed Issues

### 1. JWT Secret Configuration
**Issue**: Backend failed to start with `Binding to target ai.cloudforge.api.auth.JwtProperties failed: must not be blank`

**Fix**: Updated `[services/api/src/main/resources/application.yaml](services/api/src/main/resources/application.yaml)` to provide a default JWT secret:
```yaml
cloudforge:
  jwt:
    secret: ${CLOUDFORGE_JWT_SECRET:c3VwZXItc2VjcmV0LWp3dC1rZXktY2xvdWRmb3JnZS1haS0zMi1ieXRlcyE=}
```

### 2. Maven Wrapper Installation
**Issue**: Backend required `./mvnw` script but it was missing

**Fixed Files**:
- Added [services/api/mvnw](services/api/mvnw) - Unix/Mac launcher script
- Added [services/api/mvnw.cmd](services/api/mvnw.cmd) - Windows launcher script  
- Added [services/api/.mvn/wrapper/maven-wrapper.properties](services/api/.mvn/wrapper/maven-wrapper.properties) - Configuration

These scripts delegate to the bundled Maven distribution at `./mvn_dist/apache-maven-3.9.9/`

## ✅ Verified Status

All systems build and pass tests:

| Component | Command | Status |
|-----------|---------|--------|
| Frontend | `npm run build` | ✅ PASSED |
| Frontend Lint | `npm run lint` | ✅ PASSED |
| Backend Tests | `./mvnw -q test` | ✅ BUILD SUCCESS (156 tests) |
| Backend Container | Docker Compose build | ✅ Successfully built |

## 📋 Prerequisites for Local Development

### Required Software
- **Java 21 or 24**: The project targets JDK 21 but works with JDK 24
  - Expected path: `/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home` (macOS)
- **PostgreSQL 16**: Database server
- **Redis 7**: Cache/session store
- **Node.js 18+**: For frontend development

### Environment Setup

```bash
# Set Java 24 for macOS
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# Verify
java -version
```

## 🚀 Running the Project

### Option 1: Docker Compose (Recommended)
```bash
# Start entire stack with all services
docker-compose up --build

# Access:
# - Frontend: http://localhost:3000
# - Backend API: http://localhost:8000
# - OpenAPI Docs: http://localhost:8000/swagger-ui/index.html
```

### Option 2: Local Development

#### Backend
```bash
cd services/api

# Set Java environment
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home

# Run server
./mvnw spring-boot:run

# Or run tests
./mvnw clean test
```

Requirements:
- PostgreSQL running on `localhost:5432` (default credentials: `cloudforge`/`cloudforge-secure-dev-pass`)
- Redis running on `localhost:6379`

#### Frontend
```bash
cd apps/web

# Development server with hot reload
npm run dev

# Production build
npm run build
npm start

# Type checking
npm run typecheck

# Linting
npm run lint
```

## 🧪 Testing

### Backend Unit Tests
```bash
cd services/api
./mvnw clean test -DskipTests=false

# With specific test class
./mvnw test -Dtest=HealthControllerTest
```

### Frontend Type Checking & Linting
```bash
cd apps/web
npm run typecheck    # TypeScript validation
npm run lint         # ESLint validation
npm run build        # Full build with optimizations
```

## 📊 Project Structure

```
cloudforge-ai/
├── apps/
│   └── web/                    # Next.js 16 frontend (React 19)
│       ├── app/               # App router
│       ├── components/        # React components
│       ├── hooks/             # Custom React hooks
│       └── lib/               # Utilities
├── services/
│   └── api/                   # Spring Boot 3.5 backend
│       ├── src/main/java      # 316 Java source files
│       ├── src/test/java      # 108 test files
│       ├── src/main/resources # Database migrations, configs
│       └── pom.xml            # Maven dependencies
├── k8s/                       # Kubernetes manifests
├── docs/                      # Architecture & design docs
└── compose.yaml               # Docker Compose config
```

## 🔐 Configuration

### JWT Secret
The JWT secret is configured in `services/api/src/main/resources/application.yaml`:
- Default (dev): `c3VwZXItc2VjcmV0LWp3dC1rZXktY2xvdWRmb3JnZS1haS0zMi1ieXRlcyE=`
- Production: Override via `CLOUDFORGE_JWT_SECRET` environment variable
- Decoded: `super-secret-jwt-key-cloudforge-ai-32-bytes!`

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cloudforge
    username: cloudforge
    password: change-me-for-local-development
```

Override via environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### Redis Configuration
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

Override via:
- `SPRING_DATA_REDIS_HOST`
- `SPRING_DATA_REDIS_PORT`

## 🐛 Troubleshooting

### Backend won't start: "Connection refused to postgres"
**Solution**: Ensure PostgreSQL is running on `localhost:5432`
```bash
# Using Docker
docker run -d --name pg16 -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:16

# Or use docker-compose
docker-compose up postgres
```

### Backend won't start: "Connection refused to redis"
**Solution**: Ensure Redis is running on `localhost:6379`
```bash
# Using Docker
docker run -d --name redis7 -p 6379:6379 redis:7

# Or use docker-compose
docker-compose up redis
```

### Frontend build fails
**Solution**: Clear cache and reinstall dependencies
```bash
cd apps/web
rm -rf node_modules package-lock.json .next
npm install
npm run build
```

### Tests failing with "No such file or directory: mvnw"
**Solution**: Ensure you're in the correct directory and the wrapper is executable
```bash
cd services/api
chmod +x mvnw mvnw.cmd
./mvnw test
```

## 📚 Documentation

- [Architecture Overview](docs/architecture/ARCHITECTURE.md)
- [API Documentation](docs/api/api-overview.md)
- [OpenAPI Specification](docs/api/OPENAPI_SPEC.md)
- [Database Schema](docs/database/SCHEMA.md)
- [Testing Strategy](docs/testing/TESTING_STRATEGY.md)
- [Deployment Guide](docs/deployment/DEPLOYMENT.md)
