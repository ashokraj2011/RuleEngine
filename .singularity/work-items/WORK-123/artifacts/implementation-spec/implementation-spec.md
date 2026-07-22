<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "WORK-123",
  "workType": "feature",
  "phase": "implementation-spec",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011"
  },
  "generatedPersona": "architect",
  "sourceCommit": "a00e8118a4c2704e0aebcc63f90990fdb0628d98",
  "generationCommit": "5d5fc6bb5ada01358488fa7e3054b622d2ab64c1",
  "publicationCommit": "5d5fc6bb5ada01358488fa7e3054b622d2ab64c1",
  "configSha256": "c5e1429897a228e5eb78c395ddf91788e7cf18fc507a3622b052bb00c245174a",
  "sourceSha256": "9fa612a3960c673acd7a07d6f387a92024f52a8d98d0265db1adeafabd71bb98",
  "template": {
    "path": ".singularity/templates/feature/implementation-spec.md",
    "sha256": "b76f05e99c0eceb87d1361c1dbf80fb9e84165c7426f752e2b7117cef04df29e"
  },
  "inputs": null,
  "remoteAgent": null,
  "remoteOutputs": [],
  "usage": [
    {
      "status": "unavailable",
      "source": "copilot-unavailable",
      "provider": null,
      "model": null,
      "inputTokens": null,
      "outputTokens": null,
      "cachedInputTokens": null,
      "totalTokens": null,
      "startedAt": "2026-07-22T12:19:08.804Z",
      "completedAt": "2026-07-22T12:19:08.809Z",
      "persona": "architect"
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "implementation-spec",
      "at": "2026-07-22T12:38:58.567Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011"
      },
      "persona": "architect",
      "channel": "terminal",
      "generation": 1,
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# WORK-123 — Implementation Specification

## Traceability

| Spec ID | Acceptance criteria | Planned code/tests | Status |
|---|---|---|---|
| SPEC-001 | AC-001, AC-002, AC-005 | `Operator.tan`; `RuleEngineService.evalCondition` `case tan:` tolerance branch; `RuleEngineServiceTest.testTanWithinTolerance`, `testTanOutsideToleranceIsFalse` | planned |
| SPEC-002 | AC-003 | `RuleEngineService.evalCondition` `case tan:` non-numeric field check; `RuleEngineServiceTest.testTanNonNumericFieldThrows`, `testTanMissingFieldThrows` | planned |
| SPEC-003 | AC-004 | `RuleEngineService.evalCondition` `case tan:` non-numeric value check; `RuleEngineServiceTest.testTanNonNumericValueThrows`, `testTanMissingValueThrows` | planned |
| SPEC-004 | AC-006 | `RuleEngineService.evalCondition` `case tan:` `Double.isNaN`/`isInfinite` guard; `RuleEngineServiceTest.testTanNearHalfPiReturnsFalse` | planned |
| SPEC-005 | AC-005 | No changes to `compare()`/`toBigDecimalOrNull`/other `case` branches; existing test suite (`RuleEngineServiceTest`, `RuleEngineControllerTest`) re-run unchanged | planned |

## APIs, schemas, and contracts

- No REST/HTTP contract changes. The existing condition JSON shape is
  extended in-place: `{ "field": "<name>", "op": "tan", "value": <number> }`
  becomes a valid `op` value, parsed the same way as every other operator
  via `Operator.valueOf(opStr)` in `evalCondition`. No new endpoint,
  request/response DTO, or serialization change is required.
- Internal contract: `Operator` enum gains one constant, `tan`, added to the
  existing `// Comparison` group in
  `src/main/java/org/example/rules/Operator.java`. This is a pure additive
  enum change; no existing constant is renamed, reordered in a
  behavior-affecting way, or removed.
- Internal contract: `RuleEngineService.evalCondition` gains one new
  `switch (op)` case, `case tan:`, following the same signature and
  side-effect-free contract as `case eq:`/`case lt:` (reads `left` and
  `valueNode` from the enclosing method scope, returns a `boolean`, throws
  `IllegalArgumentException` on invalid input). No change to the method
  signature, `evaluate`, `evalGroup`, `resolvePath`, `jsonToJava`, or
  `compare`.
- New private contract: `private static final double TAN_TOLERANCE = 1e-6;`
  field on `RuleEngineService`, used only within `case tan:`. Not exposed
  externally; not configurable (explicitly out of scope per requirements).

## File-level implementation plan

**1. `src/main/java/org/example/rules/Operator.java`**
- Add `tan` to the existing enum constant list, in the `// Comparison`
  group (alongside `eq, ne, lt, lte, gt, gte`):
  ```java
  // Comparison
  eq, ne, lt, lte, gt, gte, tan,
  ```
- Add a Javadoc comment on the enum (or on the `tan` constant, per existing
  file conventions — the file currently has no per-constant Javadoc, so a
  short block comment above the `// Comparison` group or a dedicated
  one-line comment above `tan` is acceptable) documenting that `tan`
  compares `Math.tan(fieldValue)` (field value interpreted as **radians**)
  against `value` within a fixed tolerance.

**2. `src/main/java/org/example/rules/RuleEngineService.java`**
- Add the constant near the top of the class body (alongside other
  class-level state, before `evaluate`):
  ```java
  private static final double TAN_TOLERANCE = 1e-6;
  ```
- Add `case tan:` to the `switch (op)` block inside `evalCondition`,
  positioned with the other comparison cases (e.g. directly before
  `default:`):
  ```java
  case tan:
      BigDecimal tanLeft = toBigDecimalOrNull(left);
      if (tanLeft == null) {
          throw new IllegalArgumentException("tan requires a numeric field value");
      }
      BigDecimal tanTarget = toBigDecimalOrNull(jsonToJava(valueNode));
      if (tanTarget == null) {
          throw new IllegalArgumentException("tan requires a numeric value");
      }
      double computed = Math.tan(tanLeft.doubleValue());
      if (Double.isNaN(computed) || Double.isInfinite(computed)) {
          return false;
      }
      return Math.abs(computed - tanTarget.doubleValue()) <= TAN_TOLERANCE;
  ```
  Note: local variable names (`tanLeft`, `tanTarget`, `computed`) must not
  collide with existing `switch`-scoped locals (`min`, `max`, `right`,
  `pattern`) already declared in sibling `case` blocks in the same method
  body — verify at authoring time and rename if a `switch`-scope collision
  is reported by the compiler.
- No changes to `evaluate`, `evalGroup`, `evalCondition`'s existing cases,
  `textOrNull`, `jsonToJava`, `dataContainsPath`, `resolvePath`,
  `resolvePathInternal`, `compare`, `toBigDecimalOrNull`, or
  `toInstantOrNull`.

**3. `src/test/java/org/example/rules/RuleEngineServiceTest.java`**
- Add new `@Test` methods following the existing style in this file (each
  test constructs a small `data` map and a JSON rule string, then calls the
  service under test — mirror `testGreaterThanAndContains`/
  `testBetweenAndRegex` for structure):
  - `testTanWithinTolerance` (AC-001): field value `angle = Math.PI / 4`,
    condition `value` set to `Math.tan(Math.PI / 4)` (i.e. `1.0`), assert
    `evaluate(...)` returns `true`.
  - `testTanOutsideToleranceIsFalse` (AC-002): same `angle`, `value` set to
    `1.0 + 10 * TAN_TOLERANCE`-equivalent (a value clearly outside `1e-6`,
    e.g. `2.0`), assert `evaluate(...)` returns `false`.
  - `testTanNonNumericFieldThrows` (AC-003): field value is a non-numeric
    string (e.g. `"not-a-number"`), assert
    `assertThrows(IllegalArgumentException.class, ...)`.
  - `testTanMissingFieldThrows` (AC-003): `field` path resolves to `null`
    (absent key), assert `IllegalArgumentException` is thrown.
  - `testTanNonNumericValueThrows` (AC-004): valid numeric field, JSON
    `"value"` is a non-numeric string, assert `IllegalArgumentException`.
  - `testTanMissingValueThrows` (AC-004): valid numeric field, condition
    JSON omits `"value"` entirely, assert `IllegalArgumentException`.
  - `testTanNearHalfPiReturnsFalse` (AC-006): field value at/near
    `Math.PI / 2` (e.g. exactly `Math.PI / 2`, which in double precision
    yields a large finite `Math.tan` result, not `Infinity`/`NaN` — this
    test documents the behavior at that boundary), condition `value` set to
    a value that is not within tolerance of the (large) computed result;
    assert `evaluate(...)` returns `false` without throwing. If a
    genuinely non-finite `Math.tan` result cannot be produced from a
    realistic double input, this test instead directly asserts the
    documented **false-on-non-finite** contract by reasoning over a
    value expected to produce a very large magnitude, keeping the test
    deterministic and exception-free.
- No modifications to any existing `@Test` method in this file.

**4. `src/test/java/org/example/api/RuleEngineControllerTest.java`**
- No changes required — `tan` reuses the existing condition JSON shape and
  HTTP contract already exercised by this file; existing tests continue to
  pass unmodified (AC-005).

## Security, observability, migration, and rollback

- **Security:** no new external input parsing, no new dependencies (`Math.tan`
  is JDK-only), no change to trust boundaries or input validation surface
  beyond the two new `IllegalArgumentException` checks, which reuse the
  existing exception type and propagation path already used by `between`
  and `regex`.
- **Observability:** no new logging or metrics are added. `tan` validation
  failures surface as `IllegalArgumentException`, handled identically to
  existing operator validation failures by any upstream error mapping
  (e.g. controller-level exception handling), so no new instrumentation is
  required.
- **Migration:** purely additive — one new enum constant and one new
  `switch` case plus one private constant. No schema, persistence, or REST
  contract changes. No existing rule JSON references `"op": "tan"` today,
  so no data migration is needed.
- **Rollback:** revert the two changed files
  (`src/main/java/org/example/rules/Operator.java` and
  `src/main/java/org/example/rules/RuleEngineService.java`) and the added
  test methods in `RuleEngineServiceTest.java`. No persistent state or
  external configuration is introduced, so rollback requires no cleanup
  beyond the code revert.

## Test specification

| Requirement | Test | Verifies |
|---|---|---|
| AC-001 | `testTanWithinTolerance` | `tan` condition evaluates `true` when `Math.tan(field)` matches `value` within `TAN_TOLERANCE`. |
| AC-002 | `testTanOutsideToleranceIsFalse` | `tan` condition evaluates `false` when `Math.tan(field)` differs from `value` beyond `TAN_TOLERANCE`. |
| AC-003 | `testTanNonNumericFieldThrows`, `testTanMissingFieldThrows` | Missing/non-numeric field value throws `IllegalArgumentException` rather than returning a boolean. |
| AC-004 | `testTanNonNumericValueThrows`, `testTanMissingValueThrows` | Missing/non-numeric `value` throws `IllegalArgumentException`. |
| AC-005 | Full existing `RuleEngineServiceTest` and `RuleEngineControllerTest` suites, re-run unmodified | Pre-existing operators (`eq`, `ne`, `lt`, `lte`, `gt`, `gte`, `between`, `in`, `contains`, `regex`, `exists`, `not_exists`, `isNull`, `isNotNull`) and API behavior are unchanged. |
| AC-006 | `testTanNearHalfPiReturnsFalse` | Input at/near an undefined-tangent point produces a deterministic `false` result, not an unhandled exception. |
