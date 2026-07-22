# Task Guide: Implement `tan` math operator

## 1. Task interpretation

Add a new rule-condition operator, `tan`, to the existing closed operator
vocabulary in `Operator.java` / `RuleEngineService.evalCondition`, so rule
authors can express a tangent-based comparison in the JSON rule grammar.
Status: **inferred task scope** — no ticket/spec was found in the repository
describing exact semantics (see Unknowns).

## 2. Relevant roles

- **Business** (this run's requested view — `views/business.md`): why the
  capability matters, who benefits, what changes for rule authors.
- **Development** (not requested this run, but required to actually implement
  the change): `views/development.md` (already generated from a prior pass)
  covers `Operator`/`RuleEngineService` structure, conventions, and the
  extension point.

Only `core` + `business` were explicitly requested for this run; this guide
flags where a `development`-view-driven implementation pass is still needed.

## 3. Relevant components

- `comp-rules-engine` (`src/main/java/org/example/rules`) — owns `Operator`
  enum and `RuleEngineService.evalCondition`'s switch; this is where the new
  case is added.
- `comp-api` (`src/main/java/org/example/api`) — unaffected; request/response
  contract does not change (still `{data, rule}` → `{result}`).

## 4. Relevant domain models

- `domains/rule-evaluation.md` — full technical catalogue of existing
  operators, comparison semantics (`RuleEngineService.compare`), and the
  invariant that every `Operator` enum constant must have a matching
  `evalCondition` case (previously violated by an uncommitted `sin` addition
  at commit `64b4948`, no longer present at current commit `a007ae82`).

## 5. Primary paths and symbols

| Path | Symbol | Role |
|---|---|---|
| `src/main/java/org/example/rules/Operator.java` | `Operator` enum | Add `tan` constant |
| `src/main/java/org/example/rules/RuleEngineService.java:63-135` | `evalCondition` switch | Add `case tan:` implementation |
| `src/main/java/org/example/rules/RuleEngineService.java:187-226` | `compare`, `toBigDecimalOrNull` | Reusable numeric coercion, if `tan` compares numerically |
| `src/test/java/org/example/rules/RuleEngineServiceTest.java` | test class | Add unit test(s) for `tan` |
| `README.md:166-213` | operator reference tables | Update documentation for the new operator |

## 6. Expected change flow (informational — not executed this run)

1. Add `tan` to the `Operator` enum.
2. Add a `case tan:` branch in `evalCondition` — decide and implement:
   resolve `left` via `resolvePath`, coerce to a numeric angle, compute
   `Math.tan(...)`, then compare to `jsonToJava(valueNode)` using the
   existing `compare(...)` helper (reusing numeric-coercion logic) or a
   dedicated tolerance-aware comparison if exact floating-point equality is
   unsuitable.
2. Decide radians vs. degrees input convention (unresolved — see Unknowns);
   document the choice.
3. Update `README.md` operator reference and add a worked example, mirroring
   the style of the existing `isNull`/`isNotNull` sections.
4. Add unit tests in `RuleEngineServiceTest` covering: normal case, boundary
   case (e.g., value near `tan`'s asymptote), and an error/invalid-input case
   if one is defined.

## 7. Contracts and invariants to preserve

- Every `Operator` enum constant **must** have a corresponding `case` in
  `evalCondition`'s switch, or calls will throw
  `IllegalArgumentException("Operator not implemented: tan")` at runtime
  instead of failing to compile (`ev-operator-current-state`,
  `domains/rule-evaluation.md` Invariants section).
- Request/response JSON shape (`{data, rule}` → `{result}`) must not change.
- Existing operator behaviors (`eq, ne, lt, ...`) must remain unaffected —
  purely additive change.

## 8. Tests to add or update

- `RuleEngineServiceTest`: at least one positive and one boundary/negative
  case for `tan`.
- `RuleEngineControllerTest`: optionally, one end-to-end HTTP case if `tan`
  is considered business-critical enough to warrant controller-level
  coverage (current controller test suite has only one happy-path case —
  see `domains/rule-evaluation.md` Tests section).

## 9. Commands to run

- `mvn -q -DskipTests package` — build after adding the enum/switch case.
- `mvn test` — run full suite including new `tan` tests.
(Not executed in this grounding pass — quick-depth, business-view-focused
run; no build/test claims are made here.)

## 10. Release or migration implications

None expected — no data migration, no API version bump implied by an
additive operator (not verified against any versioning policy; no CI/release
view was generated this run to confirm).

## 11. Risks

- Ambiguous math semantics (radians/degrees, asymptote handling) risk an
  implementation that doesn't match the intended business use case — resolve
  with domain owner before coding (see business view §9-10).
- Adding an enum constant without the matching switch case (the same mistake
  previously observed transiently at commit `64b4948`) degrades silently to
  a runtime exception rather than a compile error.

## 12. Unknowns requiring human confirmation

- Exact comparison semantics for `tan` (see business view §9).
- Whether `tan` should reuse `compare()`'s BigDecimal-based equality or need
  a floating-point tolerance parameter (tangent values are often irrational).
- Whether additional math operators are planned, which would favor a more
  general "math operator" design over a single hard-coded `tan` case.

## 13. Evidence IDs

`ev-operator-current-state`, `ev-service-condition`, `ev-service-compare`,
`ev-no-math-operators-business-impact`, `ev-tests-service`
