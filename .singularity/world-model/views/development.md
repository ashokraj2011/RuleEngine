# Development View — rule-engine-api

Audience: developers, debugging agents, refactoring agents, code-review agents.
Assumes `core/summary.md` and `core/model.json` are already loaded.

## Where to start

| Change type | Start here |
|---|---|
| New/changed operator | `Operator` enum + `RuleEngineService.evalCondition` switch |
| New/changed comparison semantics | `RuleEngineService.compare`, `toBigDecimalOrNull`, `toInstantOrNull` |
| Request/response shape | `EvaluateRequest`, `EvaluateResponse`, `RuleEngineController` |
| Error handling / status codes | `GlobalExceptionHandler` |
| App startup / config | `RuleEngineApplication` |

## Source tree map

```
src/main/java/org/example/
  RuleEngineApplication.java   – @SpringBootApplication entrypoint (comp-bootstrap)
  Main.java                    – unused IDE scratch class (comp-scratch-main, do not extend)
  api/
    RuleEngineController.java  – POST /api/v1/rule-engine/evaluate (comp-api)
    GlobalExceptionHandler.java– maps exceptions -> HTTP responses
    dto/EvaluateRequest.java   – {data: Map<String,Object>, rule: JsonNode}, @NotNull validated
    dto/EvaluateResponse.java  – {result: boolean}
  rules/
    RuleEngineService.java     – recursive evaluator (comp-rules-engine)
    Operator.java               – enum of supported condition operators
src/test/java/org/example/
  api/RuleEngineControllerTest.java   – MockMvc, one happy-path HTTP test
  rules/RuleEngineServiceTest.java    – unit tests, no Spring context
```

## Important modules and symbols

- `RuleEngineService.evaluate(Map<String,Object> data, JsonNode rule)` —
  top-level dispatch: null/object/array handling; throws
  `IllegalArgumentException` for null or unsupported rule node types.
  (`src/main/java/org/example/rules/RuleEngineService.java:17-39`, evidence `ev-service-dispatch`)
- `evalGroup(data, arrNode, andLogic)` — iterates children with short-circuit
  AND/OR; empty array follows identity (`true` for AND, `false` for OR).
  (`RuleEngineService.java:41-61`)
- `evalCondition(data, cond)` — resolves `field`/`op`/`value`, parses `op` via
  `Operator.valueOf`, then a `switch` implements each operator.
  (`RuleEngineService.java:63-135`, evidence `ev-service-condition`)
- `Operator` enum — exactly 14 constants: `eq, ne, lt, lte, gt, gte, contains,
  in, regex, between, exists, not_exists, isNull, isNotNull`. **No math or
  trigonometric operator (`sin`, `tan`, etc.) exists at the current commit** —
  an earlier, uncommitted `sin` addition seen in a prior grounding pass is not
  present in this tree. (`Operator.java:1-14`, evidence `ev-operator-current-state`)
- `resolvePath` / `resolvePathInternal` — dot-path traversal over nested
  `Map`s only; does not descend into `List`s. (`RuleEngineService.java:168-184`)
- `compare(a, b)` — numeric (BigDecimal) → ISO-8601 instant → `Comparable` →
  string fallback, in that order. (`RuleEngineService.java:187-212`, evidence `ev-service-compare`)

## Entrypoints and initialization

- Server: `RuleEngineApplication.main` → `SpringApplication.run(...)`. No
  custom `@Configuration`, `application.properties`, or profiles were found —
  the app runs with Spring Boot defaults (port 8080).
- HTTP: `RuleEngineController` is the only `@RestController`; it is
  constructor-injected with `RuleEngineService` (a `@Service` singleton bean,
  no injected dependencies itself).

## Common implementation flows

**Add a new operator** (e.g. `tan`):
1. Add the constant to the `Operator` enum (`Operator.java`) — no such
   constant exists today for any math/trig function.
2. Add a matching `case tan:` branch in `RuleEngineService.evalCondition`
   implementing the desired numeric semantics (likely via
   `toBigDecimalOrNull`/`Math.tan`, then reuse or extend `compare(...)`).
   Decide radians vs. degrees and tolerance for floating-point comparison —
   undecided in the repository today (see `task-guides/implement-tan-math-operator.md`).
3. Add unit tests in `RuleEngineServiceTest` (happy path + edge cases: non-numeric
   field, missing value, asymptotic input).
4. Add an HTTP-level test in `RuleEngineControllerTest` if the operator has
   endpoint-visible edge cases.
5. Update README "Full Operator Reference" section.

