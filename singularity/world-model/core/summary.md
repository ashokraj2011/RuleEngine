# Repository Core Summary

## Repository purpose
This repository (`rule-engine-api`) is a lightweight **Spring Boot REST API** that evaluates a JSON-defined rule grammar against arbitrary JSON input data and returns a boolean result. It is a small, single-purpose backend service, not a full product suite. (observed, high — README.md, pom.xml)

## Repository type
Single-module Maven application (not a monorepo, not a multi-service system). One deployable JAR built from one `pom.xml`.

## Main applications, packages, or services
- **rule-engine-api** — the sole deployable Spring Boot service.
  - `org.example` — application bootstrap.
  - `org.example.api` — REST layer (controller, DTOs, error handling).
  - `org.example.rules` — rule evaluation engine (business logic).
- `singularity/` is Singularity Flow SDLC governance tooling (workflow, personas, templates) — not application code and out of scope for architecture/business analysis. (observed, high — ev-015)

## High-level component map
| Component ID | Name | Responsibility |
|---|---|---|
| `comp-app-bootstrap` | Application bootstrap | Starts the Spring Boot context |
| `comp-api` | REST API layer | HTTP contract, request validation, error mapping |
| `comp-rules-engine` | Rule evaluation engine | Parses/evaluates the JSON rule grammar against data |
| `comp-scratch-main` | Scratch `Main.java` | Unrelated IDE-generated demo class, not part of the running application |

`comp-api` depends on `comp-rules-engine`. `comp-app-bootstrap` wires the Spring context that hosts `comp-api`. `comp-scratch-main` has no relationship to the other components. (observed, high — ev-003, ev-004, ev-011)

## Main entry points
- **HTTP**: `POST /api/v1/rule-engine/evaluate` — the only REST endpoint (`RuleEngineController.evaluate`, `src/main/java/org/example/api/RuleEngineController.java:23-27`).
- **Process**: `RuleEngineApplication.main` — Spring Boot startup (`src/main/java/org/example/RuleEngineApplication.java:8-10`).
- **Unrelated**: `Main.main` — an IntelliJ scratch/demo `main` method not wired into the Spring app (`src/main/java/org/example/Main.java`).

## Primary technologies
Java 17 (built/tested locally with a newer JDK, see risks), Spring Boot 3.2.5 (web, validation starters), Jackson (`jackson-databind` for JSON tree parsing), JUnit 5 + Spring Boot Test (MockMvc) for tests. Maven is the build tool. (observed, high — ev-002)

## Standard build and test commands
- Build: `mvn -q -DskipTests package`
- Run: `mvn spring-boot:run` (serves on `http://localhost:8080` by default)
- Test: `mvn test`
- Verified in this session: `mvn test` → **8 tests run, 0 failures, 0 errors** (7 in `RuleEngineServiceTest`, 1 in `RuleEngineControllerTest`). (observed, high — ev-014)

## Important risks
- No authentication/authorization on the `/evaluate` endpoint; it is fully open. (observed, high — ev-016)
- The `regex` operator compiles user-supplied patterns with no complexity/timeout guard — a possible ReDoS surface. (inferred, medium — ev-017)
- README documents 422 for validation errors, but the code returns 400 for `MethodArgumentNotValidException` — a doc/code mismatch. (observed, high — ev-006)
- No CI/CD, Dockerfile, or deployment manifests exist in the repository. (observed, high — ev-020)

## Important unknowns
- Whether the service is deployed anywhere, and under what infrastructure/environment configuration (no config files found).
- Whether `Main.java` is intentionally retained or leftover scaffolding.
- Actual precision/rounding behavior of numeric coercion at very large `long`/`double` boundaries (compare uses `Number.doubleValue()`).

## Commit and freshness
- **Commit inspected**: `56026842b14b2f6f75a4174bf45e3f2eac5444d5`
- Working tree was clean at inspection time; detached HEAD (not on a named branch).
- ⚠️ This model reflects a single point-in-time snapshot. Re-verify against the current commit before relying on it for decisions.

## Recommended next view by task
| Task | Load |
|---|---|
| Understand product behavior / business impact | `views/business.md` |
| Evaluate design, boundaries, dependencies | `views/architecture.md` |
| Implement, debug, refactor code | `views/development.md` |
| Write/assess tests | `views/testing.md` |
| Security review (auth, secrets, input handling) | `views/security.md` |
| Deep rule-grammar/domain detail | `domains/rule-evaluation.md` |
