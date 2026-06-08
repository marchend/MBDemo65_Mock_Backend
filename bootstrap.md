# Acme Mock Backend — Seed Architecture

> **Purpose:** Authoritative seed spec for agentic build-out of the **Acme Mock
> Backend** — the stand-in systems-of-record that return fixed mock **Accounts**
> and **Transactions** for the [Mock Banking demo](../mock-banking-system.md).
> The [Orchestration Service](../MockBankingOrchestrator/acme-orchestrator.md)
> calls it; the API contract is [`openapi-mock-backend.yaml`](openapi-mock-backend.yaml)
> — paste it into the API-implementation stories.

---

## 0. Where it sits

```
Orchestration Service ──internal HTTP──▶ Acme Mock Backend
                                          GET /accounts, /transactions
                                          fixed mock data (no real DB)
```

It is the simplest tier: **no auth, no orchestration, no real persistence** —
just canonical mock data that makes the Home screen real. It exists so the rest
of the stack can be built and demoed against something that behaves like a core
banking system without one.

---

## 1. Project Overview

| Item | Value |
|---|---|
| Runtime | Java 21 (LTS) |
| Framework | Spring Boot 3.3.x |
| Web stack | Spring MVC (`spring-boot-starter-web`) — plain blocking REST is fine here |
| Build tool | Gradle 8 (Kotlin DSL) — `build.gradle.kts` is the source of truth |
| JSON | Jackson — **canonical camelCase** (no naming strategy override), ISO-8601 dates, `BigDecimal` plain |
| Auth | **None** — internal-only service (see §4) |
| Observability | Spring Boot Actuator (`/actuator/health`) |
| Container | Temurin JRE 21, multi-stage Docker build |
| Group / Artifact | `com.acmebank` / `acme-mock-backend` |
| Service name | `acme-mock-backend` |
| Container port | `8080` · **local host port `9091`** (see deployment) |