**Add a new group operator**: extend the `if/else if` chain in
`RuleEngineService.evaluate` (currently `all`/`any`/`not`) and `evalGroup` if
new short-circuit semantics are needed.

**Change error response shape/status**: edit `GlobalExceptionHandler`; note
the existing README/code mismatch (422 documented vs 400 actual for
validation errors, evidence `ev-exception-handler`) — reconcile intentionally
rather than silently changing one side.

## Dependency injection / composition

Simple two-bean graph: `RuleEngineController` ← `RuleEngineService`, both
managed by Spring component scanning from `RuleEngineApplication`'s package.
No repositories, no external clients, no configuration classes observed.

## Error-handling conventions

- Domain/validation failures inside `RuleEngineService`/`evalCondition` throw
  `IllegalArgumentException` with a descriptive message (e.g. "Unknown
  operator: X", "between requires array [min, max]").
- `@Valid` on `EvaluateRequest` triggers `MethodArgumentNotValidException` for
  null `data`/`rule`.
- Both exception types are caught by `GlobalExceptionHandler` and turned into
  `{error, message}` JSON bodies with **HTTP 400** in both cases (observed;
  README's claim of 422 for validation errors is not borne out by the code).

## Logging and observability conventions

No logging framework usage, structured logs, metrics, or tracing were found
in `src/main`. This is a gap, not a convention — treat "no logging" as
`unknown/absent` rather than an established pattern.

## Configuration-loading behavior

No `application.properties`/`.yml`, `@ConfigurationProperties`, or
environment-variable reads were found. The app currently runs on Spring
Boot's built-in defaults only.

## Persistence access patterns

None observed. All evaluation is in-memory against the request-supplied
`data` map; there is no database, cache, or file I/O in `src/main`.

## Coding and naming conventions

- Package-by-layer: `api` (HTTP), `api.dto` (request/response shapes),
  `rules` (domain logic).
- Enum for closed operator set (`Operator`), parsed defensively via
  `Operator.valueOf` inside a try/catch that rethrows as
  `IllegalArgumentException`.
- Plain POJO DTOs with getters/setters (no records used) for Jackson binding.

## Generated-code boundaries

None. No generated sources, annotation-processor output, or build-time
codegen directories were found under `src/`.

## Change-impact guide

| If you touch... | Also check |
|---|---|
| `Operator` enum | `RuleEngineService.evalCondition` switch must have a matching `case` for every enum value, or it silently falls to `default: throw` at runtime (compiler does not enforce exhaustiveness here). No enum/switch drift currently exists — keep this invariant when adding `tan` or any new operator. |
| `compare()` | Both `eq`/`ne`/`lt`/`lte`/`gt`/`gte`/`between`/`in` semantics depend on it; re-run `RuleEngineServiceTest` fully. |
| `resolvePathInternal` | Affects `exists`/`not_exists`/`isNull`/`isNotNull` and all field resolution; note it only traverses nested `Map`s, not `List` indices. |
| `EvaluateRequest`/`EvaluateResponse` | Update README request/response schema section and `RuleEngineControllerTest`. |
| `GlobalExceptionHandler` | Update README's documented status codes to match, or vice versa. |

## Debugging starting points

1. Reproduce with the smallest possible `{data, rule}` payload via the
   `/evaluate` endpoint or directly in a `RuleEngineServiceTest`-style unit test.
2. For "Unknown operator" or "Operator not implemented" errors, check
   `Operator` enum vs. the `evalCondition` switch for a missing `case`.
3. For unexpected comparison results, inspect `compare()`'s three fallback
   tiers (BigDecimal → Instant → Comparable/string) — type coercion surprises
   usually originate here.

## Validation commands

```
mvn -q -DskipTests package   # build only
mvn test                      # run JUnit tests (controller + service)
mvn spring-boot:run            # run locally on :8080
```
`mvn -o compile` was executed against the current working tree at commit
`a007ae82` during this pass and succeeded (exit 0). `mvn test` has not been
executed in any grounding pass.

## Known implementation hotspots

- `evalCondition`'s single large `switch` is the central place all operator
  behavior lives — this is the extension point for a `tan` operator, and any
  future enum drift would surface here.
- `compare()`'s implicit type-coercion chain is a common source of subtle
  bugs (e.g., numeric strings vs. numbers, date strings vs. plain strings).

## Questions this view does not answer

- Business meaning/ownership of specific rules or fields (see `views/business.md`, generated this run).
- Deployment topology, environments, or release process (see `views/release.md`, not generated).
- Test coverage adequacy in depth or a full test inventory (see `views/testing.md`, not generated).
- Security/authn/authz posture of the endpoint (see `views/security.md`, not generated).
