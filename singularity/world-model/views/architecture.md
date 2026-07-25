# Architecture View

Audience: solution architects, technical leads, design agents.
Assumes `core/summary.md` and `core/model.json` are already loaded. Component IDs match `core/model.json`.

## System context
A single-process, stateless Spring Boot service exposing one HTTP endpoint. There are no upstream/downstream systems, databases, queues, or external service integrations in the repository. (observed, high — ev-002, ev-018)

```
[HTTP client] --POST /api/v1/rule-engine/evaluate--> [comp-api] --> [comp-rules-engine] --> boolean result
```

## Container / application map
One deployable unit: the `rule-engine-api` Spring Boot JAR (`comp-app-bootstrap` hosting `comp-api`, which uses `comp-rules-engine`). `comp-scratch-main` is dead/unused code not part of the running container. (observed, high — ev-003, ev-011)

## Component responsibilities
| Component | Responsibility | Evidence |
|---|---|---|
| `comp-app-bootstrap` | Boots the Spring context | ev-003 |
| `comp-api` | HTTP contract, bean validation, exception → HTTP status mapping | ev-004, ev-005, ev-006 |
| `comp-rules-engine` | Recursive rule-tree evaluation, operator dispatch, type coercion/comparison | ev-007, ev-008, ev-009, ev-010 |
| `comp-scratch-main` | None (unused scratch code) | ev-011 |

## Dependency graph
`comp-api` → `comp-rules-engine` (constructor injection of `RuleEngineService` into `RuleEngineController`, `RuleEngineController.java:19-21`). No other internal dependencies exist. `comp-rules-engine` has no dependency on Spring beyond the `@Service` stereotype annotation — its core logic is framework-agnostic. (observed, high — ev-004, ev-007)

## Interfaces and contracts
- **HTTP contract**: `POST /api/v1/rule-engine/evaluate`, JSON in/out, request = `{data: object, rule: Rule}`, response = `{result: boolean}`. (observed, high — ev-004, ev-005)
- **Internal contract**: `RuleEngineService.evaluate(Map<String,Object> data, JsonNode rule) -> boolean` is the sole internal API surface between `comp-api` and `comp-rules-engine`. (observed, high — ev-007)
- **Rule grammar** (the real "schema" of this system) is defined implicitly by `evaluate`/`evalCondition`/`Operator`, not by a formal JSON Schema file. (observed, high — ev-007, ev-008)

## Data ownership
No component owns persistent data. `data` and `rule` are transient, per-request inputs held only in memory for the duration of one HTTP call. There is no shared or cached state between requests. (observed, high — ev-018)

## Important runtime workflows
**Evaluate flow**: `RuleEngineController.evaluate` → `RuleEngineService.evaluate` → recursive descent:
- Object with `all`/`any` key → `evalGroup` (short-circuiting AND/OR over child rules).
- Object with `not` key → negate recursive `evaluate` of nested rule.
- Plain object → `evalCondition` (leaf), dispatches on `Operator` enum.
- Array → treated as implicit `all` group.
(observed, high — ev-007, ev-008, `RuleEngineService.java:17-61`)

## Security and trust boundaries
The only trust boundary is the HTTP listener itself; there is no authentication layer inside the application, so the trust boundary and the network boundary are effectively the same. See `views/security.md` for detail. (observed, high — ev-016)

## Scalability and performance signals
- Fully stateless request handling → horizontally scalable behind a load balancer with no session affinity requirements. (inferred, high — ev-018)
- Recursive tree evaluation depth is bounded only by the caller's rule JSON size/nesting; no explicit depth or size limit is enforced, which could allow deeply nested or very large rule payloads to consume excessive CPU/stack. (inferred, medium — ev-007)
- `regex` operator has no compiled-pattern cache or execution timeout — repeated identical patterns are recompiled every call, and pathological patterns can degrade latency. (inferred, medium — ev-017)

## Reliability and consistency behavior
No retries, circuit breakers, or timeouts are implemented (none needed, since there are no outbound calls). Errors surface synchronously as HTTP 400 via `GlobalExceptionHandler`; there is no fallback/default-decision behavior on error. (observed, high — ev-006)

## Architectural invariants
- `comp-api` must never contain rule-evaluation logic; all grammar semantics live in `comp-rules-engine`.
- The public HTTP contract (`{data, rule}` → `{result}`) is the only externally visible contract; internal method signatures are free to change.

## Architectural debt and risks
- Single monolithic `RuleEngineService` class (240 lines) mixes parsing, dispatch, path resolution, and type coercion — could be decomposed but is not currently a functional problem at this size. (observed, high — ev-007–ev-010)
- No abstraction/interface for `RuleEngineService` — `RuleEngineController` depends on the concrete class, limiting testability via mocking (though tests currently use the real implementation, which works fine at this scale). (observed, medium)
- Doc/code mismatch on HTTP status for validation errors (ev-006) is an architecture-adjacent contract risk for API consumers.

## Design decisions inferred from the repository
- JSON-tree-based rule representation (Jackson `JsonNode`) was chosen over a typed rule DSL/AST, favoring flexibility over compile-time safety. (inferred, medium — ev-007, ev-008)
- Comparison logic centralizes type coercion (`BigDecimal`, then `Instant`, then `Comparable`, then `String`) to support heterogeneous JSON value types without requiring callers to specify types explicitly. (inferred, high — ev-009)

## Areas requiring architectural confirmation
- Whether rule size/depth limits, request-rate limits, or an API gateway are intended to be added in front of this service in production.
- Whether the `regex` operator should be restricted (e.g., pattern allow-list, execution timeout) before being exposed to untrusted callers.

## Where to start
Read `RuleEngineService.evaluate` (`src/main/java/org/example/rules/RuleEngineService.java:17-61`) for the core recursive workflow, then `RuleEngineController.evaluate` for the HTTP boundary.

## Questions this view does not answer
- Concrete business scenarios and terminology → `views/business.md`.
- Line-by-line implementation guidance and conventions → `views/development.md`.
- Test coverage and gaps → `views/testing.md`.
- Detailed security posture → `views/security.md`.
