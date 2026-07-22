# Domain: Rule Evaluation

## Domain purpose

Evaluate a JSON-defined boolean rule expression against an arbitrary JSON
data payload and return a single boolean result. This is the sole business
capability of the repository. (evidence: `ev-repo-purpose`, `ev-service-dispatch`)

## Terminology

| Term | Meaning |
|---|---|
| **Rule** | A JSON object or array describing a boolean expression: a group (`all`/`any`/`not`) or a leaf condition (`field`/`op`/`value`). |
| **Group** | `all` (AND, short-circuit on first false), `any` (OR, short-circuit on first true), `not` (negation of a single nested rule). |
| **Condition** | Leaf rule: `{ "field": "<dot.path>", "op": "<Operator>", "value": <any> }`. |
| **Operator** | One of the `Operator` enum values controlling how `field`'s resolved value is compared against `value`. |
| **Field path** | Dot-separated string (e.g. `user.address.city`) resolved against the `data` map via nested `Map` traversal only (no list indexing). |
| **Data** | The arbitrary JSON object (`Map<String,Object>`) the rule is evaluated against. |

## Business rules

- Rule dispatch: object with `all`/`any`/`not` key → group; object without
  those keys → condition; array → implicit AND group. Anything else throws.
  (`RuleEngineService.java:17-39`)
- Empty group arrays follow logical identity: empty `all` → `true`, empty
  `any` → `false`. (`RuleEngineService.java:41-44`)
- `between` requires exactly a 2-element `[min, max]` array value; violating
  this throws `IllegalArgumentException`. (`RuleEngineService.java:88-94`)
- `in` requires an array value; matches if any element compares equal to the
  resolved field value. (`RuleEngineService.java:95-102`)
- `contains` behaves differently by type: substring check for `String`
  fields, element-membership check for `Collection` fields; otherwise
  `false`. (`RuleEngineService.java:103-114`)
- `exists`/`not_exists`/`isNull`/`isNotNull` ignore the `value` payload and
  reason purely about path presence/nullness. (`RuleEngineService.java:80-87`)
- Value comparison precedence: numeric (BigDecimal) → ISO-8601 instant →
  `Comparable` → string fallback. (`ev-service-compare`)

## Owning components

- `comp-rules-engine` (`src/main/java/org/example/rules`) owns all rule
  semantics.
- `comp-api` (`src/main/java/org/example/api`) is a thin HTTP wrapper; it
  contains no rule-evaluation logic itself.

## Important symbols

- `RuleEngineService.evaluate` — top-level entrypoint into the domain.
- `RuleEngineService.evalGroup` — group short-circuit logic.
- `RuleEngineService.evalCondition` — per-operator leaf logic (the switch to
  extend for new operators).
- `RuleEngineService.compare` — shared comparison semantics used by 8 of the
  14 operators.
- `Operator` — the closed set of supported operator names.

## Entry points

- `POST /api/v1/rule-engine/evaluate` (`ep-evaluate-endpoint`) is the only way
  external callers invoke this domain.
- `RuleEngineService.evaluate(...)` can also be called directly in-process
  (as done by `RuleEngineServiceTest`), bypassing HTTP.

## Main workflows

1. **Simple condition evaluation**: client posts `{data, rule:{field,op,value}}`
   → controller validates non-null → `RuleEngineService.evaluate` resolves
   `field` in `data`, applies `op` via `evalCondition` → boolean returned.
2. **Nested group evaluation**: client posts a `rule` tree combining
   `all`/`any`/`not` and conditions → `evaluate` recurses depth-first with
   short-circuiting at each group level.

## Data and state

Entirely stateless and in-memory per request; no persistence, cache, or
shared mutable state. `data` and `rule` exist only for the lifetime of one
HTTP request/response cycle.

## External integrations

None. No outbound HTTP calls, databases, message queues, or third-party
services are used by this domain.

## Invariants

- `Operator.valueOf(opStr)` must succeed for every `op` string reaching
  `evalCondition`, and `evalCondition`'s switch must have a matching `case`
  for every enum constant, or a runtime `IllegalArgumentException` results
  (currently violated by `sin` — see risk below).
- `between`/`in` value shapes are enforced at evaluation time, not at request
  validation time (i.e., malformed shapes surface as 400s from
  `IllegalArgumentException`, not from bean validation).

## Tests

- `RuleEngineServiceTest` (unit, no Spring context): `eq`, `gt`+`contains`
  combined in `all`, `any` returning false, `between`+`regex` combined,
  `exists`/`not_exists`, `isNull`, `isNotNull`. (evidence `ev-tests-service`)
- `RuleEngineControllerTest` (Spring/MockMvc): one end-to-end happy path
  (`gte`). (evidence `ev-tests-controller`)
- **Not covered by any discovered test**: `ne`, `lt`, `lte`, `gte` in
  isolation, `in`, `not` group, malformed-rule error responses, and the
  uncommitted `sin` operator.

## Change risks

- Adding/renaming an `Operator` constant without a matching `evalCondition`
  case degrades silently to a runtime exception rather than a compile error
  — currently true for `sin` (evidence `ev-operator-enum-gap`).
- `resolvePathInternal` only traverses `Map` instances; paths through `List`
  elements (e.g. `items.0.name`) are not supported — an easy-to-miss
  limitation when writing new rules or tests.

## Unknowns

- At the current commit (`a007ae82`), no `sin`/`tan`/math operator exists in
  `Operator.java` or `evalCondition` — a `sin` addition observed transiently
  in an earlier grounding pass (commit `64b4948`) is no longer present
  (`ev-operator-current-state`). Intended semantics for a future `tan`
  operator (radians vs. degrees, comparison tolerance) are undecided — see
  `views/business.md` §9 and `task-guides/implement-tan-math-operator.md`.
- Whether list-indexed field paths are an intentional non-goal or an
  unaddressed gap.

## Evidence IDs

`ev-repo-purpose`, `ev-service-dispatch`, `ev-service-condition`,
`ev-service-compare`, `ev-operator-enum-gap`, `ev-operator-current-state`,
`ev-tests-service`, `ev-tests-controller`, `ev-entrypoint-controller`