**Generated, not hand-assembled.** Scaffold via [Spring Initializr](https://start.spring.io)
(`spring init`) with `web`, `actuator` — same mechanism as the BFF seed §1.5.
`build.gradle.kts` + the committed Gradle wrapper are the source of truth.

---

## 2. Folder Structure

```
acme-mock-backend/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew / gradle/                 # committed wrapper
├── Dockerfile                        # multi-stage (deployment phase emits this)
├── deploy/                           # per-env deploy config + local bundle
│
├── src/main/java/com/acmebank/mock/
│   ├── AcmeMockBackendApplication.java
│   ├── web/
│   │   ├── AccountController.java      # GET /accounts, /accounts/{id}, /accounts/{id}/transactions
│   │   ├── TransactionController.java  # GET /transactions
│   │   └── error/ApiExceptionHandler.java   # 404 → RFC 7807 problem+json
│   ├── model/
│   │   ├── Account.java                # canonical record (accountId, currentBalance, …)
│   │   └── Transaction.java
│   └── data/
│       └── MockDataStore.java          # in-memory fixtures (the screen's data)
│
├── src/main/resources/
│   └── application.yml                 # server.port=8080, jackson date config
│
└── src/test/java/com/acmebank/mock/
    └── web/AccountControllerTest.java  # @WebMvcTest slice
```

---

## 3. API — canonical domain data

Full contract: [`openapi-mock-backend.yaml`](openapi-mock-backend.yaml). Summary:

| Endpoint | Returns |
|---|---|
| `GET /accounts?customerId={id}` | `AccountList` |
| `GET /accounts/{accountId}` | `Account` |
| `GET /accounts/{accountId}/transactions?limit&offset` | `TransactionList` |
| `GET /transactions?customerId={id}&limit&offset` | `TransactionList` (recent across accounts — Home "Recent Activity") |

Field names are **canonical camelCase** — `accountId`, `currentBalance`,
`accountType: "CHEQUING"`. Do **not** snake_case here; that transform is the
BFF's job. Money is `BigDecimal` serialized as a plain JSON number; dates are
ISO-8601.

### Models (Java records)

```java
public record Account(
    String accountId, String customerId, String displayName,
    String accountType,        // CHEQUING | SAVINGS | CREDIT | INVESTMENT
    String maskedNumber, BigDecimal currentBalance,
    BigDecimal availableBalance, String currencyCode) {}

public record Transaction(
    String transactionId, String accountId, String description,
    BigDecimal amount,         // negative = debit
    Instant postedDate, String category, String merchantName) {}
```

---

## 4. No authentication — internal only

This service is reachable **only from the orchestrator inside the cluster**. It
has no security config: every request is served. That is correct for the demo —
the trust boundary is the cluster network, and the user's identity has already
been enforced upstream at the BFF and orchestrator (Okta JWT).

> **Production note (state it, don't pretend otherwise):** a real systems-of-record
> service would require service-to-service auth (mTLS or a client-credentials
> token) and would scope data by the authenticated caller. The demo omits this
> deliberately to keep the mock a mock — call it out when presenting.

`customerId` is accepted as a query parameter and used to key the fixtures; the
mock returns the same demo customer's data regardless, so any `customerId`
resolves to the Home-screen fixtures below.

---

## 5. Mock data — matches the Home screen exactly

`MockDataStore` holds the fixtures the screen renders (customer `cust-alex`):

| Account | type | masked | balance | available |
|---|---|---|---|---|
| Unlimited Chequing | CHEQUING | 4821 | 4287.52 | 4287.52 |
| High-Interest Savings | SAVINGS | 9203 | 18940.00 | 18940.00 |
| Visa Platinum | CREDIT | 1188 | −612.34 | 9387.66 |

Plus a handful of recent transactions (Coffee Bar −4.75, Paycheck +3200.00,
Grocery Mart −86.43, …) so "Recent Activity" has content. See the
`openapi-mock-backend.yaml` examples for the exact JSON.

Keep the fixtures in one place (`MockDataStore`) so a story can extend them
without touching controllers.

---

## 6. Configuration

```yaml
# application.yml
server:
  port: 8080
spring:
  jackson:
    serialization:
      write-dates-as-timestamps: false   # ISO-8601, not epoch
```

No per-environment secrets — there's nothing secret in a mock. The orchestrator
reaches it at `http://localhost:9091` locally (its container 8080 mapped to host
9091; see [the system doc's port plan](../mock-banking-system.md#local-port-plan-avoid-collisions)).

---

## 7. Testing Conventions

**Stack:** JUnit 5 + Spring Boot Test, ≥80% coverage. Keep it light — this is a mock.

- `@WebMvcTest` slice per controller asserting the **wire shape** (canonical
  camelCase keys, numeric money, ISO-8601 dates) + status codes (200, 404).
- A `MockDataStore` unit test pinning the fixture values so the Home-screen
  numbers can't silently drift.
- Unknown `accountId` → 404 `application/problem+json`.

---

## 8. Deployment

Containerized Spring Boot — the MothershipCode **CI/CD deployment phase** emits
the `Dockerfile`, `deploy.yml`, and local `docker-compose.yml` on **Add CI/CD**.
Configure the `develop` environment to deploy **locally** with **host port
9091** via the deployment editor. See
[the system doc](../mock-banking-system.md#local-auto-deployment--yes-the-platform-supports-this-now).
Build the Mock Backend **first** — the orchestrator depends on it being up.

---

## 9. Story Implementation Checklist

- [ ] Compiles clean: `./gradlew build`
- [ ] Endpoints match [`openapi-mock-backend.yaml`](openapi-mock-backend.yaml) (paths, params, schemas)
- [ ] Canonical camelCase JSON — no snake_case, no naming-strategy override
- [ ] Money is `BigDecimal` (plain number); dates are ISO-8601
- [ ] `@WebMvcTest` covers each endpoint's wire shape + 404 path
- [ ] Fixtures live in `MockDataStore` and match the Home screen numbers
- [ ] No auth, no real DB — it stays a mock