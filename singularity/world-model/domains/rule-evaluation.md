# Domain: Rule Evaluation

## Domain purpose
Evaluate a caller-supplied JSON rule tree against caller-supplied JSON data and return a single boolean verdict. This is the entire business/technical capability of the repository. (observed, high — ev-001, ev-007)

## Terminology
| Term | Definition | Evidence |
|---|---|---|
| Rule | A JSON value that is either a group (`all`/`any`/`not`) or a leaf condition (`field`/`op`/`value`) | ev-007 |
| Group | `{"all": [...]}` (AND), `{"any": [...]}` (OR), `{"not": <rule>}` (negation), or a bare top-level array (implicit AND) | ev-007 |
| Condition | `{"field": "<dot.path>", "op": "<operator>", "value": <any>}` | ev-008 |
| Operator | One of: `eq`, `ne`, `lt`, `lte`, `gt`, `gte`, `between`, `in`, `contains`, `regex`, `exists`, `not_exists`, `isNull`, `isNotNull` | ev-008 |
| Field path | Dot-separated string resolved by walking nested `Map<String,Object>` values | ev-010 |

## Business rules
- `all` short-circuits to `false` on the first failing child; `any` short-circuits to `true` on the first passing child. (observed, high — ev-007, `RuleEngineService.java:41-61`)
- Empty group arrays return the logical identity: `true` for `all`, `false` for `any`. (observed, high — ev-007, line 43)
- `exists`/`not_exists`/`isNull`/`isNotNull` ignore the `value` field entirely. (observed, high — ev-008, README.md "Notes")
- `isNull` is true for both an explicit `null` value and a missing field path; `isNotNull` is its exact logical inverse. (observed, high — `RuleEngineServiceTest.testIsNull/testIsNotNull`, ev-012)
- `between` requires `value` to be a 2-element array `[min, max]`, inclusive on both ends (`compare(left,min) >= 0 && compare(left,max) <= 0`). (observed, high — ev-008, `RuleEngineService.java:88-94`)
- `contains` behaves differently by type of `left`: substring check for `String`, element-equality check for `Collection`; returns `false` for any other type or when `left` is `null`. (observed, high — ev-008, `RuleEngineService.java:103-114`)
- Value comparison order: numeric (`BigDecimal`) → ISO-8601 instant → `Comparable` → string fallback. (observed, high — ev-009)

## Owning components
`comp-rules-engine` (`src/main/java/org/example/rules/`) owns all domain logic. `comp-api` is a thin adapter that has no domain knowledge of its own. (observed, high — ev-007, ev-004)

## Important symbols
- `RuleEngineService.evaluate` — entrypoint, dispatches on JSON node type (`RuleEngineService.java:17-39`)
- `RuleEngineService.evalGroup` — AND/OR short-circuit (`RuleEngineService.java:41-61`)
- `RuleEngineService.evalCondition` — leaf operator dispatch (`RuleEngineService.java:63-135`)
- `RuleEngineService.compare` — cross-type comparison (`RuleEngineService.java:187-212`)
- `RuleEngineService.resolvePathInternal` — dot-path resolution (`RuleEngineService.java:172-184`)
- `Operator` — enum of valid `op` values (`Operator.java:3-14`)

## Entry points
`POST /api/v1/rule-engine/evaluate` (`RuleEngineController.evaluate`, `RuleEngineController.java:24-27`) is the only way to invoke this domain from outside the JVM. Internally, `RuleEngineService.evaluate(Map<String,Object>, JsonNode)` is the programmatic entrypoint. (observed, high — ev-004, ev-007)

## Main workflows
1. **Simple condition**: field path resolved → operator applied → boolean returned.
2. **Grouped condition**: recursive descent through nested `all`/`any`/`not`, each child re-invoking `evaluate`.
3. **Top-level array**: treated as an implicit `all` group for convenience (README.md "Top-level array (implicit AND)" example). (observed, high — ev-007)

## Data and state
Stateless — no rule or evaluation result is stored between requests. `data` is a `Map<String,Object>` built by Jackson from the request JSON; `rule` remains a Jackson `JsonNode` tree throughout evaluation (not converted to a typed AST). (observed, high — ev-018, ev-007)

## External integrations
None. This domain has zero external system dependencies (no DB, no cache, no message broker, no third-party API). (observed, high — ev-018)

## Invariants
- The response `result` field is always a strict boolean — never `null`, never an error object mixed into a 200 response.
- Any structurally invalid rule (unknown operator, wrong value shape, null rule) throws `IllegalArgumentException` rather than returning a default boolean — errors are never silently coerced to `false`. (observed, high — ev-007, ev-008)

## Tests
7 unit tests in `RuleEngineServiceTest` cover `eq`, `gt`, `contains`, `all`, `any` (false case), `between`, `regex`, `exists`, `not_exists`, `isNull`, `isNotNull`. 1 integration test in `RuleEngineControllerTest` covers the HTTP happy path for `gte`. **Not covered**: `ne`, `lt`, `lte`, `gte` (unit-level), `in`, `not` group operator, malformed-input error paths. (observed, high — ev-012, ev-013; see `views/testing.md` for full gap analysis)

## Change risks
- Changing `compare()`'s coercion precedence affects every operator that uses it (`eq`, `ne`, `lt`, `lte`, `gt`, `gte`, `between`, `in`) simultaneously — highest blast radius in the domain. (inferred, high — ev-009)
- Adding array-index support to field paths would change `resolvePathInternal` semantics and could alter `exists`/`isNull` results for currently-unsupported paths. (inferred, medium — ev-010)

## Unknowns
- Whether callers expect the `not` operator's untested behavior to match its apparent implementation (simple negation) — no test currently proves this.
- Whether extremely large numeric values lose precision through the `Number.doubleValue()` conversion path in `toBigDecimalOrNull` (`RuleEngineService.java:214-226`).

## Evidence IDs
ev-001, ev-004, ev-005, ev-006, ev-007, ev-008, ev-009, ev-010, ev-012, ev-013, ev-018
