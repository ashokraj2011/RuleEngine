<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "WORK-123",
  "workType": "feature",
  "phase": "intake",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011"
  },
  "generatedPersona": "product-owner",
  "sourceCommit": "d000d1b88d8cf409ce4637dd4d504a7cbda1ce7e",
  "generationCommit": "b006274894ae174dcad8514bf1345d67d372c3ca",
  "publicationCommit": "b006274894ae174dcad8514bf1345d67d372c3ca",
  "configSha256": "c5e1429897a228e5eb78c395ddf91788e7cf18fc507a3622b052bb00c245174a",
  "sourceSha256": "9fa612a3960c673acd7a07d6f387a92024f52a8d98d0265db1adeafabd71bb98",
  "template": {
    "path": ".singularity/templates/feature/intake.md",
    "sha256": "eb53814f46f12ea3d93d1629164bd7ff22a3a54feceff7f7dd55670caeb5dbab"
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
      "startedAt": "2026-07-22T08:46:15.829Z",
      "completedAt": "2026-07-22T08:46:15.830Z",
      "persona": "product-owner"
    }
  ],
  "approvals": [
    {
      "decision": "approved",
      "phase": "intake",
      "at": "2026-07-22T08:50:44.902Z",
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

# WORK-123 — Feature Intake

## User and outcome

Rule authors who write JSON-defined rules against numeric fields currently have
access to comparison operators (`eq`, `ne`, `lt`, `lte`, `gt`, `gte`,
`between`, `in`) but no trigonometric operators. This work adds a `tan`
operator so a rule can express "the tangent of a numeric field's value"
directly, without requiring the caller to pre-compute trigonometric values
before submitting the rule payload. Measurable outcome: a rule condition
using `op: "tan"` against a numeric field evaluates without error and its
result is verifiable via automated tests, matching Java's standard `tan()`
semantics.

## Proposed capability

Add a new `tan` leaf-condition operator to the existing rule grammar
(`comp-rules-engine`, `Operator` enum). A condition of the form
`{ "field": "<name>", "op": "tan", "value": <number> }` evaluates to true
when the tangent of the field's numeric value equals the supplied `value`
(subject to a defined numeric tolerance, since floating-point tangent
results are rarely exact). The operator accepts only numeric inputs; no
change to the surrounding group operators (`all`/`any`/`not`) or the HTTP
API surface is proposed.

## Scope, constraints, and stakeholders

- **In scope:** a single new operator (`tan`) in the rule evaluation engine,
  restricted to numeric field values, plus positive- and negative-path test
  coverage.
- **Out of scope:** other trigonometric operators (`sin`, `cos`, etc.),
  changes to the REST API contract, and changes to non-numeric operator
  behavior.
- **Constraints:** must only accept numbers (non-numeric `value`/field
  input should raise a validation error, consistent with existing operator
  error handling); floating-point comparison requires an explicit tolerance
  rather than exact equality.
- **Dependencies:** none outside the existing `comp-rules-engine` component.
- **Stakeholders:** rule authors/API consumers (primary), QA (verification
  of numeric edge cases such as undefined tangent near 90°/π/2).
- **Unresolved decisions:** the exact comparison tolerance for floating-point
  equality, and the error behavior when the field value is at an
  undefined-tangent point (e.g., ±90°/π/2 in degrees/radians) — to be
  resolved in the requirements/design phases.
