<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "implementation",
  "generation": 1,
  "status": "in_progress",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": "developer",
  "authorship": {
    "schemaVersion": 1,
    "producer": "governed-agent",
    "channel": "copilot-host",
    "actor": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    },
    "governedAgentContext": {
      "agentId": "developer"
    },
    "kernelModel": {
      "invoked": false,
      "status": "exact",
      "invocationIds": []
    },
    "externalAiUse": {
      "value": "unknown",
      "status": "unavailable"
    },
    "source": {
      "kind": "in-place",
      "filename": "implementation-summary.md",
      "mediaType": "text/markdown",
      "sha256": "700f0cec5deb296f92ffaaa22fca9318f5686be7617f266c271347b6473a9690",
      "bytes": 64953
    },
    "generation": 1,
    "publishedAt": "2026-08-22T15:45:43.394Z"
  },
  "sourceCommit": "b13d76e013f8995ec582326a08837f89ff9c642d",
  "generationCommit": null,
  "publicationCommit": null,
  "configSha256": "3bdacb436bc709f61acdd7a587e3c64c3f1401d861ba0ae781de15bf96568b9b",
  "sourceSha256": "142becb19837037c336774addd45db0b908d38715d71a2782805c230756620ca",
  "template": {
    "path": "singularity/templates/common/implementation.md",
    "sha256": "5d0478b18c8fd14221e14c68e6238b909bccd6802a70262c416005354716c62c"
  },
  "inputs": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/inputs-implementation-gen1.json",
    "sha256": "0b7b82a3e52493fafc0e595de9b77cfcd6ec8ee2dc3f02f5c93292dd7176ad58",
    "renderedSha256": "ed919d41ac3ab890180a5b5f1ce8aa6ec9a550a8ed91c6b81f926db56d9c8e7e",
    "mode": "enforce"
  },
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": null,
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/ANU-STORY/telemetry/implementation-gen1.json",
      "sha256": "298f8ff8ed7e0280a6315e42cf5eeef5f302e52492ac0cdf09bf75eb79cd5b79",
      "status": "pending",
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
      "requestedModel": null,
      "resolvedModel": null,
      "resolvedModelAssurance": "unavailable",
      "inputTokens": null,
      "outputTokens": null,
      "cachedInputTokens": null,
      "cacheWriteInputTokens": null,
      "totalTokens": null,
      "providerCost": null,
      "costStatus": "unavailable",
      "spans": null,
      "startedAt": "2026-08-22T15:45:43.394Z",
      "completedAt": "2026-08-22T15:45:43.394Z",
      "agent": "developer",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [],
  "selfApproval": false,
  "conformanceTree": null
}
-->

# ANU-STORY — Implementation Summary

## Implemented outcome

The implementation work for ANU-STORY will add a reusable interest-calculation capability to the existing Spring Boot rule engine structure. The flow is designed around a dedicated service and a thin HTTP endpoint so interest scenarios can be evaluated deterministically without coupling the logic to the console entry point.

## Changed components and decisions

- Add a domain-oriented interest service that owns validation and simple-interest calculation.
- Introduce request and response DTOs for principal, rate, period, and calculated interest values.
- Expose the capability through a thin controller endpoint at /interest/calculate while keeping the change additive to the current API surface.
- Keep validation in the service layer and avoid persistence, UI, or background-job changes, in line with the approved scope.
- Follow the approved design decisions: simple interest only, with rate expressed as a decimal fraction and invalid or negative values rejected with clear validation errors.

## Tests and operational notes

- Add example-driven tests for valid calculation scenarios and invalid input handling (SPEC-002).
- Verify behavior through Maven test execution and sample requests to the new endpoint.
- No migrations, rollout flags, or operational handoffs are required for this additive change.

<!-- singularity-flow:inputs:start -->

# Approved phase inputs

## Approved phase input: design

<!-- source=singularity/work-items/ANU-STORY/artifacts/design/design.md sha256=428779d668f0575a9479582a176fbeed9f12bf9cf39931ae742a05887d546199 status=captured projection=full -->

<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "design",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": "product-owner",
  "authorship": {
    "schemaVersion": 1,
    "producer": "governed-agent",
    "channel": "copilot-host",
    "actor": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    },
    "governedAgentContext": {
      "agentId": "product-owner"
    },
    "kernelModel": {
      "invoked": false,
      "status": "exact",
      "invocationIds": []
    },
    "externalAiUse": {
      "value": "unknown",
      "status": "unavailable"
    },
    "source": {
      "kind": "in-place",
      "filename": "design.md",
      "mediaType": "text/markdown",
      "sha256": "59c0cee7232d94cbd590bd84cc77371e0b1affe7c7494c65b5b51f3a13944418",
      "bytes": 16578
    },
    "generation": 1,
    "publishedAt": "2026-08-22T13:58:21.936Z"
  },
  "sourceCommit": "148aa762ca0695ad7054d51f7e04f527cbcbbc23",
  "generationCommit": "c3c44c1d401b93e18fd2f204ed23343534078d79",
  "publicationCommit": "c3c44c1d401b93e18fd2f204ed23343534078d79",
  "configSha256": "3bdacb436bc709f61acdd7a587e3c64c3f1401d861ba0ae781de15bf96568b9b",
  "sourceSha256": "142becb19837037c336774addd45db0b908d38715d71a2782805c230756620ca",
  "template": {
    "path": "singularity/templates/feature/design.md",
    "sha256": "8b7455f464a7025efa92942c272a04e3c0a3ab2a4d3eb438703cc14e230bc856"
  },
  "inputs": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/inputs-design-gen1.json",
    "sha256": "2af40a1cdb9da68e27d18295c3aa92b88914efdec2d982984dd804346c8dd75d",
    "renderedSha256": "7a7740cd071c93f6d8af7403c1c1044e9bc18ab9fd0a53bf0911578498cc2908",
    "mode": "enforce"
  },
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/clarifications-design-gen1.json",
    "sha256": "f1de14f6b50350e0941cf65673bfc8842b89f5c80e98c2b406a69b3763625b60",
    "promptSha256": "33add69e0128a262be2caf38faf630c81b31f2fff515b096d4571a049da7baa8",
    "responses": 1,
    "markers": [],
    "recordedAt": "2026-08-22T13:58:12.469Z",
    "recordedBy": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    }
  },
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/ANU-STORY/telemetry/design-gen1.json",
      "sha256": "600dcf49584a50486d37ef31df61d95402a2fd25ba92b6d0e933a0bf15cacfe1",
      "status": "pending",
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
      "startedAt": "2026-08-22T13:58:21.936Z",
      "completedAt": "2026-08-22T13:58:21.936Z",
      "agent": "product-owner",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "design",
      "at": "2026-08-22T14:09:08.558Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011",
        "githubLookup": "resolved"
      },
      "agent": "architect",
      "authorityGroup": "architecture-reviewers",
      "identityAssurance": "configured-local",
      "channel": "terminal",
      "generation": 1,
      "artifactSha256": [
        {
          "path": "singularity/work-items/ANU-STORY/artifacts/design/design.md",
          "sha256": "20f40291a2021fa06ea3085d6fa28186c465a7d6670bbcfaba8457bad629932d"
        }
      ],
      "reviewPacketSha256": "c907a6e89f5904ed4ebc36c6ba0da073adbbfff93d51040996cd3d67b291fa2e",
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# ANU-STORY — Architecture and Design

