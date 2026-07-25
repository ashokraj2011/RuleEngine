# Testing View

Audience: QA engineers, test automation agents, validation agents, reviewers.
Assumes `core/summary.md` and `core/model.json` are already loaded.

## Test strategy found in the repository
Two layers only: (1) pure unit tests against `RuleEngineService` with in-memory `Map`/Jackson `ObjectNode` fixtures, and (2) one Spring `@SpringBootTest` + `MockMvc` integration test against the HTTP endpoint. No contract tests, no end-to-end tests, no performance/load tests exist. (observed, high — ev-012, ev-013)

## Test map
| Layer | File | Tests |
|---|---|---|
| Unit | `src/test/java/org/example/rules/RuleEngineServiceTest.java` | 7 |
| Integration (MockMvc) | `src/test/java/org/example/api/RuleEngineControllerTest.java` | 1 |
| Contract / E2E | none | 0 |

## Test commands
- `mvn test` — runs all tests.
- **Executed this session**: `mvn -q -DskipTests=false test` → **Tests run: 8, Failures: 0, Errors: 0** (Surefire reports: `RuleEngineServiceTest` 7/7 passing, `RuleEngineControllerTest` 1/1 passing). (observed, high — ev-014)
- Discovered but not individually re-verified beyond the full-suite run above: all 8 tests listed in the map.
- Not run: none — the full discovered suite was executed and passed.

## Test environment requirements
None beyond a JDK and Maven — no database, no network services, no test containers. `@SpringBootTest` spins up an in-process MockMvc context only (no real HTTP port bound). (observed, high — ev-013)

## Fixtures, factories, mocks, and fakes
No mocking framework usage (no Mockito). Tests build Jackson `ObjectNode`/`ArrayNode` fixtures directly via `ObjectMapper.createObjectNode()`/`createArrayNode()`, and plain Java `Map.of(...)`/`HashMap` for `data`. No shared test-fixture utility class exists — each test builds its own inline fixtures. (observed, high — ev-012, ev-013)

## Component → test mapping
| Component | Tests |
|---|---|
| `comp-rules-engine` | `RuleEngineServiceTest` (7 tests) |
| `comp-api` | `RuleEngineControllerTest` (1 test) |
| `comp-app-bootstrap` | Indirectly exercised by `@SpringBootTest` context load in `RuleEngineControllerTest` |

## Business workflow → test mapping
The single "Rule Evaluation" workflow (see `views/business.md`) is covered end-to-end by `RuleEngineControllerTest.evaluateEndpointWorks` (happy path only) and exercised at the logic level by all 7 `RuleEngineServiceTest` cases. (observed, high — ev-012, ev-013)

## Critical positive scenarios (covered)
- Simple `eq` comparison (`testSimpleEquality`)
- `gt` + `contains` combined under `all` (`testGreaterThanAndContains`)
- `between` + `regex` combined under `all` (`testBetweenAndRegex`)
- `exists` + `not_exists` combined under `all` (`testExistsAndNotExists`)
- `isNull` / `isNotNull` across present, null, and missing fields (`testIsNull`, `testIsNotNull`)
- `any` group evaluating to false (`testAnyGroupFalse`)
- HTTP happy path for `gte` via the controller (`evaluateEndpointWorks`)

## Critical negative and failure scenarios (gaps)
No tests found for:
- Null/missing `rule` or `data` in the request → expected 400/422 (README) — **not covered**, and note the doc/code mismatch (ev-006) is itself untested.
- Unknown operator string → `IllegalArgumentException` path — **not covered**.
- Malformed `between`/`in` value shape (non-array `value`) — **not covered**.
- `not` group operator — **not covered by any test**, despite being part of the grammar (ev-007).
(inferred, high — absence based on full read of both test files, ev-012, ev-013)

## Boundary and edge cases (gaps)
- `ne`, `lt`, `lte`, `gte`, `in` operators have **no dedicated unit test** (only `gt`, `eq`, `between`, `contains`, `regex`, `exists`, `not_exists`, `isNull`, `isNotNull` are tested). (observed, high — ev-012)
- Empty `all`/`any` arrays (identity-value short-circuit behavior in `evalGroup`, line 43) — **not covered**.
- Numeric precision at large `long`/`double` boundaries in `compare()` — **not covered**.
- Field paths through array-indexed segments — **not covered** (and not supported by `resolvePathInternal`).

## Concurrency, retry, and idempotency tests
None exist or are needed at the current architecture level — the service is stateless and synchronous with no shared mutable state. (observed, high — ev-018)

## Security-related tests
None. No tests exercise the `regex` operator with adversarial patterns, no tests probe for injection via `field` paths, and no tests exist for unauthenticated access (there is no auth to test). See `views/security.md`. (observed, high — ev-016, ev-017)

## Migration and compatibility tests
Not applicable — no persistence layer or schema exists.

## Coverage gaps summary
1. Negative/validation-error HTTP responses (400 vs 422 mismatch, ev-006) — untested.
2. `not` group operator — untested.
3. `ne`, `lt`, `lte`, `gte`, `in` operators — untested.
4. Empty-group identity behavior — untested.
5. Adversarial `regex` patterns — untested.

## Risk-based regression suite (recommended, not yet implemented)
Priority order for adding tests before further changes to `comp-rules-engine`:
1. `not` operator correctness (currently zero coverage of a documented grammar feature).
2. All untested comparison operators (`ne`, `lt`, `lte`, `gte`, `in`).
3. Controller-level validation-error tests (null `data`/`rule`) to lock in the actual (400) behavior or fix the docs.
4. Malformed `between`/`in` shape → expect `IllegalArgumentException`/400.

## Test-selection guide by changed path
| Changed path | Run |
|---|---|
| `rules/RuleEngineService.java`, `rules/Operator.java` | `mvn test -Dtest=RuleEngineServiceTest` |
| `api/RuleEngineController.java`, `api/dto/*`, `api/GlobalExceptionHandler.java` | `mvn test -Dtest=RuleEngineControllerTest` |
| Any change | `mvn test` (full suite; only 8 tests, negligible cost) |

## Where to start
Run `mvn test` first to reproduce the current green baseline (8/8 passing, verified this session), then add tests for the gaps listed above before or alongside any grammar change.

## Questions this view does not answer
- Business rationale for which scenarios matter most → `views/business.md`.
- Internal implementation detail of the methods under test → `views/development.md`.
- Security threat modeling beyond test-gap identification → `views/security.md`.
