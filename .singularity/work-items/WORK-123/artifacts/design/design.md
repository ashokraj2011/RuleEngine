<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "WORK-123",
  "workType": "feature",
  "phase": "design",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011"
  },
  "generatedPersona": "product-owner",
  "sourceCommit": "c91be4fb29fde0bc85d8415be5bf04d457cf558c",
  "generationCommit": "f7eaa2c2e3a62451335af1ae3246adaf987537e2",
  "publicationCommit": "f7eaa2c2e3a62451335af1ae3246adaf987537e2",
  "configSha256": "c5e1429897a228e5eb78c395ddf91788e7cf18fc507a3622b052bb00c245174a",
  "sourceSha256": "9fa612a3960c673acd7a07d6f387a92024f52a8d98d0265db1adeafabd71bb98",
  "template": {
    "path": ".singularity/templates/feature/design.md",
    "sha256": "8b7455f464a7025efa92942c272a04e3c0a3ab2a4d3eb438703cc14e230bc856"
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
      "startedAt": "2026-07-22T11:23:58.820Z",
      "completedAt": "2026-07-22T11:23:58.821Z",
      "persona": "product-owner"
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "design",
      "at": "2026-07-22T12:02:03.757Z",
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

# WORK-123 — Architecture and Design

## Context and constraints

The approved requirements (generation 1) add a `tan` operator so a JSON rule
condition of the form `{ "field": "<name>", "op": "tan", "value": <number> }`
evaluates `Math.tan(fieldValue)` against `value`.

The current architecture is a single-service rule engine:

- `Operator` (`src/main/java/org/example/rules/Operator.java`) is a plain
  enum of supported condition operators. It has no fields or behavior; the
  string name is parsed via `Operator.valueOf(opStr)` in
  `RuleEngineService.evalCondition`.
- `RuleEngineService.evalCondition` resolves the condition's `field` value
  from the input `Map<String, Object>` via `resolvePath`, converts the JSON
  `value` node to a Java object via `jsonToJava`, and switches on `Operator`
  to compute a boolean result. Numeric operators (`eq`, `ne`, `lt`, `lte`,
  `gt`, `gte`, `between`, `in`) all funnel through `compare(Object, Object)`,
  which uses `toBigDecimalOrNull` to attempt a `BigDecimal` comparison before
  falling back to `Instant` or `Comparable`/string comparison.
- There is no existing tolerance-aware equality: `compare` returns an exact
  `BigDecimal.compareTo` result (0 only on exact equality), so `eq` today
  requires exact numeric match. `tan` must introduce the first
  tolerance-based numeric equality in this codebase without changing
  behavior for any existing operator.

Constraints carried over from requirements:
- No new library — `Math.tan` is JDK-only (AC acceptance implies no new
  dependency).
- Must not alter `compare()`'s exact-match semantics used by `eq`/`ne`/etc.
  (Requirements AC-005).
- Must throw `IllegalArgumentException` for missing/non-numeric field or
  value (AC-003, AC-004), matching the style already used by `between`
  (`"between requires array [min, max]"`) and `regex`
  (`"regex requires string pattern"`).
- Must define deterministic behavior at/near undefined-tangent inputs
  (e.g. π/2 radians) without an unhandled runtime exception (AC-006).

## Proposed design

**1. `Operator` enum**
Add `tan` to the existing operator list in
`src/main/java/org/example/rules/Operator.java`, grouped under Comparison
alongside `eq`/`ne`/`lt`/etc., since it is a numeric relational operator
(field-derived value compared to a target value), not a new category.

**2. Evaluation in `RuleEngineService.evalCondition`**
Add a `case tan:` branch in the existing `switch (op)` block, following the
same shape as `eq`/`lt`/etc.:

1. Require `left` (the resolved field value) to be non-null and numeric.
   Convert using the existing `toBigDecimalOrNull(left)` helper; if the
   result is `null`, throw
   `new IllegalArgumentException("tan requires a numeric field value")`
   (mirrors AC-003).
2. Require `valueNode`/`jsonToJava(valueNode)` to be non-null and numeric via
   the same `toBigDecimalOrNull` helper; if `null`, throw
   `new IllegalArgumentException("tan requires a numeric value")` (mirrors
   AC-004). This reuses `toBigDecimalOrNull` — no new numeric-parsing code
   path.
3. Compute `double computed = Math.tan(leftBigDecimal.doubleValue());` and
   `double target = valueBigDecimal.doubleValue();`.
4. Compare with a **fixed absolute tolerance** `TAN_TOLERANCE = 1e-6` defined
   as a `private static final double` constant on `RuleEngineService`:
   `return Math.abs(computed - target) <= TAN_TOLERANCE;` — this is the new
   tolerance-based equality requested in Requirements' open question 1, kept
   local to the `tan` case so `compare()` and all other operators are
   untouched (satisfies AC-005).
5. **Undefined-tangent handling (AC-006):** at/near π/2 radians,
   `Math.tan` returns a very large finite double or, for exact
   `Math.PI / 2`, a large finite value (not actually `Infinity`, since
   `Math.PI/2` is not exactly π/2 in double precision) — but as a
   deterministic safety net for any input that does yield a non-finite
   result, evaluation checks `Double.isNaN(computed) ||
   Double.isInfinite(computed)` **before** the tolerance comparison and, in
   that case, returns `false` rather than propagating a silent `true`/`NaN`
   comparison or throwing. This makes the undefined-tangent behavior a
   defined, tested `false` result rather than an unhandled exception,
   satisfying AC-006 without requiring callers to pre-filter inputs.

No changes are made to `evaluate`, `evalGroup`, `resolvePath`, `jsonToJava`,
or `compare` — the new operator is fully additive within `evalCondition`.

**Pseudocode:**
```java
private static final double TAN_TOLERANCE = 1e-6;
...
case tan:
    BigDecimal leftNum = toBigDecimalOrNull(left);
    if (leftNum == null) {
        throw new IllegalArgumentException("tan requires a numeric field value");
    }
    BigDecimal targetNum = toBigDecimalOrNull(jsonToJava(valueNode));
    if (targetNum == null) {
        throw new IllegalArgumentException("tan requires a numeric value");
    }
    double computed = Math.tan(leftNum.doubleValue());
    if (Double.isNaN(computed) || Double.isInfinite(computed)) {
        return false;
    }
    return Math.abs(computed - targetNum.doubleValue()) <= TAN_TOLERANCE;
```

**Rule authoring note:** `Math.tan` takes radians, so field values for `tan`
conditions must be radians (open question 2). This will be documented in the
`tan` operator's Javadoc on the enum constant and/or module README as part
of implementation.

## Security, observability, migration, and rollback

- **Security:** no new external input parsing beyond existing JSON condition
  fields; no new dependencies; no change to trust boundaries. The new
  `IllegalArgumentException` paths reuse existing error handling/response
  mapping used by other operators, so no new error-handling code is needed
  upstream.
- **Observability:** no new logging/metrics are introduced by this change;
  existing exception propagation surfaces validation failures the same way
  as `between`/`regex` do today. If the service already logs
  `IllegalArgumentException` at the API boundary, `tan` failures will appear
  there automatically.
- **Migration:** purely additive — a new enum constant and a new `switch`
  case. No schema, storage, or API contract changes; existing rules
  (JSON documents) are unaffected since none reference `"op": "tan"` today.
- **Rollback:** reverting the two changed files (`Operator.java`,
  `RuleEngineService.java`) fully removes the feature; no data migration or
  cleanup is required since no persistent state is introduced.

## Alternatives and decisions

- **Tolerance strategy:** considered (a) reusing `compare()`'s exact
  `BigDecimal` equality (rejected — floating-point `Math.tan` results are
  virtually never exactly equal to a hand-picked expected value, which would
  make `eq`-style exactness impractical per requirements' risk note), and
  (b) a fixed absolute tolerance constant scoped to the `tan` case only
  (**selected** — simplest, keeps `compare()`/other operators unchanged,
  satisfies AC-001/AC-002 without a configurable-tolerance feature, which
  requirements explicitly places out of scope).
- **Undefined-tangent behavior:** considered (a) throwing
  `IllegalArgumentException` when input is at/near π/2 (rejected — adds an
  extra "near" threshold that is arbitrary and diverges from how `Math.tan`
  itself behaves, since Java doubles can't represent exact π/2), and (b)
  returning `false` for any non-finite `Math.tan` result (**selected** —
  deterministic, testable, requires no extra threshold logic, and matches
  AC-006's requirement to avoid an unhandled exception while still being
  explicitly defined and covered by a test).
- **Enum grouping:** `tan` is added to the existing `// Comparison` group in
  `Operator` rather than introducing a new trigonometric-only enum category,
  since requirements explicitly scope out other trig operators (`sin`,
  `cos`, `atan`) for now; a separate category would be premature
  generalization.
- **Where the tolerance constant lives:** a `private static final double`
  in `RuleEngineService` (co-located with its single use site) rather than
  a new configuration property, consistent with requirements marking
  configurable tolerance as out of scope for this iteration.