## Context and constraints

The approved requirements define a reusable interest-calculation capability that accepts principal, rate, and period values, rejects invalid or negative input values, calculates simple interest deterministically, and produces a result that can be verified through example scenarios. The current repository is a minimal Spring Boot application with a placeholder entry point in Main and no existing interest domain model or validation layer. The design must stay within the existing Java/Spring Boot structure, avoid persistence and UI work, and remain additive to the current API surface.

## Proposed design

Implement the capability as a small domain service and an optional HTTP endpoint so the rule engine can evaluate interest scenarios without coupling the logic to the console entry point.

1. Add a domain-oriented service, `InterestCalculationService`, in the existing Java package structure. The service will own the calculation logic and validation rules for the initial release.
2. Define a request/response contract for the calculation flow:
   - `InterestRequest`: `principal`, `rate`, and `period`
   - `InterestResult`: `principal`, `rate`, `period`, `interest`, and `resultType`
3. Apply validation in the service layer before calculation:
   - `principal` must be non-negative
   - `rate` must be non-negative
   - `period` must be non-negative
   - invalid input values must return a clear validation error rather than a silent fallback
4. Use the simple-interest formula `interest = principal × rate × period` for the initial release. For clarity and consistency, the design assumes the input rate is expressed as a decimal fraction (for example, `0.05` for `5%`).
5. Expose the capability through a thin Spring MVC endpoint, such as `POST /interest/calculate`, backed by the service. This keeps the implementation testable and preserves compatibility with the existing Spring Boot web stack.
6. Keep the implementation isolated from persistence, background jobs, and UI concerns so it remains aligned with the approved scope.

The design also records two stable identifiers for the implementation follow-up:
- `SPEC-001` — Implement the interest calculation service, validation rules, and endpoint contract.
- `SPEC-002` — Add example-driven tests that verify valid calculations and invalid input handling.

## Security, observability, migration, and rollback

Security concerns are limited because the capability does not handle secrets, user accounts, or external integrations. The main controls are strict input validation, explicit error responses for invalid data, and no persistence or side effects in the calculation path. Observability can be added through structured request logs and response metadata without changing the public contract. Migration and rollback are low risk because the design is additive: the new endpoint and service can be introduced without changing existing routes or stored data. If issues are discovered after release, the endpoint can be disabled or reverted independently from the rest of the application.

## Alternatives and decisions

Alternative 1: Place the calculation logic directly in the existing `Main` class. This was rejected because it would couple the feature to the console entry point and make testing and reuse harder.

Alternative 2: Add a dedicated service plus a thin controller. This was selected because it keeps the logic domain-focused, testable, and aligned with the Spring Boot architecture already present in the repository.

Decision record:
- `AC-001` is satisfied by the service-level validation and calculation flow described above.
- The initial release will support simple interest only, with rate input expressed as a decimal fraction to avoid ambiguity.
- The implementation should remain additive and not introduce persistence or UI work.

<!-- approved source inputs:start -->

# Approved phase inputs

## Approved phase input: requirements

<!-- source=artifacts/requirements/requirements.md sha256=0e042063c4f8d950b74a69c7d670bd38876c68a1fbc16f4d2d5dfcf911426de2 status=captured projection=full -->

<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "requirements",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": "product-owner",
  "authorship": {
    "schemaVersion": 1,
    "producer": "governed-agent",
    "channel": "copilot-host",
    "actor": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    },
    "governedAgentContext": {
      "agentId": "product-owner"
    },
    "kernelModel": {
      "invoked": false,
      "status": "exact",
      "invocationIds": []
    },
    "externalAiUse": {
      "value": "unknown",
      "status": "unavailable"
    },
    "source": {
      "kind": "in-place",
      "filename": "requirements.md",
      "mediaType": "text/markdown",
      "sha256": "2ce85cbe1456c316a7c39c650c8ddb967e305a07ac861831c9f13d574fdc970b",
      "bytes": 7830
    },
    "generation": 1,
    "publishedAt": "2026-08-22T12:44:12.645Z"
  },
  "sourceCommit": "470d0c7d92e732fa1b51f3ce35f3df5e71c6d93a",
  "generationCommit": "91f7bef9e16861ae5d42e2701bba28c83dd00278",
  "publicationCommit": "91f7bef9e16861ae5d42e2701bba28c83dd00278",
  "configSha256": "3bdacb436bc709f61acdd7a587e3c64c3f1401d861ba0ae781de15bf96568b9b",
  "sourceSha256": "142becb19837037c336774addd45db0b908d38715d71a2782805c230756620ca",
  "template": {
    "path": "singularity/templates/feature/requirements.md",
    "sha256": "32016db8ed6fadd6596e7dc702647cff95cdee1a203b38395d7ba5626dd8134e"
  },
  "inputs": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/inputs-requirements-gen1.json",
    "sha256": "8e51697c0211270485f515e011ae4b038ee76fd19b78676130f87768c654dc50",
    "renderedSha256": "4262a420498bce7cf0b193afab32653bff2031bd68b6d93178b787fa9fa9267d",
    "mode": "enforce"
  },
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/clarifications-requirements-gen1.json",
    "sha256": "35d3512918ebc252869713b3ee80c76d9d93715fa75982800d4189e2757eb9bc",
    "promptSha256": "b36631aebe96ef2ff13cf4294362302dfc8b232ced801581e974403aadbd2669",
    "responses": 1,
    "markers": [],
    "recordedAt": "2026-08-22T12:44:07.362Z",
    "recordedBy": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    }
  },
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/ANU-STORY/telemetry/requirements-gen1.json",
      "sha256": "c2fa36259cf8acaffe8aff72a84b36e2f49f8486ee8f07bec611731ed6a22931",
      "status": "pending",
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
      "startedAt": "2026-08-22T12:44:12.645Z",
      "completedAt": "2026-08-22T12:44:12.645Z",
      "agent": "product-owner",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "requirements",
      "at": "2026-08-22T12:54:44.379Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011",
        "githubLookup": "resolved"
      },
      "agent": "product-owner",
      "authorityGroup": "product-approvers",
      "identityAssurance": "configured-local",
      "channel": "terminal",
      "generation": 1,
      "artifactSha256": [
        {
          "path": "singularity/work-items/ANU-STORY/artifacts/requirements/requirements.md",
          "sha256": "7f50fd9bd464c525e1a16f93dae6eeea92e3c8fee4d27125385cd544ec7d0bf3"
        }
      ],
      "reviewPacketSha256": "9e2ab4eba5bc90e8fba62eb77c1f148f70ea80477b97c2a429c6f38684acaa96",
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# ANU-STORY — Feature Requirements

