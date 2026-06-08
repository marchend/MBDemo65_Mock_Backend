# Acme Mock Backend — Project Context

## Overview
The Acme Mock Backend is a Spring Boot 3.3.x / Java 21 internal service that
returns fixed mock Accounts and Transactions for the Acme Banking demo system.
It acts as a stand-in core-banking system-of-record so the Orchestration Service
and downstream clients can be built and demoed against realistic data without a
real database or external dependency.

## Tech Stack
| Layer | Choice |
|---|---|
| Runtime | Java 21 (LTS) |
| Framework | Spring Boot 3.3.x — Spring MVC (`spring-boot-starter-web`) |
| Observability | Spring Boot Actuator (`/actuator/health`) |
| Build | Gradle 8 (Kotlin DSL) — `build.gradle.kts` |
| JSON | Jackson — canonical camelCase, ISO-8601 dates, `BigDecimal` plain |
| Auth | None — internal-only service |
| Test | JUnit 5 + Spring Boot Test (`@WebMvcTest` slices) |

## How to Run Locally
```bash
./gradlew bootRun
# Service starts on http://localhost:8080
# Health: GET http://localhost:8080/actuator/health → {"status":"UP"}
```
> Local host port for the full system: **9091** (container 8080 mapped → host 9091).
> The Orchestration Service expects `http://localhost:9091`.

## How to Run Tests
```bash
./gradlew test
# or full build + test:
./gradlew build
```

## Key Directory Structure
```
acme-mock-backend/
├── build.gradle.kts                          # Gradle 8 Kotlin DSL (source of truth)
├── settings.gradle.kts
├── gradlew / gradle/wrapper/                 # committed wrapper
├── src/main/java/com/acmebank/mock/
│   ├── AcmeMockBackendApplication.java       # @SpringBootApplication (implemented ✓)
│   ├── web/                                  # REST controllers (deferred)
│   │   ├── AccountController.java
│   │   ├── TransactionController.java
│   │   └── error/ApiExceptionHandler.java
│   ├── model/                                # Java records (deferred)
│   │   ├── Account.java
│   │   └── Transaction.java
│   └── data/                                 # In-memory fixtures (deferred)
│       └── MockDataStore.java
├── src/main/resources/application.yml        # server.port=8080, Jackson config (✓)
└── src/test/java/com/acmebank/mock/
    ├── AcmeMockBackendApplicationTests.java   # context-loads smoke test (✓)
    └── web/AccountControllerTest.java         # @WebMvcTest slice (deferred)
```

## Planned Architecture

### Entry Point (implemented in this PR)
- `AcmeMockBackendApplication` — `@SpringBootApplication`, boots on port 8080.
- `application.yml` — port + Jackson ISO-8601 config.
- `GET /actuator/health` — returns `{"status":"UP"}`.

### REST API (deferred — future PR)
Endpoints matching `openapi-mock-backend.yaml`:
- `GET /accounts?customerId={id}` → `AccountList`
- `GET /accounts/{accountId}` → `Account`
- `GET /accounts/{accountId}/transactions?limit&offset` → `TransactionList`
- `GET /transactions?customerId={id}&limit&offset` → `TransactionList`

JSON contract: camelCase keys, `BigDecimal` as plain number, ISO-8601 dates.

### Domain Models (deferred — future PR)
Java records: `Account` (accountId, customerId, displayName, accountType,
maskedNumber, currentBalance, availableBalance, currencyCode) and
`Transaction` (transactionId, accountId, description, amount, postedDate,
category, merchantName).

### Mock Data Fixtures (deferred — future PR)
`MockDataStore` — in-memory fixtures for customer `cust-alex`:
- Unlimited Chequing (4821, $4 287.52)
- High-Interest Savings (9203, $18 940.00)
- Visa Platinum (1188, −$612.34 / $9 387.66 available)
- Recent transactions: Coffee Bar −$4.75, Paycheck +$3 200.00, Grocery Mart −$86.43

### Error Handling (deferred — future PR)
`ApiExceptionHandler` — maps unknown `accountId` → HTTP 404 with
`application/problem+json` (RFC 7807).

### Testing Strategy (deferred — future PR)
- `@WebMvcTest` slice per controller asserting wire shape + status codes.
- `MockDataStore` unit test pinning fixture values (prevents silent drift).
- Target: ≥80% coverage.

### Container / Deployment (deferred — future PR)
Multi-stage Dockerfile (Temurin JRE 21). Local docker-compose maps container
port 8080 → host port 9091. CI/CD deployment phase emits these artifacts.

## Deferred Work
- AccountController + TransactionController — future PR
- Account + Transaction Java records — future PR
- MockDataStore with Home-screen fixtures (cust-alex) — future PR
- ApiExceptionHandler (RFC 7807 problem+json) — future PR
- @WebMvcTest controller slices (wire shape, 404) — future PR
- MockDataStore fixture unit tests — future PR
- Dockerfile (multi-stage, Temurin JRE 21) — future PR
- docker-compose / deploy/ config (host port 9091) — future PR
- OpenAPI contract validation (`openapi-mock-backend.yaml`) — future PR
- ≥80% test coverage — grows with feature PRs

## Keychain Note (N/A for this service)
This is a pure REST service — no Keychain APIs. The note is included for
consistency with the four-branch workflow docs.

## Git Workflow

> **Default PR target branch: `develop`.** Every feature/refactor/docs PR
> opens against `develop`. PRs are only opened against `qa`, `uat`, or
> `main` for explicit promotion PRs.

**Branch model (`develop` → `qa` → `uat` → `main`):**

| Branch  | Role                                 | Receives PRs from              | Promotes to |
|---------|--------------------------------------|--------------------------------|-------------|
| develop | Default integration branch           | feature branches               | qa          |
| qa      | First quality gate                   | develop (promotion PR)         | uat         |
| uat     | Pre-prod acceptance                  | qa (promotion PR)              | main        |
| main    | Production / release tags            | uat (promotion PR)             | tagged only |

All feature PRs MUST target `develop`. Never open a feature PR against
`qa`, `uat`, or `main`. Promotions happen via dedicated promotion PRs.
