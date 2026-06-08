# Bootstrap Plan — Acme Mock Backend

## In scope (this PR)

### Project name + tech stack decisions
- **Project:** `acme-mock-backend` (Group: `com.acmebank`)
- **Runtime:** Java 21 (LTS)
- **Framework:** Spring Boot 3.3.x with Spring MVC (`spring-boot-starter-web`) + Actuator
- **Build tool:** Gradle 8 (Kotlin DSL) — `build.gradle.kts` is the source of truth
- **Test runner:** JUnit 5 + Spring Boot Test (`@WebMvcTest`)
- **Rationale:** Exactly as specified in §1 of bootstrap.md — plain blocking REST, no auth, no DB

### Directory structure (bootstrap only)
```
acme-mock-backend/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/com/acmebank/mock/
│   │   │   └── AcmeMockBackendApplication.java   ← @SpringBootApplication entry point
│   │   └── resources/
│   │       └── application.yml                   ← server.port=8080, Jackson ISO-8601
│   └── test/
│       └── java/com/acmebank/mock/
│           └── AcmeMockBackendApplicationTests.java  ← context loads smoke test
├── bootstrap_plan.md
├── CLAUDE.md
├── AGENT.md
└── README.md
```

### Files this PR creates
| File | Purpose |
|------|---------|
| `build.gradle.kts` | Gradle 8 Kotlin DSL build, Spring Boot 3.3.x deps |
| `settings.gradle.kts` | Root project name |
| `gradlew` + `gradle/wrapper/*` | Committed Gradle wrapper (buildable without local Gradle install) |
| `src/main/java/…/AcmeMockBackendApplication.java` | `@SpringBootApplication` entry point — boots and serves |
| `src/main/resources/application.yml` | `server.port=8080`, Jackson date config |
| `src/test/java/…/AcmeMockBackendApplicationTests.java` | Single smoke test: Spring context loads |
| `CLAUDE.md` / `AGENT.md` | Project overview, planned architecture, deferred work, Git workflow |
| `README.md` | How to build and run |

### How to run locally
```bash
./gradlew bootRun
# App starts on http://localhost:8080
# Health check: http://localhost:8080/actuator/health
```

### How to run tests
```bash
./gradlew test
```

### Definition of Hello World
- `./gradlew bootRun` starts the service on port 8080
- `GET /actuator/health` returns `{"status":"UP"}` with HTTP 200
- One smoke test (`AcmeMockBackendApplicationTests.contextLoads()`) passes

---

## Out of scope — deferred to future work

- **AccountController** (`GET /accounts`, `/accounts/{id}`, `/accounts/{id}/transactions`) — future PR
- **TransactionController** (`GET /transactions?customerId`) — future PR
- **Account + Transaction Java records** (canonical models) — future PR
- **MockDataStore** with full Home-screen fixtures (cust-alex, 3 accounts, transactions) — future PR
- **ApiExceptionHandler** — RFC 7807 `problem+json` 404 responses — future PR
- **`@WebMvcTest` slices** per controller verifying wire shape (camelCase, BigDecimal, ISO-8601) — future PR
- **MockDataStore unit test** pinning fixture values — future PR
- **Dockerfile** multi-stage container build — future PR (CI/CD deployment phase)
- **docker-compose / deploy/** local bundle and per-env deploy config — future PR
- **OpenAPI spec compliance** (`openapi-mock-backend.yaml`) — validated in controller stories
- **80%+ test coverage** requirement — grows with feature PRs