## Problem and outcome

The requested capability enables financial rules to calculate simple interest from a principal value, an annual rate, and a period so that the rule engine can support interest-based decision rules in a consistent way. The measurable outcome is a deterministic interest amount that can be validated with example inputs. [ANU-STORY:REQ-001]

## Scope

The implementation includes a reusable interest-calculation path that accepts principal, rate, and period values, validates inputs, and returns a normalized interest result. It explicitly excludes integrations with external billing systems, persistence changes, or user-interface work. [ANU-STORY:CON-001]

## Acceptance criteria

The completed behavior MUST accept principal, rate, and period values; MUST reject invalid or negative inputs with a clear validation error; MUST calculate simple interest using a documented formula; and MUST return a result that can be verified with example scenarios. [ANU-STORY:AC-001]

## Dependencies, risks, and open questions

Dependencies include the existing rule-engine expression and numeric handling paths. Risks include ambiguous rate semantics and inconsistent handling of period units. Open questions include whether the initial release should support only simple interest or also compound interest, and whether rate input should be expressed as a decimal or a percentage. [ANU-STORY:DE-001]

<!-- approved source inputs:start -->

# Approved phase inputs

## Approved phase input: intake

<!-- source=artifacts/intake/intake.md sha256=cc4f2e89a17fe5e84fdcae5c818d79dc72e60cbf0d9917cb9008915e1369f0ae status=captured projection=full -->

<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "intake",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": "product-owner",
  "authorship": {
    "schemaVersion": 1,
    "producer": "governed-agent",
    "channel": "copilot-host",
    "actor": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    },
    "governedAgentContext": {
      "agentId": "product-owner"
    },
    "kernelModel": {
      "invoked": false,
      "status": "exact",
      "invocationIds": []
    },
    "externalAiUse": {
      "value": "unknown",
      "status": "unavailable"
    },
    "source": {
      "kind": "in-place",
      "filename": "intake.md",
      "mediaType": "text/markdown",
      "sha256": "05c86408b0608f471d05809422ffbac68b025275fe93641e09d6216fed354f68",
      "bytes": 1247
    },
    "generation": 1,
    "publishedAt": "2026-08-22T12:36:25.942Z"
  },
  "sourceCommit": "97026e5d0e6fb4e53fa68d8ed071ffa76beedabb",
  "generationCommit": "759609df8cdb1acd3136f5c4762ab28584c31d2e",
  "publicationCommit": "759609df8cdb1acd3136f5c4762ab28584c31d2e",
  "configSha256": "3bdacb436bc709f61acdd7a587e3c64c3f1401d861ba0ae781de15bf96568b9b",
  "sourceSha256": "142becb19837037c336774addd45db0b908d38715d71a2782805c230756620ca",
  "template": {
    "path": "singularity/templates/feature/intake.md",
    "sha256": "eb53814f46f12ea3d93d1629164bd7ff22a3a54feceff7f7dd55670caeb5dbab"
  },
  "inputs": null,
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/clarifications-intake-gen1.json",
    "sha256": "fc66b446cb78fbd6491ac7d861c8297789620bc7a63620f3e84cb7a4d347c7f8",
    "promptSha256": "f42994d4ca1939d0b5d380b56e45e493b0cd4f199264917f6b2983fb70ceeca2",
    "responses": 1,
    "markers": [],
    "recordedAt": "2026-08-22T12:36:20.563Z",
    "recordedBy": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    }
  },
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/ANU-STORY/telemetry/intake-gen1.json",
      "sha256": "c057a1781f32f4bfb8be24a5d90a80dd2789263702526bcf3081ed8560aba15a",
      "status": "pending",
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
      "startedAt": "2026-08-22T12:36:25.942Z",
      "completedAt": "2026-08-22T12:36:25.942Z",
      "agent": "product-owner",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "intake",
      "at": "2026-08-22T12:38:41.817Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011",
        "githubLookup": "resolved"
      },
      "agent": "product-owner",
      "authorityGroup": "product-approvers",
      "identityAssurance": "configured-local",
      "channel": "copilot-selection-receipt",
      "generation": 1,
      "artifactSha256": [
        {
          "path": "singularity/work-items/ANU-STORY/artifacts/intake/intake.md",
          "sha256": "b37a8c0c3684410f4a8223e0612e63fc94fcd4668651316253dc15a300a4e227"
        }
      ],
      "reviewPacketSha256": "c1a371f2724c59f520a3fa747ce230b006c5d31b24220eb13150989137ba1d6e",
      "actionContext": {
        "phase": "intake",
        "label": "Intake",
        "generation": 1,
        "submittedAt": "2026-08-22T12:37:18.083Z",
        "artifacts": [
          {
            "path": "singularity/work-items/ANU-STORY/artifacts/intake/intake.md",
            "sha256": "b37a8c0c3684410f4a8223e0612e63fc94fcd4668651316253dc15a300a4e227"
          }
        ],
        "agentBriefs": [],
        "reviewPacketSha256": "c1a371f2724c59f520a3fa747ce230b006c5d31b24220eb13150989137ba1d6e",
        "submittedSourceCommit": "759609df8cdb1acd3136f5c4762ab28584c31d2e",
        "planId": "e6aeea3fb993c621dc02d875"
      },
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# ANU-STORY — Feature Intake

## User and outcome

Business users need a reliable way to calculate interest amounts from a principal value, a rate, and a period so that the rule engine can support financial decision rules in a consistent way. The success measure is a clear, repeatable calculation path that can be validated with example inputs and produces the expected interest result.

## Proposed capability

Add an interest-calculation capability to the rule engine workflow so that a rule or request can evaluate interest-related scenarios without bespoke implementation work. The capability should be described as part of the product intake so the implementation team can design and build it against a shared understanding of the user need.

## Scope, constraints, and stakeholders

The scope is limited to intake and planning for the interest-calculation feature; implementation details should be deferred until the requirements and design phases. Constraints include keeping the change aligned with the existing rule-engine API and avoiding assumptions about external billing or persistence systems. Stakeholders include the product owner, the engineering team, and any downstream users who will rely on the new calculation capability.

<!-- approved source inputs:end -->

<!-- approved source inputs:end -->

## Approved phase input: implementation-spec

