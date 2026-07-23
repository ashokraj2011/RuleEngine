<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "WORK-1234",
  "workType": "feature",
  "phase": "implementation-spec",
  "generation": 1,
  "status": "awaiting_approval",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011"
  },
  "generatedPersona": "architect",
  "sourceCommit": "b3ba49483366d6518e48b9f37193726e94e041a3",
  "generationCommit": "fbf357848c2cc38e92f281e6f606b36a6b8a713b",
  "publicationCommit": "fbf357848c2cc38e92f281e6f606b36a6b8a713b",
  "configSha256": "c5e1429897a228e5eb78c395ddf91788e7cf18fc507a3622b052bb00c245174a",
  "sourceSha256": "71a41fa561937851d6546f0b2bf54588bbf094b28722b3f6425c2c46a7d6b000",
  "template": {
    "path": ".singularity/templates/feature/implementation-spec.md",
    "sha256": "b76f05e99c0eceb87d1361c1dbf80fb9e84165c7426f752e2b7117cef04df29e"
  },
  "inputs": null,
  "remoteAgent": null,
  "telemetry": [
    {
      "generation": 1,
      "path": ".singularity/work-items/WORK-1234/telemetry/implementation-spec-gen1.json",
      "sha256": "7211e258f3bf8b1c8602ca997a985be2718b8e239b80840d92b82b67a0454763",
      "status": "unavailable",
      "models": [],
      "providerCost": null
    }
  ],
  "remoteOutputs": [],
  "usage": [
    {
      "status": "unavailable",
      "source": "copilot-otel-unavailable",
      "provider": null,
      "model": null,
      "inputTokens": null,
      "outputTokens": null,
      "cachedInputTokens": null,
      "cacheWriteInputTokens": null,
      "totalTokens": null,
      "providerCost": null,
      "costStatus": "unavailable",
      "spans": null,
      "startedAt": "2026-07-23T00:00:13.026Z",
      "completedAt": "2026-07-23T00:00:13.026Z",
      "persona": "architect",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [],
  "selfApproval": false,
  "conformanceTree": null
}
-->

# WORK-1234 — Implementation Specification

## Traceability

| Spec ID | Acceptance criteria | Planned code/tests | Status |
|---|---|---|---|
| SPEC-001 | AC-001, AC-002 | `Operator.java`: add `circle_area` enum constant; `RuleEngineService.evalCondition`: add `case circle_area` computing `π × radius²` via `BigDecimal` and comparing to expected value via `compare()`. Test: `RuleEngineServiceTest.testCircleAreaPositiveRadius`, `testCircleAreaZeroRadius` | planned |
| SPEC-002 | AC-003 | `RuleEngineService.evalCondition` `circle_area` case: reject non-numeric `left` via `toBigDecimalOrNull(left) == null` check, throwing `IllegalArgumentException("circle_area requires a numeric radius")`. Test: `RuleEngineServiceTest.testCircleAreaRejectsStringRadius` | planned |
| SPEC-003 | AC-004 | New `@Test` methods in `RuleEngineServiceTest.java` following existing `ObjectMapper`/`ObjectNode` rule-construction conventions (see `testSimpleEquality`, `testGreaterThanAndContains`) | planned |
| SPEC-004 | AC-005 | No changes to existing `switch` cases in `evalCondition`; existing tests (`testSimpleEquality`, `testGreaterThanAndContains`, etc.) must continue to pass unmodified, verifying no regression | planned |

## APIs, schemas, and contracts

- **`Operator` enum (`Operator.java`):** add new constant `circle_area` under a new `// Geometry` group comment. No changes to existing constants.
- **Rule condition contract (unchanged shape, new semantics for `circle_area`):**
  ```json
  { "field": "radius", "op": "circle_area", "value": 78.53981633974483 }
  ```
  - `field` — dot-path (existing `resolvePath` convention) resolving to the radius value in the input `data` map. Must resolve to a `Number`, a `BigDecimal`, or a numeric `String` (per `toBigDecimalOrNull`); otherwise an `IllegalArgumentException` is thrown.
  - `value` — the expected circle area (JSON number), compared against the computed area using the existing `compare()` `BigDecimal` equality path.
  - Return value: `boolean`, `true` when computed area equals `value` (same semantics as `eq`), consistent with `RuleEngineService.evaluate`'s existing boolean contract.
- **No changes to `RuleEngineController`** (out of scope; the new operator flows through the existing `/evaluate`-style endpoint(s) unchanged).

## File-level implementation plan

1. **`src/main/java/org/example/rules/Operator.java`**
   - Add `circle_area` to the enum, in a new `// Geometry` group (after existing groups), e.g.:
     ```java
     // Geometry
     circle_area
     ```
2. **`src/main/java/org/example/rules/RuleEngineService.java`**
   - In `evalCondition`'s `switch (op)`, add a `case circle_area:` branch before `default:`:
     - Compute `BigDecimal radius = toBigDecimalOrNull(left);`
     - If `radius == null`, throw `new IllegalArgumentException("circle_area requires a numeric radius");`
     - Compute `BigDecimal area = radius.multiply(radius).multiply(BigDecimal.valueOf(Math.PI));`
     - Return `compare(area, jsonToJava(valueNode)) == 0;`
   - No changes to `compare()`, `toBigDecimalOrNull()`, `resolvePath()`, or any other existing method.
3. **`src/test/java/org/example/rules/RuleEngineServiceTest.java`**
   - Add new `@Test` methods (following existing `ObjectMapper`/`ObjectNode` construction style):
     - `testCircleAreaPositiveRadius` — radius `5`, expect area `≈78.5398163...` (`π × 25`), assert `true`.
     - `testCircleAreaZeroRadius` — radius `0`, expect area `0`, assert `true`.
     - `testCircleAreaRejectsStringRadius` — radius as `String` (e.g., `"abc"`), assert `IllegalArgumentException` thrown via `assertThrows`.
     - `testCircleAreaMismatch` — radius `5`, expect an incorrect area value, assert `false` (negative case for AC-001 correctness).

## Security, observability, migration, and rollback

- **Security:** no new input surface; `field`/`value` flow through the existing validated JSON rule/data contract. No serialization or reflection changes.
- **Observability:** no new logging or metrics; failures are signaled via `IllegalArgumentException`, matching the existing pattern for unsupported/invalid operator usage elsewhere in `evalCondition`.
- **Migration:** purely additive enum constant + `switch` case; no schema, config, or persisted-data migration required. Existing rules and stored rule JSON remain valid and unaffected.
- **Rollback:** revert the two file changes (`Operator.java`, `RuleEngineService.java`) and the added tests; no data cleanup needed since no persisted state is introduced.

## Test specification

| ID | Maps to | Test | Expected result |
|---|---|---|---|
| TEST-001 | AC-001, SPEC-001 | `testCircleAreaPositiveRadius` | `evaluate()` returns `true` for radius `5`, expected area `π × 25` |
| TEST-002 | AC-002, SPEC-001 | `testCircleAreaZeroRadius` | `evaluate()` returns `true` for radius `0`, expected area `0` |
| TEST-003 | AC-003, SPEC-002 | `testCircleAreaRejectsStringRadius` | `evaluate()` throws `IllegalArgumentException` for a string radius (e.g., `"abc"`) |
| TEST-004 | AC-001, SPEC-001 | `testCircleAreaMismatch` | `evaluate()` returns `false` when the provided `value` does not match the computed area |
| TEST-005 | AC-005, SPEC-004 | Existing test suite (`testSimpleEquality`, `testGreaterThanAndContains`, and all other pre-existing tests) | All continue to pass unmodified, confirming no regression to existing operators |
