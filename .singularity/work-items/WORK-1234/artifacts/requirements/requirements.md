<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "WORK-1234",
  "workType": "feature",
  "phase": "requirements",
  "generation": 1,
  "status": "awaiting_approval",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011"
  },
  "generatedPersona": "product-owner",
  "sourceCommit": "ff04c6890777784b6631a4996f6844b5fef458b3",
  "generationCommit": "4baf826c304c4e3e34c75c8198babc8c4fe2a839",
  "publicationCommit": "4baf826c304c4e3e34c75c8198babc8c4fe2a839",
  "configSha256": "c5e1429897a228e5eb78c395ddf91788e7cf18fc507a3622b052bb00c245174a",
  "sourceSha256": "71a41fa561937851d6546f0b2bf54588bbf094b28722b3f6425c2c46a7d6b000",
  "template": {
    "path": ".singularity/templates/feature/requirements.md",
    "sha256": "92a9807dfe4a2a986823450c24caffdb07205c36e2d963db74d5b8fc9092a9eb"
  },
  "inputs": null,
  "remoteAgent": null,
  "telemetry": [
    {
      "generation": 1,
      "path": ".singularity/work-items/WORK-1234/telemetry/requirements-gen1.json",
      "sha256": "115b2a1f971b8e38c94f458bea71623d177eeeaeb8902334055253493df8062d",
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
      "startedAt": "2026-07-22T23:49:13.536Z",
      "completedAt": "2026-07-22T23:49:13.536Z",
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

# WORK-1234 — Feature Requirements

## Problem and outcome

The rule engine (`org.example.rules`) currently supports only comparison, collection/string, pattern, range, and existence operators (`Operator.java`: `eq`, `ne`, `lt`, `lte`, `gt`, `gte`, `contains`, `in`, `regex`, `between`, `exists`, `not_exists`, `isNull`, `isNotNull`). There is no way for a rule to compute or validate the area of a circle from a given radius. The measurable outcome is: given a numeric radius value supplied as a fact/rule input, the engine computes `area = π × radius²` and makes that value available for rule evaluation (e.g., via a new operator or derived value), with automated tests confirming correct results for valid numeric radii and correct rejection/error behavior for non-numeric (string) input.

## Scope

**In scope:**
- Accept numeric radius inputs (integers and decimals, including zero and positive values).
- Compute circle area using `area = π × radius²`.
- Make the computed area usable within rule conditions/actions consistent with existing `Operator` evaluation patterns.
- Unit/integration tests validating correct area computation for representative numeric radii.

**Out of scope:**
- String or non-numeric radius inputs (explicitly unsupported per intake; behavior should be a defined validation error, not silent coercion).
- Negative radius handling beyond a defined validation rule (not specified by stakeholder; flagged as open question below).
- Changes to unrelated existing operators in `Operator.java`.

## Acceptance criteria

- AC-001: Given a positive numeric radius (integer or decimal), the engine computes the circle area as `π × radius²` within acceptable floating-point precision.
- AC-002: Given a radius of `0`, the engine computes an area of `0`.
- AC-003: Given a non-numeric (string) radius input, the engine rejects the input (validation error) rather than computing an incorrect result.
- AC-004: Automated tests (unit and/or integration, following the existing `RuleEngineServiceTest.java` conventions) cover AC-001–AC-003 and pass.
- AC-005: The new capability integrates with the existing rule/operator evaluation flow (`RuleEngineService.java`) without breaking existing operator behavior.

## Dependencies, risks, and open questions

- **Dependencies:** None identified (self-contained addition to the existing rule engine).
- **Risks:** None identified by stakeholders; floating-point precision in area computation is a standard, low risk.
- **Open questions:**
  - OQ-1: Should negative radius values be rejected as invalid, or is behavior for negative input undefined/out of scope? (Not specified in intake; stakeholder PZN to confirm during design.)
  - OQ-2: Should the capability be exposed as a new `Operator` enum value (e.g., `circle_area`) or as a derived/computed fact available to existing operators? Decision deferred to the design phase.