<!-- source=singularity/work-items/ANU-STORY/artifacts/implementation-spec/implementation-spec.md sha256=3d45eb4a2eb29470185d7381fbcda953cb1fbe457a0e0512fd9f215cbe51fa6c status=captured projection=full -->

<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "implementation-spec",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": "architect",
  "authorship": {
    "schemaVersion": 1,
    "producer": "governed-agent",
    "channel": "copilot-host",
    "actor": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    },
    "governedAgentContext": {
      "agentId": "architect"
    },
    "kernelModel": {
      "invoked": false,
      "status": "exact",
      "invocationIds": []
    },
    "externalAiUse": {
      "value": "unknown",
      "status": "unavailable"
    },
    "source": {
      "kind": "in-place",
      "filename": "implementation-spec.md",
      "mediaType": "text/markdown",
      "sha256": "e30abe35774e0592cfb6fc7b685e167d63a84fa0e99d2f946c8f996fea0c5f3d",
      "bytes": 37346
    },
    "generation": 1,
    "publishedAt": "2026-08-22T14:20:50.164Z"
  },
  "sourceCommit": "e842290ae2775bbd1f374b4c4c9ab6fb0575fa60",
  "generationCommit": "1e41a047c4d38cf9a21ed2e3171f488dafa9ab3e",
  "publicationCommit": "1e41a047c4d38cf9a21ed2e3171f488dafa9ab3e",
  "configSha256": "3bdacb436bc709f61acdd7a587e3c64c3f1401d861ba0ae781de15bf96568b9b",
  "sourceSha256": "142becb19837037c336774addd45db0b908d38715d71a2782805c230756620ca",
  "template": {
    "path": "singularity/templates/feature/implementation-spec.md",
    "sha256": "f6b06a7e8c8dfa87a7f1289b2a80c0a6e98f5ee8e3cae2fe9faed501c031656c"
  },
  "inputs": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/inputs-implementation-spec-gen1.json",
    "sha256": "1a091b749afc0165c3afe40703538e7906ab61c5bc6c8fd7e793a4b5c27fb2dc",
    "renderedSha256": "c9d3083454bf6ac7112ed971d8ff9469434db9db21dcfce32d008083c700e5eb",
    "mode": "enforce"
  },
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/clarifications-implementation-spec-gen1.json",
    "sha256": "6e61d5ca113b85bb12e8f166551462de544cf9f509df1fb265125801fd925d34",
    "promptSha256": "fe445267ca8ea700baa96c0b1f10437c3d85d37834bfe4102d8cac47255072b9",
    "responses": 1,
    "markers": [],
    "recordedAt": "2026-08-22T14:20:44.881Z",
    "recordedBy": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    }
  },
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/ANU-STORY/telemetry/implementation-spec-gen1.json",
      "sha256": "444280952f0367900fff49a84a48caf458c04482ed2c564dce33bae26daa183b",
      "status": "pending",
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
      "startedAt": "2026-08-22T14:20:50.164Z",
      "completedAt": "2026-08-22T14:20:50.164Z",
      "agent": "architect",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "implementation-spec",
      "at": "2026-08-22T14:28:52.961Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011",
        "githubLookup": "resolved"
      },
      "agent": "architect",
      "authorityGroup": "architecture-reviewers",
      "identityAssurance": "configured-local",
      "channel": "terminal",
      "generation": 1,
      "artifactSha256": [
        {
          "path": "singularity/work-items/ANU-STORY/artifacts/implementation-spec/implementation-spec.md",
          "sha256": "ef7a1339a70cc90cad523440a9b22f848e804d9f6c5358e296404b2f16d028b7"
        }
      ],
      "reviewPacketSha256": "e5255577beb0b3e2cca514f1b66429b3f58ec4856115a92ee4f7fcdb4a8c50e3",
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# ANU-STORY — Implementation Specification

## Traceability

| Clause | Acceptance criteria | Planned code/tests | Status |
|---|---|---|---|
| `ANU-STORY:IFC-001` | `ANU-STORY:AC-001` | Add a service and controller plus example-driven tests | planned |
| `ANU-STORY:IFC-002` | `ANU-STORY:AC-001` | Validate negative and invalid input values before calculation | planned |
| `ANU-STORY:IFC-003` | `ANU-STORY:AC-001` | Add request/response models and endpoint contract coverage | planned |

## APIs, schemas, and contracts

The implementation MUST preserve or introduce the following exact contract for the initial release: a `POST /interest/calculate` HTTP endpoint that accepts JSON with `principal`, `rate`, and `period`, and returns a JSON body with `principal`, `rate`, `period`, `interest`, and `resultType`. The service layer MUST reject non-negative validation failures with a clear error response rather than silently falling back. [ANU-STORY:IFC-001]

The request and response payloads are defined as follows:

- Request: `{"principal": 1000, "rate": 0.05, "period": 2}`
- Response: `{"principal": 1000, "rate": 0.05, "period": 2, "interest": 100, "resultType": "simple-interest"}`
- Invalid request example: a negative `principal`, `rate`, or `period` returns a validation error payload and HTTP 400 semantics.

## File-level implementation plan

The implementation will remain additive and keep the change isolated to the Spring Boot application surface.

1. Add `InterestCalculationService` in the existing Java package structure to own calculation logic and validation.
2. Add request and response value objects for the endpoint contract, such as `InterestRequest` and `InterestResult`.
3. Add a thin Spring MVC controller that exposes `POST /interest/calculate` and delegates to the service.
4. Add example-driven tests that cover valid calculations, invalid negative values, and a representative success path through the controller layer.
5. Keep the implementation free of persistence, UI, and background job dependencies so it remains aligned with the approved scope.

## Security, observability, migration, and rollback

The implementation MUST satisfy the approved security and operational expectations by validating inputs strictly, returning explicit errors for invalid data, and avoiding persistence or side effects in the calculation path. Structured request logs and response metadata can be added without changing the public contract. Migration and rollback remain low risk because the endpoint and service are additive; the new code can be disabled or reverted independently if issues appear after release. [ANU-STORY:CON-002]

## Test specification

The implementation MUST be verified through allowlisted tests that cover the approved requirements and design clauses:

- `REQ-001`: test a successful simple-interest calculation with a known input and expected result.
- `AC-001`: test that valid inputs produce a deterministic result and invalid or negative values produce a clear validation error.
- `IFC-001`: test that the HTTP contract accepts the request shape and returns the documented response shape.
- `IFC-002`: test that validation failures occur before any calculation is performed.
- `IFC-003`: test the controller/service boundary and the expected JSON contract.
- `CON-002`: test that the implementation does not introduce persistence or side-effect behavior in the calculation path.

<!-- approved source inputs:start -->

# Approved phase inputs

