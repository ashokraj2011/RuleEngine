<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "WORK-123",
  "workType": "feature",
  "phase": "requirements",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011"
  },
  "generatedPersona": "product-owner",
  "sourceCommit": "ae03eb39ff3a9e5dc2e9bd834fb1f0f2ecb4992f",
  "generationCommit": "2d82a71c1e87f999c1b0ac084f8b98bd2c0c877c",
  "publicationCommit": "2d82a71c1e87f999c1b0ac084f8b98bd2c0c877c",
  "configSha256": "c5e1429897a228e5eb78c395ddf91788e7cf18fc507a3622b052bb00c245174a",
  "sourceSha256": "9fa612a3960c673acd7a07d6f387a92024f52a8d98d0265db1adeafabd71bb98",
  "template": {
    "path": ".singularity/templates/feature/requirements.md",
    "sha256": "92a9807dfe4a2a986823450c24caffdb07205c36e2d963db74d5b8fc9092a9eb"
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
      "startedAt": "2026-07-22T10:34:45.209Z",
      "completedAt": "2026-07-22T10:34:45.214Z",
      "persona": "product-owner"
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "requirements",
      "at": "2026-07-22T11:22:06.973Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011"
      },
      "persona": "product-owner",
      "channel": "terminal",
      "generation": 1,
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# WORK-123 — Feature Requirements

## Problem and outcome

Rule authors defining JSON rule conditions against `org.example.rules.Operator`
can currently only compare numeric fields using `eq`, `ne`, `lt`, `lte`, `gt`,
`gte`, `between`, and `in`. There is no way to express a condition on the
tangent of a numeric field's value without pre-computing it outside the rule
engine. This work adds a `tan` operator to `Operator` and its evaluation in
`RuleEngineService.evalCondition`, so a condition of the form
`{ "field": "<name>", "op": "tan", "value": <number> }` evaluates directly
against `Math.tan(fieldValue)`.

Measurable outcome: a rule containing a `tan` condition evaluates correctly
against Java's `Math.tan()` semantics (within a defined tolerance) for
representative inputs, is covered by automated tests for both matching and
non-matching cases, and rejects non-numeric field or `value` input with the
same validation-error behavior as existing numeric operators (e.g. `eq`,
`lt`).

## Scope

**In scope**
- Add `tan` to the `Operator` enum (`src/main/java/org/example/rules/Operator.java`).
- Implement evaluation for `tan` in `RuleEngineService.evalCondition`:
  resolve the field value, require it and `value` to be numeric, compute
  `Math.tan(fieldValue.doubleValue())`, and compare the result to `value`
  using a fixed numeric tolerance (consistent with existing `BigDecimal`-based
  numeric comparison in `compare`/`toBigDecimalOrNull`).
- Raise `IllegalArgumentException` (matching the existing error style used
  by `between`/`regex`) when the field value or `value` is missing or
  non-numeric.
- Add positive-path and negative-path automated tests for the new operator,
  including a case near an undefined-tangent point (e.g. π/2 radians).

**Out of scope**
- Other trigonometric operators (`sin`, `cos`, `atan`, etc.).
- Changes to the REST/HTTP API contract or request/response shapes beyond
  accepting `"op": "tan"` in existing condition JSON.
- Changes to group operator (`all`/`any`/`not`) semantics.
- Configurable tolerance (tolerance is fixed/hard-coded for this iteration).

## Acceptance criteria

- AC-001: Given a condition `{ "field": "angle", "op": "tan", "value": V }`,
  when `angle`'s value is numeric and `Math.tan(angle) == V` within the
  defined tolerance, the condition evaluates to `true`.
- AC-002: Given the same condition shape, when `Math.tan(angle) != V`
  (outside tolerance), the condition evaluates to `false`.
- AC-003: Given a `tan` condition where the resolved field value is missing,
  `null`, or non-numeric, evaluation throws `IllegalArgumentException`
  rather than returning `true`/`false` silently.
- AC-004: Given a `tan` condition where `value` is missing or non-numeric,
  evaluation throws `IllegalArgumentException`.
- AC-005: Existing operators (`eq`, `ne`, `lt`, `lte`, `gt`, `gte`, `between`,
  `in`, `contains`, `regex`, `exists`, `not_exists`, `isNull`, `isNotNull`)
  continue to behave unchanged; all pre-existing tests continue to pass.
- AC-006: A field value at or extremely near an undefined-tangent point
  (e.g. π/2 radians) is handled without an unhandled runtime exception
  (either produces `Infinity`/`NaN`-aware comparison behavior or a defined
  validation error), and this behavior is covered by a test.

## Dependencies, risks, and open questions

- **Dependencies:** none outside the existing `comp-rules-engine`/
  `org.example.rules` package; no new library required since `Math.tan` is
  part of the JDK.
- **Risks:** floating-point tangent results are rarely exact, so an
  inappropriate tolerance could cause false positives/negatives; values near
  π/2 (in radians) produce very large or `Infinity`/`NaN` results that must
  be handled deterministically rather than throwing an unexpected exception.
- **Open questions (to resolve in design):**
  1. What numeric tolerance should be used for the `tan` equality comparison,
     and should it reuse/extend the existing `BigDecimal` comparison in
     `compare()`?
  2. Are field values interpreted as radians or degrees? (Java's `Math.tan`
     takes radians; this must be documented for rule authors.)
  3. What is the exact defined behavior at/near undefined-tangent inputs
     (validation error vs. `Infinity`/`NaN` comparison)?
