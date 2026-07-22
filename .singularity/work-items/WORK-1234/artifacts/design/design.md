<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "WORK-1234",
  "workType": "feature",
  "phase": "design",
  "generation": 1,
  "status": "in_progress",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011"
  },
  "generatedPersona": "product-owner",
  "sourceCommit": "8056d9c8b6e5aa9c49e906a8846cf85a0304aca6",
  "generationCommit": null,
  "publicationCommit": null,
  "configSha256": "c5e1429897a228e5eb78c395ddf91788e7cf18fc507a3622b052bb00c245174a",
  "sourceSha256": "71a41fa561937851d6546f0b2bf54588bbf094b28722b3f6425c2c46a7d6b000",
  "template": {
    "path": ".singularity/templates/feature/design.md",
    "sha256": "8b7455f464a7025efa92942c272a04e3c0a3ab2a4d3eb438703cc14e230bc856"
  },
  "inputs": null,
  "remoteAgent": null,
  "telemetry": [
    {
      "generation": 1,
      "path": ".singularity/work-items/WORK-1234/telemetry/design-gen1.json",
      "sha256": "797787aebe59920ad1152f708caaaf51667c8718d6963b8e1fc1d3fbcd38811f",
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
      "startedAt": "2026-07-22T23:52:38.502Z",
      "completedAt": "2026-07-22T23:52:38.502Z",
      "persona": "product-owner",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [],
  "selfApproval": false,
  "conformanceTree": null
}
-->

# WORK-1234 — Architecture and Design

## Context and constraints

Approved requirements (WORK-1234) require the rule engine to compute a circle's area (`π × radius²`) from a numeric radius so rule authors can validate/use that value, while rejecting non-numeric radius input (AC-001–AC-005).

Current architecture (`org.example.rules`):
- `Operator` (`Operator.java`) is a flat enum of supported condition operators (`eq`, `ne`, `lt`, `lte`, `gt`, `gte`, `contains`, `in`, `regex`, `between`, `exists`, `not_exists`, `isNull`, `isNotNull`).
- `RuleEngineService.evaluate(...)` parses a JSON rule tree (`all`/`any`/`not`/leaf condition) and dispatches each leaf condition through `evalCondition`, which resolves a `field` path from the input `data` map (`resolvePath`), converts the rule's declared `value` from JSON to a Java object (`jsonToJava`), and switches on `Operator` to produce a boolean result. Numeric comparisons go through `compare()`, which uses `BigDecimal` for numeric types.
- `RuleEngineController` exposes evaluation over HTTP (not modified by this design).

Constraint: the engine's evaluation contract is boolean (`evaluate` returns `true`/`false` per condition); there is no existing "compute and store a derived value" mechanism, so circle-area support must be expressed as a new comparison-style operator consistent with the existing `switch`-based pattern, rather than a side-channel computed field.

## Proposed design

1. **New operator `circle_area`** added to `Operator.java`, grouped under a new `// Geometry` comment, alongside existing groups.
2. **Evaluation semantics** in `RuleEngineService.evalCondition`: for `circle_area`, treat the resolved `field` value (`left`) as the radius and the rule's `value` as the expected area:
   - Resolve `left` via existing `resolvePath`.
   - Require `left` to be numeric (`Number`, `BigDecimal`-convertible `String`, per existing `toBigDecimalOrNull` helper) — reuse `toBigDecimalOrNull(left)`. If it returns `null` (including for `String` radius values, per AC-003 "reject non-numeric input"), throw `IllegalArgumentException("circle_area requires a numeric radius")`, matching the engine's existing fail-fast style (see `evalCondition`'s `IllegalArgumentException` usage for missing/unknown operators).
   - Compute `area = radius.multiply(radius).multiply(BigDecimal.valueOf(Math.PI))` using `BigDecimal` (consistent with `compare()`'s numeric handling) — this satisfies AC-001 (positive radius) and AC-002 (radius `0` → area `0`).
   - Compare computed `area` to `jsonToJava(valueNode)` using the existing `compare()` helper (numeric `BigDecimal` comparison path), returning `compare(area, expected) == 0`. This reuses `compare()` rather than introducing a new equality routine, satisfying AC-005 (integrates without breaking existing operator behavior).
3. **Precision:** rely on `BigDecimal` arithmetic (already used elsewhere in the class) for consistency; no new epsilon/tolerance parameter is introduced, since `compare()` already performs exact `BigDecimal` comparison for numeric types — same behavior as `eq`.
4. **Negative radius (OQ-1):** left unspecified per stakeholder; the design does not add special-case rejection for negative values, deferring to natural `BigDecimal` arithmetic (documented as an open item, see below), since intake/requirements explicitly left this undecided.

## Security, observability, migration, and rollback

- **Security:** no new external input surface; radius/value flow through the existing JSON rule/data contract already validated by callers of `RuleEngineController`. No new attack surface beyond existing operator dispatch.
- **Observability:** no new logging/metrics needed — failures surface as `IllegalArgumentException`, consistent with other operators (e.g., "Unknown operator").
- **Migration/compatibility:** purely additive — a new enum constant and a new `switch` case. No existing operator behavior changes; existing rules referencing `eq`, `between`, etc. are unaffected.
- **Rollback:** revert the `Operator.java` enum addition and the `circle_area` `switch` case in `RuleEngineService.java`; no data migration or schema change is involved.

## Alternatives and decisions

- **Alternative A — Derived/computed fact injected into `data` before evaluation** (e.g., a pre-processing step that adds `radius_area` to the input map): rejected because it requires callers to change how they invoke `RuleEngineService.evaluate`, is inconsistent with the self-contained operator dispatch pattern, and was explicitly listed as a design-phase decision point (OQ-2) — the new-operator approach keeps the change scoped to `Operator.java` and `RuleEngineService.java` only.
- **Alternative B — Separate utility method (e.g., `CircleAreaUtil.compute(radius)`) called ad hoc by rule authors outside the operator framework:** rejected because it bypasses the rule engine's declarative JSON rule contract and would not be invokable via the existing `field`/`op`/`value` condition shape used by `RuleEngineController` consumers.
- **Decision:** implement `circle_area` as a new `Operator` enum value evaluated in `RuleEngineService.evalCondition`, per the Proposed design above. Resolves OQ-2 from the requirements phase.
- **Open item carried forward:** OQ-1 (negative radius handling) remains unresolved; implementation will follow natural `BigDecimal` arithmetic (negative radius squared is still positive, so no runtime error), and this is flagged for verification-phase test coverage rather than blocking implementation.
