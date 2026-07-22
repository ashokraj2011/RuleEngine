# Repository Core Summary — rule-engine-api

## What is this repository?

`rule-engine-api` is a small, single-module **Java 17 / Spring Boot 3.2.5** web
service that evaluates a JSON-defined rule grammar against an arbitrary JSON
data payload and returns a boolean result. It is an **application**, not a
library or monorepo — one Maven artifact, one deployable JAR. (evidence:
`ev-repo-purpose`, `ev-build-config`)

The rule grammar supports group operators (`all`/AND, `any`/OR, `not`) and
leaf conditions (`field`, `op`, `value`) with operators such as `eq`, `ne`,
`lt`, `lte`, `gt`, `gte`, `between`, `in`, `contains`, `regex`, `exists`,
`not_exists`, `isNull`, `isNotNull`.

## Major components

| Component ID | Name | Responsibility | Path |
|---|---|---|---|
| `comp-api` | REST API layer | HTTP endpoint + error mapping | `src/main/java/org/example/api` |
| `comp-rules-engine` | Rule evaluation engine | Recursive rule parsing/evaluation, comparisons | `src/main/java/org/example/rules` |
| `comp-bootstrap` | Spring Boot bootstrap | Process entrypoint, DI wiring | `src/main/java/org/example/RuleEngineApplication.java` |
| `comp-scratch-main` | IDE scratch class | Unused IntelliJ template leftover | `src/main/java/org/example/Main.java` |

`comp-bootstrap` starts the process and Spring wires `comp-api` to
`comp-rules-engine` via constructor injection of `RuleEngineService` into
`RuleEngineController`. `comp-scratch-main` is not referenced by the build,
tests, or the other components.

## Primary entry points

- **Server start**: `RuleEngineApplication.main` (`ep-spring-boot-main`) —
  `mvn spring-boot:run`, listens on port 8080 by default.
- **HTTP API**: `POST /api/v1/rule-engine/evaluate` (`ep-evaluate-endpoint`) —
  accepts `{ "data": {...}, "rule": <Rule> }`, returns `{ "result": bool }`.
- **Unused scratch entrypoint**: `Main.main` (`ep-main-scratch`) — an
  IntelliJ-generated demo class, not part of the running application.

## Primary technologies

- Java 17, Maven (single `pom.xml`, no submodules)
- Spring Boot 3.2.5 (`spring-boot-starter-web`, `spring-boot-starter-validation`)
- Jackson (`jackson-databind`) for JSON tree parsing (`JsonNode`/`ObjectNode`/`ArrayNode`)
- JUnit 5 + Spring Boot Test / MockMvc for testing

## Standard validation commands

| Command | Purpose |
|---|---|
| `mvn -q -DskipTests package` | Build the jar without running tests |
| `mvn spring-boot:run` | Run the service locally (port 8080) |
| `mvn test` | Run the JUnit test suite |

During this analysis, `mvn -o compile` was executed against the current
working tree and **succeeded (exit code 0)**. Tests were not executed as
part of this analysis, so no pass/fail claim is made about `mvn test`.

## Important risks

1. **Uncommitted, unimplemented operator.** The working tree has an
   uncommitted change (`git status` shows `M src/main/java/.../Operator.java`)
   adding a `sin` enum constant with **no corresponding case** in
   `RuleEngineService.evalCondition`'s switch. Using `op: "sin"` would compile
   fine but throw `IllegalArgumentException("Operator not implemented: sin")`
   at runtime. (`ev-operator-enum-gap`)
2. **Doc/code mismatch on validation error status.** README states 422 for
   validation errors, but `GlobalExceptionHandler` returns 400 for
   `MethodArgumentNotValidException`. (`ev-exception-handler`)
3. **Thin negative-path test coverage** — no tests for `ne`, standalone
   `lt`/`lte`/`gte`, `in`, `not` groups, or malformed-rule error handling.

## Important unknowns

- Whether `Main.java` should be deleted (appears to be dead scaffold code).
- Whether the uncommitted `sin` operator is in-progress work or accidental.
- Whether the README's 422 claim or the code's 400 behavior reflects the
  intended contract.

## Commit and freshness

Inspected commit: **`64b49487614835be2966c38dc2d5f5e629a466ef`** (branch
`main`, committed 2026-07-22). **Working tree is not clean** — see risk #1
above. This grounding reflects the state including that uncommitted change;
re-run analysis if the tree changes further.

## Recommended next view by task

| Task | Load next |
|---|---|
| Implement/debug/refactor/review code | `views/development.md` (generated) |
| Understand product/business behavior | `views/business.md` (not generated — see manifest `load_when`) |
| Evaluate design/dependencies/scalability | `views/architecture.md` (not generated) |
| Write/assess tests | `views/testing.md` (not generated) |
| Build/package/deploy/rollback | `views/release.md` (not generated) |
| Diagnose runtime/incidents | `views/operations.md` (not generated) |
| Auth/secrets/vulnerability analysis | `views/security.md` (not generated) |

This run generated **core + development** only, per auto-routing for an
unspecified/broad task. See `manifest.json` for `load_when` triggers to
generate other views on a future run with a concrete task or explicit view
request.