## Approved phase input: requirements

<!-- source=artifacts/requirements/requirements.md sha256=0e042063c4f8d950b74a69c7d670bd38876c68a1fbc16f4d2d5dfcf911426de2 status=captured projection=full -->

<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "requirements",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": "product-owner",
  "authorship": {
    "schemaVersion": 1,
    "producer": "governed-agent",
    "channel": "copilot-host",
    "actor": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    },
    "governedAgentContext": {
      "agentId": "product-owner"
    },
    "kernelModel": {
      "invoked": false,
      "status": "exact",
      "invocationIds": []
    },
    "externalAiUse": {
      "value": "unknown",
      "status": "unavailable"
    },
    "source": {
      "kind": "in-place",
      "filename": "requirements.md",
      "mediaType": "text/markdown",
      "sha256": "2ce85cbe1456c316a7c39c650c8ddb967e305a07ac861831c9f13d574fdc970b",
      "bytes": 7830
    },
    "generation": 1,
    "publishedAt": "2026-08-22T12:44:12.645Z"
  },
  "sourceCommit": "470d0c7d92e732fa1b51f3ce35f3df5e71c6d93a",
  "generationCommit": "91f7bef9e16861ae5d42e2701bba28c83dd00278",
  "publicationCommit": "91f7bef9e16861ae5d42e2701bba28c83dd00278",
  "configSha256": "3bdacb436bc709f61acdd7a587e3c64c3f1401d861ba0ae781de15bf96568b9b",
  "sourceSha256": "142becb19837037c336774addd45db0b908d38715d71a2782805c230756620ca",
  "template": {
    "path": "singularity/templates/feature/requirements.md",
    "sha256": "32016db8ed6fadd6596e7dc702647cff95cdee1a203b38395d7ba5626dd8134e"
  },
  "inputs": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/inputs-requirements-gen1.json",
    "sha256": "8e51697c0211270485f515e011ae4b038ee76fd19b78676130f87768c654dc50",
    "renderedSha256": "4262a420498bce7cf0b193afab32653bff2031bd68b6d93178b787fa9fa9267d",
    "mode": "enforce"
  },
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/clarifications-requirements-gen1.json",
    "sha256": "35d3512918ebc252869713b3ee80c76d9d93715fa75982800d4189e2757eb9bc",
    "promptSha256": "b36631aebe96ef2ff13cf4294362302dfc8b232ced801581e974403aadbd2669",
    "responses": 1,
    "markers": [],
    "recordedAt": "2026-08-22T12:44:07.362Z",
    "recordedBy": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    }
  },
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/ANU-STORY/telemetry/requirements-gen1.json",
      "sha256": "c2fa36259cf8acaffe8aff72a84b36e2f49f8486ee8f07bec611731ed6a22931",
      "status": "pending",
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
      "startedAt": "2026-08-22T12:44:12.645Z",
      "completedAt": "2026-08-22T12:44:12.645Z",
      "agent": "product-owner",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "requirements",
      "at": "2026-08-22T12:54:44.379Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011",
        "githubLookup": "resolved"
      },
      "agent": "product-owner",
      "authorityGroup": "product-approvers",
      "identityAssurance": "configured-local",
      "channel": "terminal",
      "generation": 1,
      "artifactSha256": [
        {
          "path": "singularity/work-items/ANU-STORY/artifacts/requirements/requirements.md",
          "sha256": "7f50fd9bd464c525e1a16f93dae6eeea92e3c8fee4d27125385cd544ec7d0bf3"
        }
      ],
      "reviewPacketSha256": "9e2ab4eba5bc90e8fba62eb77c1f148f70ea80477b97c2a429c6f38684acaa96",
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# ANU-STORY — Feature Requirements

## Problem and outcome

The requested capability enables financial rules to calculate simple interest from a principal value, an annual rate, and a period so that the rule engine can support interest-based decision rules in a consistent way. The measurable outcome is a deterministic interest amount that can be validated with example inputs. [ANU-STORY:REQ-001]

## Scope

The implementation includes a reusable interest-calculation path that accepts principal, rate, and period values, validates inputs, and returns a normalized interest result. It explicitly excludes integrations with external billing systems, persistence changes, or user-interface work. [ANU-STORY:CON-001]

## Acceptance criteria

The completed behavior MUST accept principal, rate, and period values; MUST reject invalid or negative inputs with a clear validation error; MUST calculate simple interest using a documented formula; and MUST return a result that can be verified with example scenarios. [ANU-STORY:AC-001]

## Dependencies, risks, and open questions

Dependencies include the existing rule-engine expression and numeric handling paths. Risks include ambiguous rate semantics and inconsistent handling of period units. Open questions include whether the initial release should support only simple interest or also compound interest, and whether rate input should be expressed as a decimal or a percentage. [ANU-STORY:DE-001]

<!-- approved source inputs:start -->

# Approved phase inputs

## Approved phase input: intake

<!-- source=artifacts/intake/intake.md sha256=cc4f2e89a17fe5e84fdcae5c818d79dc72e60cbf0d9917cb9008915e1369f0ae status=captured projection=full -->

