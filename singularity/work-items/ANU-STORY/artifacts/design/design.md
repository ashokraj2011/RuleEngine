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

<!-- singularity-flow:inputs:start -->

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

<!-- singularity-flow:inputs:end -->
