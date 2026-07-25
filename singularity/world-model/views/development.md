# Development View

Audience: developers, debugging agents, refactoring agents, code-review agents.
Assumes `core/summary.md` and `core/model.json` are already loaded.

## Developer setup
- Requirements: Java 17+, Maven 3.8+ (README.md:14-16). This session ran successfully with Java 25/26 Temurin against the Spring Boot 3.2.5 parent — no compatibility failures were observed, but the pom's `<java.version>17</java.version>` is the declared target. (observed, high — ev-002, ev-014)
- Build: `mvn -q -DskipTests package`
- Run: `mvn spring-boot:run` (port 8080 by default)
- Test: `mvn test`

## Source tree map
```
src/main/java/org/example/
  Main.java                      # unrelated scratch class (comp-scratch-main) — do not extend
  RuleEngineApplication.java     # comp-app-bootstrap — Spring Boot entrypoint
  api/
    RuleEngineController.java    # comp-api — HTTP endpoint
    GlobalExceptionHandler.java  # comp-api — error → HTTP status mapping
    dto/
      EvaluateRequest.java       # comp-api — request DTO (data, rule)
      EvaluateResponse.java      # comp-api — response DTO (result)
  rules/
    RuleEngineService.java       # comp-rules-engine — grammar parsing + evaluation
    Operator.java                # comp-rules-engine — supported operator enum
src/test/java/org/example/
  api/RuleEngineControllerTest.java   # MockMvc integration test
  rules/RuleEngineServiceTest.java    # unit tests for evaluate()
```

## Important modules and symbols
| Symbol | Location | Role |
|---|---|---|
| `RuleEngineController.evaluate` | `api/RuleEngineController.java:24-27` | HTTP handler |
| `RuleEngineService.evaluate` | `rules/RuleEngineService.java:17-39` | Rule-tree dispatch (object/array/group) |
| `RuleEngineService.evalGroup` | `rules/RuleEngineService.java:41-61` | AND/OR short-circuit logic |
| `RuleEngineService.evalCondition` | `rules/RuleEngineService.java:63-135` | Leaf condition operator dispatch |
| `RuleEngineService.compare` | `rules/RuleEngineService.java:187-212` | Cross-type value comparison |
| `RuleEngineService.resolvePathInternal` | `rules/RuleEngineService.java:172-184` | Dot-path field resolution over `Map` |
| `Operator` | `rules/Operator.java:3-14` | Enum of 14 supported operators |

## Entrypoints and initialization
`RuleEngineApplication` (`@SpringBootApplication`) boots the context; Spring auto-registers `RuleEngineController` (`@RestController`) and `RuleEngineService` (`@Service`) via component scanning under `org.example`. No explicit `@Configuration` classes, profiles, or `application.properties`/`application.yml` exist. (observed, high — ev-003)

## Common implementation flows
**Add a new operator**:
1. Add the enum constant to `Operator.java`.
2. Add a `case` in `RuleEngineService.evalCondition`'s switch (`RuleEngineService.java:79-134`).
3. Add unit tests in `RuleEngineServiceTest.java` mirroring existing patterns (build `ObjectNode` via Jackson `ObjectMapper`, call `service.evaluate(data, rule)`).
4. Update `README.md` operator reference and examples.

**Add a new HTTP-level behavior** (e.g., new error code): modify `GlobalExceptionHandler` and add a controller test in `RuleEngineControllerTest.java`.

## Dependency injection / composition
Constructor injection only: `RuleEngineController(RuleEngineService ruleEngineService)` (`RuleEngineController.java:19-21`). No interfaces are used — direct concrete-class injection. No composition/factory patterns beyond standard Spring bean wiring.

## Error-handling conventions
- Business/validation failures throw `IllegalArgumentException` from deep within `RuleEngineService` (e.g., unknown operator, wrong value shape) and are caught centrally by `GlobalExceptionHandler`, not locally. (observed, high — ev-006, ev-007, ev-008)
- Bean-validation failures (`@NotNull` on `EvaluateRequest` fields) throw `MethodArgumentNotValidException`, also centrally handled.
- No checked exceptions or custom exception hierarchy exist — all errors are `IllegalArgumentException` or framework exceptions.

## Logging and observability conventions
No explicit `Logger` usage anywhere in `src/main`. Only Spring Boot's default framework startup logs appear. There are no correlation IDs, structured logs, or metrics. (observed, high — ev-019)

## Configuration-loading behavior
No `application.properties`/`application.yml`, profiles, or externalized config exist. All behavior is compiled-in; the only "configuration" is the hardcoded base path `/api/v1/rule-engine` on the controller's `@RequestMapping`. (observed, high — ev-004)

## Persistence access patterns
None. No repositories, JPA entities, or JDBC/JDBC-adjacent code exist anywhere in the codebase. (observed, high — ev-018)

## Coding and naming conventions
- Package-by-layer (`api`, `api.dto`, `rules`), not package-by-feature.
- DTOs are plain mutable POJOs with getters/setters (no records, no Lombok).
- Enum names for operators intentionally match the JSON `op` string values exactly (`Operator.valueOf(opStr)`, `RuleEngineService.java:72`) — renaming an enum constant is a breaking API change.
- Private helper methods in `RuleEngineService` are grouped by concern (parsing → dispatch → path resolution → comparison).

## Generated-code boundaries
None observed. No generated sources, no annotation-processor output directories referenced in `pom.xml`.

## Change-impact guide
| If you change... | You must also check |
|---|---|
| `Operator` enum values | `evalCondition` switch, README operator table, `RuleEngineServiceTest` |
| `evalGroup` short-circuit logic | All group (`all`/`any`/`not`) test cases |
| `compare()` coercion order | Any test relying on numeric/date/string comparison edge cases |
| `EvaluateRequest`/`EvaluateResponse` shape | `RuleEngineControllerTest`, README request/response schema section |
| HTTP status codes in `GlobalExceptionHandler` | README error-response section (currently mismatched, ev-006) |

## Debugging starting points
- Reproduce input via `RuleEngineServiceTest`-style unit test (fastest feedback, no Spring context).
- For HTTP-layer issues, use `RuleEngineControllerTest`'s MockMvc pattern.
- `IllegalArgumentException` messages thrown in `evalCondition`/`evalGroup` indicate exactly which grammar rule failed validation — check the message text first.

## Validation commands
- `mvn test` — full suite (verified: 8/8 passing this session, ev-014).
- `mvn -q -DskipTests package` — compile-only check.

## Known implementation hotspots
- `RuleEngineService.compare` (lines 187-239) — the most complex method; touches numeric, date, and fallback comparison paths simultaneously. High-risk for regressions when adding new operators.
- `resolvePathInternal` (lines 172-184) — silently returns `null` for any non-`Map` intermediate node; does not support array-indexed paths.

## Where to start
For any behavior change, start at `RuleEngineService.evaluate` (`rules/RuleEngineService.java:17`) and trace into `evalGroup`/`evalCondition`/`compare` as needed.

## Questions this view does not answer
- Business rationale for the operator set → `views/business.md`.
- System-level scalability/coupling concerns → `views/architecture.md`.
- Test coverage gaps and risk-based test selection → `views/testing.md`.
- Security posture of user-supplied regex/data → `views/security.md`.