<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "intake",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": "product-owner",
  "authorship": {
    "schemaVersion": 1,
    "producer": "governed-agent",
    "channel": "copilot-host",
    "actor": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    },
    "governedAgentContext": {
      "agentId": "product-owner"
    },
    "kernelModel": {
      "invoked": false,
      "status": "exact",
      "invocationIds": []
    },
    "externalAiUse": {
      "value": "unknown",
      "status": "unavailable"
    },
    "source": {
      "kind": "in-place",
      "filename": "intake.md",
      "mediaType": "text/markdown",
      "sha256": "05c86408b0608f471d05809422ffbac68b025275fe93641e09d6216fed354f68",
      "bytes": 1247
    },
    "generation": 1,
    "publishedAt": "2026-08-22T12:36:25.942Z"
  },
  "sourceCommit": "97026e5d0e6fb4e53fa68d8ed071ffa76beedabb",
  "generationCommit": "759609df8cdb1acd3136f5c4762ab28584c31d2e",
  "publicationCommit": "759609df8cdb1acd3136f5c4762ab28584c31d2e",
  "configSha256": "3bdacb436bc709f61acdd7a587e3c64c3f1401d861ba0ae781de15bf96568b9b",
  "sourceSha256": "142becb19837037c336774addd45db0b908d38715d71a2782805c230756620ca",
  "template": {
    "path": "singularity/templates/feature/intake.md",
    "sha256": "eb53814f46f12ea3d93d1629164bd7ff22a3a54feceff7f7dd55670caeb5dbab"
  },
  "inputs": null,
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/clarifications-intake-gen1.json",
    "sha256": "fc66b446cb78fbd6491ac7d861c8297789620bc7a63620f3e84cb7a4d347c7f8",
    "promptSha256": "f42994d4ca1939d0b5d380b56e45e493b0cd4f199264917f6b2983fb70ceeca2",
    "responses": 1,
    "markers": [],
    "recordedAt": "2026-08-22T12:36:20.563Z",
    "recordedBy": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    }
  },
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/ANU-STORY/telemetry/intake-gen1.json",
      "sha256": "c057a1781f32f4bfb8be24a5d90a80dd2789263702526bcf3081ed8560aba15a",
      "status": "pending",
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
      "startedAt": "2026-08-22T12:36:25.942Z",
      "completedAt": "2026-08-22T12:36:25.942Z",
      "agent": "product-owner",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "intake",
      "at": "2026-08-22T12:38:41.817Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011",
        "githubLookup": "resolved"
      },
      "agent": "product-owner",
      "authorityGroup": "product-approvers",
      "identityAssurance": "configured-local",
      "channel": "copilot-selection-receipt",
      "generation": 1,
      "artifactSha256": [
        {
          "path": "singularity/work-items/ANU-STORY/artifacts/intake/intake.md",
          "sha256": "b37a8c0c3684410f4a8223e0612e63fc94fcd4668651316253dc15a300a4e227"
        }
      ],
      "reviewPacketSha256": "c1a371f2724c59f520a3fa747ce230b006c5d31b24220eb13150989137ba1d6e",
      "actionContext": {
        "phase": "intake",
        "label": "Intake",
        "generation": 1,
        "submittedAt": "2026-08-22T12:37:18.083Z",
        "artifacts": [
          {
            "path": "singularity/work-items/ANU-STORY/artifacts/intake/intake.md",
            "sha256": "b37a8c0c3684410f4a8223e0612e63fc94fcd4668651316253dc15a300a4e227"
          }
        ],
        "agentBriefs": [],
        "reviewPacketSha256": "c1a371f2724c59f520a3fa747ce230b006c5d31b24220eb13150989137ba1d6e",
        "submittedSourceCommit": "759609df8cdb1acd3136f5c4762ab28584c31d2e",
        "planId": "e6aeea3fb993c621dc02d875"
      },
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# ANU-STORY — Feature Intake

## User and outcome

Business users need a reliable way to calculate interest amounts from a principal value, a rate, and a period so that the rule engine can support financial decision rules in a consistent way. The success measure is a clear, repeatable calculation path that can be validated with example inputs and produces the expected interest result.

## Proposed capability

Add an interest-calculation capability to the rule engine workflow so that a rule or request can evaluate interest-related scenarios without bespoke implementation work. The capability should be described as part of the product intake so the implementation team can design and build it against a shared understanding of the user need.

## Scope, constraints, and stakeholders

The scope is limited to intake and planning for the interest-calculation feature; implementation details should be deferred until the requirements and design phases. Constraints include keeping the change aligned with the existing rule-engine API and avoiding assumptions about external billing or persistence systems. Stakeholders include the product owner, the engineering team, and any downstream users who will rely on the new calculation capability.

<!-- approved source inputs:end -->

## Approved phase input: design

<!-- source=artifacts/design/design.md sha256=428779d668f0575a9479582a176fbeed9f12bf9cf39931ae742a05887d546199 status=captured projection=full -->

<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "design",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": "product-owner",
  "authorship": {
    "schemaVersion": 1,
    "producer": "governed-agent",
    "channel": "copilot-host",
    "actor": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    },
    "governedAgentContext": {
      "agentId": "product-owner"
    },
    "kernelModel": {
      "invoked": false,
      "status": "exact",
      "invocationIds": []
    },
    "externalAiUse": {
      "value": "unknown",
      "status": "unavailable"
    },
    "source": {
      "kind": "in-place",
      "filename": "design.md",
      "mediaType": "text/markdown",
      "sha256": "59c0cee7232d94cbd590bd84cc77371e0b1affe7c7494c65b5b51f3a13944418",
      "bytes": 16578
    },
    "generation": 1,
    "publishedAt": "2026-08-22T13:58:21.936Z"
  },
  "sourceCommit": "148aa762ca0695ad7054d51f7e04f527cbcbbc23",
  "generationCommit": "c3c44c1d401b93e18fd2f204ed23343534078d79",
  "publicationCommit": "c3c44c1d401b93e18fd2f204ed23343534078d79",
  "configSha256": "3bdacb436bc709f61acdd7a587e3c64c3f1401d861ba0ae781de15bf96568b9b",
  "sourceSha256": "142becb19837037c336774addd45db0b908d38715d71a2782805c230756620ca",
  "template": {
    "path": "singularity/templates/feature/design.md",
    "sha256": "8b7455f464a7025efa92942c272a04e3c0a3ab2a4d3eb438703cc14e230bc856"
  },
  "inputs": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/inputs-design-gen1.json",
    "sha256": "2af40a1cdb9da68e27d18295c3aa92b88914efdec2d982984dd804346c8dd75d",
    "renderedSha256": "7a7740cd071c93f6d8af7403c1c1044e9bc18ab9fd0a53bf0911578498cc2908",
    "mode": "enforce"
  },
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/clarifications-design-gen1.json",
    "sha256": "f1de14f6b50350e0941cf65673bfc8842b89f5c80e98c2b406a69b3763625b60",
    "promptSha256": "33add69e0128a262be2caf38faf630c81b31f2fff515b096d4571a049da7baa8",
    "responses": 1,
    "markers": [],
    "recordedAt": "2026-08-22T13:58:12.469Z",
    "recordedBy": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    }
  },
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/ANU-STORY/telemetry/design-gen1.json",
      "sha256": "600dcf49584a50486d37ef31df61d95402a2fd25ba92b6d0e933a0bf15cacfe1",
      "status": "pending",
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
      "startedAt": "2026-08-22T13:58:21.936Z",
      "completedAt": "2026-08-22T13:58:21.936Z",
      "agent": "product-owner",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "design",
      "at": "2026-08-22T14:09:08.558Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011",
        "githubLookup": "resolved"
      },
      "agent": "architect",
      "authorityGroup": "architecture-reviewers",
      "identityAssurance": "configured-local",
      "channel": "terminal",
      "generation": 1,
      "artifactSha256": [
        {
          "path": "singularity/work-items/ANU-STORY/artifacts/design/design.md",
          "sha256": "20f40291a2021fa06ea3085d6fa28186c465a7d6670bbcfaba8457bad629932d"
        }
      ],
      "reviewPacketSha256": "c907a6e89f5904ed4ebc36c6ba0da073adbbfff93d51040996cd3d67b291fa2e",
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# ANU-STORY — Architecture and Design

## Context and constraints

