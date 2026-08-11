# Contributing to CloudForge AI

Thank you for your interest in contributing to CloudForge AI! We welcome contributions from developers, DevOps engineers, site reliability engineers, and technical writers.

---

## 📜 Code of Conduct

Please review and adhere to our [Code of Conduct](CODE_OF_CONDUCT.md) in all project interactions.

---

## 🛠️ How to Contribute

### 1. Reporting Bugs
- Search existing [GitHub Issues](https://github.com/cloudforge-ai/cloudforge-ai/issues) to verify the bug hasn't already been reported.
- Open a new issue containing a clear title, description, reproduction steps, expected behavior, and system environment details.

### 2. Suggesting Enhancements
- Feature requests and improvements should be submitted via GitHub Issues with the `enhancement` label.

### 3. Pull Request Process
1. Fork the repository and create your feature branch:
   ```bash
   git checkout -b feature/amazing-feature
   ```
2. Ensure your changes follow the existing project formatting and architectural contracts:
   - Backend: Spring Boot 3.5 / JDK 24 conventions with standard DDD package separation.
   - Frontend: Next.js 16 App Router, Tailwind CSS, TypeScript strict mode.
3. Run the automated test suite and lint checks:
   ```bash
   # Backend
   cd services/api && ./mvn_dist/apache-maven-3.9.9/bin/mvn test

   # Frontend
   cd apps/web && npm run lint && npm run typecheck && npm run build
   ```
4. Commit your changes with clear, descriptive commit messages.
5. Push to your branch and submit a Pull Request.

---

## 📄 License
By contributing to CloudForge AI, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE).
