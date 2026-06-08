# Acme Mock Backend

Stand-in systems-of-record that return fixed mock **Accounts** and **Transactions**
for the Acme Banking demo system. The Orchestration Service calls this service;
it requires no auth, no real database, and no external dependencies.

## Quick Start

**Prerequisites:** JDK 21+

```bash
# Run the service (port 8080)
./gradlew bootRun

# Health check
curl http://localhost:8080/actuator/health
# → {"status":"UP"}
```

> **Full-system local port:** The docker-compose stack maps container port 8080
> to **host port 9091**. The Orchestration Service connects to `http://localhost:9091`.

## Build & Test

```bash
# Compile + test
./gradlew build

# Tests only
./gradlew test
```

## Project Layout

```
src/main/java/com/acmebank/mock/
  AcmeMockBackendApplication.java   ← entry point
src/main/resources/
  application.yml                   ← port + Jackson config
src/test/java/com/acmebank/mock/
  AcmeMockBackendApplicationTests   ← context-loads smoke test
```

See `CLAUDE.md` / `AGENT.md` for the full planned architecture and deferred work.