The approved requirements define a reusable interest-calculation capability that accepts principal, rate, and period values, rejects invalid or negative input values, calculates simple interest deterministically, and produces a result that can be verified through example scenarios. The current repository is a minimal Spring Boot application with a placeholder entry point in Main and no existing interest domain model or validation layer. The design must stay within the existing Java/Spring Boot structure, avoid persistence and UI work, and remain additive to the current API surface.

## Proposed design

Implement the capability as a small domain service and an optional HTTP endpoint so the rule engine can evaluate interest scenarios without coupling the logic to the console entry point.

1. Add a domain-oriented service, `InterestCalculationService`, in the existing Java package structure. The service will own the calculation logic and validation rules for the initial release.
2. Define a request/response contract for the calculation flow:
   - `InterestRequest`: `principal`, `rate`, and `period`
   - `InterestResult`: `principal`, `rate`, `period`, `interest`, and `resultType`
3. Apply validation in the service layer before calculation:
   - `principal` must be non-negative
   - `rate` must be non-negative
   - `period` must be non-negative
   - invalid input values must return a clear validation error rather than a silent fallback
4. Use the simple-interest formula `interest = principal × rate × period` for the initial release. For clarity and consistency, the design assumes the input rate is expressed as a decimal fraction (for example, `0.05` for `5%`).
5. Expose the capability through a thin Spring MVC endpoint, such as `POST /interest/calculate`, backed by the service. This keeps the implementation testable and preserves compatibility with the existing Spring Boot web stack.
6. Keep the implementation isolated from persistence, background jobs, and UI concerns so it remains aligned with the approved scope.

The design also records two stable identifiers for the implementation follow-up:
- `SPEC-001` — Implement the interest calculation service, validation rules, and endpoint contract.
- `SPEC-002` — Add example-driven tests that verify valid calculations and invalid input handling.

## Security, observability, migration, and rollback

Security concerns are limited because the capability does not handle secrets, user accounts, or external integrations. The main controls are strict input validation, explicit error responses for invalid data, and no persistence or side effects in the calculation path. Observability can be added through structured request logs and response metadata without changing the public contract. Migration and rollback are low risk because the design is additive: the new endpoint and service can be introduced without changing existing routes or stored data. If issues are discovered after release, the endpoint can be disabled or reverted independently from the rest of the application.

## Alternatives and decisions

Alternative 1: Place the calculation logic directly in the existing `Main` class. This was rejected because it would couple the feature to the console entry point and make testing and reuse harder.

Alternative 2: Add a dedicated service plus a thin controller. This was selected because it keeps the logic domain-focused, testable, and aligned with the Spring Boot architecture already present in the repository.

Decision record:
- `AC-001` is satisfied by the service-level validation and calculation flow described above.
- The initial release will support simple interest only, with rate input expressed as a decimal fraction to avoid ambiguity.
- The implementation should remain additive and not introduce persistence or UI work.

<!-- approved source inputs:start -->

# Approved phase inputs

## Approved phase input: requirements

<!-- source=artifacts/requirements/requirements.md sha256=0e042063c4f8d950b74a69c7d670bd38876c68a1fbc16f4d2d5dfcf911426de2 status=captured projection=full -->

<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "requirements",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": "product-owner",
  "authorship": {
    "schemaVersion": 1,
    "producer": "governed-agent",
    "channel": "copilot-host",
    "actor": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    },
    "governedAgentContext": {
      "agentId": "product-owner"
    },
    "kernelModel": {
      "invoked": false,
      "status": "exact",
      "invocationIds": []
    },
    "externalAiUse": {
      "value": "unknown",
      "status": "unavailable"
    },
    "source": {
      "kind": "in-place",
      "filename": "requirements.md",
      "mediaType": "text/markdown",
      "sha256": "2ce85cbe1456c316a7c39c650c8ddb967e305a07ac861831c9f13d574fdc970b",
      "bytes": 7830
    },
    "generation": 1,
    "publishedAt": "2026-08-22T12:44:12.645Z"
  },
  "sourceCommit": "470d0c7d92e732fa1b51f3ce35f3df5e71c6d93a",
  "generationCommit": "91f7bef9e16861ae5d42e2701bba28c83dd00278",
  "publicationCommit": "91f7bef9e16861ae5d42e2701bba28c83dd00278",
  "configSha256": "3bdacb436bc709f61acdd7a587e3c64c3f1401d861ba0ae781de15bf96568b9b",
  "sourceSha256": "142becb19837037c336774addd45db0b908d38715d71a2782805c230756620ca",
  "template": {
    "path": "singularity/templates/feature/requirements.md",
    "sha256": "32016db8ed6fadd6596e7dc702647cff95cdee1a203b38395d7ba5626dd8134e"
  },
  "inputs": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/inputs-requirements-gen1.json",
    "sha256": "8e51697c0211270485f515e011ae4b038ee76fd19b78676130f87768c654dc50",
    "renderedSha256": "4262a420498bce7cf0b193afab32653bff2031bd68b6d93178b787fa9fa9267d",
    "mode": "enforce"
  },
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/clarifications-requirements-gen1.json",
    "sha256": "35d3512918ebc252869713b3ee80c76d9d93715fa75982800d4189e2757eb9bc",
    "promptSha256": "b36631aebe96ef2ff13cf4294362302dfc8b232ced801581e974403aadbd2669",
    "responses": 1,
    "markers": [],
    "recordedAt": "2026-08-22T12:44:07.362Z",
    "recordedBy": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    }
  },
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/ANU-STORY/telemetry/requirements-gen1.json",
      "sha256": "c2fa36259cf8acaffe8aff72a84b36e2f49f8486ee8f07bec611731ed6a22931",
      "status": "pending",
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
      "startedAt": "2026-08-22T12:44:12.645Z",
      "completedAt": "2026-08-22T12:44:12.645Z",
      "agent": "product-owner",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "requirements",
      "at": "2026-08-22T12:54:44.379Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011",
        "githubLookup": "resolved"
      },
      "agent": "product-owner",
      "authorityGroup": "product-approvers",
      "identityAssurance": "configured-local",
      "channel": "terminal",
      "generation": 1,
      "artifactSha256": [
        {
          "path": "singularity/work-items/ANU-STORY/artifacts/requirements/requirements.md",
          "sha256": "7f50fd9bd464c525e1a16f93dae6eeea92e3c8fee4d27125385cd544ec7d0bf3"
        }
      ],
      "reviewPacketSha256": "9e2ab4eba5bc90e8fba62eb77c1f148f70ea80477b97c2a429c6f38684acaa96",
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# ANU-STORY — Feature Requirements

## Problem and outcome

The requested capability enables financial rules to calculate simple interest from a principal value, an annual rate, and a period so that the rule engine can support interest-based decision rules in a consistent way. The measurable outcome is a deterministic interest amount that can be validated with example inputs. [ANU-STORY:REQ-001]

## Scope

The implementation includes a reusable interest-calculation path that accepts principal, rate, and period values, validates inputs, and returns a normalized interest result. It explicitly excludes integrations with external billing systems, persistence changes, or user-interface work. [ANU-STORY:CON-001]

## Acceptance criteria

The completed behavior MUST accept principal, rate, and period values; MUST reject invalid or negative inputs with a clear validation error; MUST calculate simple interest using a documented formula; and MUST return a result that can be verified with example scenarios. [ANU-STORY:AC-001]

## Dependencies, risks, and open questions

Dependencies include the existing rule-engine expression and numeric handling paths. Risks include ambiguous rate semantics and inconsistent handling of period units. Open questions include whether the initial release should support only simple interest or also compound interest, and whether rate input should be expressed as a decimal or a percentage. [ANU-STORY:DE-001]

<!-- approved source inputs:start -->

# Approved phase inputs

## Approved phase input: intake

<!-- source=artifacts/intake/intake.md sha256=cc4f2e89a17fe5e84fdcae5c818d79dc72e60cbf0d9917cb9008915e1369f0ae status=captured projection=full -->

<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "intake",
  "generation": 1,
  "status": "approved",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": "product-owner",
  "authorship": {
    "schemaVersion": 1,
    "producer": "governed-agent",
    "channel": "copilot-host",
    "actor": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    },
    "governedAgentContext": {
      "agentId": "product-owner"
    },
    "kernelModel": {
      "invoked": false,
      "status": "exact",
      "invocationIds": []
    },
    "externalAiUse": {
      "value": "unknown",
      "status": "unavailable"
    },
    "source": {
      "kind": "in-place",
      "filename": "intake.md",
      "mediaType": "text/markdown",
      "sha256": "05c86408b0608f471d05809422ffbac68b025275fe93641e09d6216fed354f68",
      "bytes": 1247
    },
    "generation": 1,
    "publishedAt": "2026-08-22T12:36:25.942Z"
  },
  "sourceCommit": "97026e5d0e6fb4e53fa68d8ed071ffa76beedabb",
  "generationCommit": "759609df8cdb1acd3136f5c4762ab28584c31d2e",
  "publicationCommit": "759609df8cdb1acd3136f5c4762ab28584c31d2e",
  "configSha256": "3bdacb436bc709f61acdd7a587e3c64c3f1401d861ba0ae781de15bf96568b9b",
  "sourceSha256": "142becb19837037c336774addd45db0b908d38715d71a2782805c230756620ca",
  "template": {
    "path": "singularity/templates/feature/intake.md",
    "sha256": "eb53814f46f12ea3d93d1629164bd7ff22a3a54feceff7f7dd55670caeb5dbab"
  },
  "inputs": null,
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": {
    "generation": 1,
    "path": "singularity/work-items/ANU-STORY/context/clarifications-intake-gen1.json",
    "sha256": "fc66b446cb78fbd6491ac7d861c8297789620bc7a63620f3e84cb7a4d347c7f8",
    "promptSha256": "f42994d4ca1939d0b5d380b56e45e493b0cd4f199264917f6b2983fb70ceeca2",
    "responses": 1,
    "markers": [],
    "recordedAt": "2026-08-22T12:36:20.563Z",
    "recordedBy": {
      "name": "Ashok Raj",
      "email": "88361104+ashokraj2011@users.noreply.github.com",
      "login": "ashokraj2011",
      "githubLookup": "resolved"
    }
  },
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/ANU-STORY/telemetry/intake-gen1.json",
      "sha256": "c057a1781f32f4bfb8be24a5d90a80dd2789263702526bcf3081ed8560aba15a",
      "status": "pending",
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
      "startedAt": "2026-08-22T12:36:25.942Z",
      "completedAt": "2026-08-22T12:36:25.942Z",
      "agent": "product-owner",
      "generation": 1
    }
  ],
  "sequenceOverrides": [],
  "approvals": [
    {
      "decision": "approved",
      "phase": "intake",
      "at": "2026-08-22T12:38:41.817Z",
      "actor": {
        "name": "Ashok Raj",
        "email": "88361104+ashokraj2011@users.noreply.github.com",
        "login": "ashokraj2011",
        "githubLookup": "resolved"
      },
      "agent": "product-owner",
      "authorityGroup": "product-approvers",
      "identityAssurance": "configured-local",
      "channel": "copilot-selection-receipt",
      "generation": 1,
      "artifactSha256": [
        {
          "path": "singularity/work-items/ANU-STORY/artifacts/intake/intake.md",
          "sha256": "b37a8c0c3684410f4a8223e0612e63fc94fcd4668651316253dc15a300a4e227"
        }
      ],
      "reviewPacketSha256": "c1a371f2724c59f520a3fa747ce230b006c5d31b24220eb13150989137ba1d6e",
      "actionContext": {
        "phase": "intake",
        "label": "Intake",
        "generation": 1,
        "submittedAt": "2026-08-22T12:37:18.083Z",
        "artifacts": [
          {
            "path": "singularity/work-items/ANU-STORY/artifacts/intake/intake.md",
            "sha256": "b37a8c0c3684410f4a8223e0612e63fc94fcd4668651316253dc15a300a4e227"
          }
        ],
        "agentBriefs": [],
        "reviewPacketSha256": "c1a371f2724c59f520a3fa747ce230b006c5d31b24220eb13150989137ba1d6e",
        "submittedSourceCommit": "759609df8cdb1acd3136f5c4762ab28584c31d2e",
        "planId": "e6aeea3fb993c621dc02d875"
      },
      "selfApproval": true
    }
  ],
  "selfApproval": true,
  "conformanceTree": null
}
-->

# ANU-STORY — Feature Intake

## User and outcome

Business users need a reliable way to calculate interest amounts from a principal value, a rate, and a period so that the rule engine can support financial decision rules in a consistent way. The success measure is a clear, repeatable calculation path that can be validated with example inputs and produces the expected interest result.

## Proposed capability

Add an interest-calculation capability to the rule engine workflow so that a rule or request can evaluate interest-related scenarios without bespoke implementation work. The capability should be described as part of the product intake so the implementation team can design and build it against a shared understanding of the user need.

## Scope, constraints, and stakeholders

The scope is limited to intake and planning for the interest-calculation feature; implementation details should be deferred until the requirements and design phases. Constraints include keeping the change aligned with the existing rule-engine API and avoiding assumptions about external billing or persistence systems. Stakeholders include the product owner, the engineering team, and any downstream users who will rely on the new calculation capability.

<!-- approved source inputs:end -->

<!-- approved source inputs:end -->

<!-- approved source inputs:end -->

<!-- singularity-flow:inputs:end -->
