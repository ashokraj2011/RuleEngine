<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "requirements",
  "generation": 1,
  "status": "in_progress",
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
  "generationCommit": null,
  "publicationCommit": null,
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
  "approvals": [],
  "selfApproval": false,
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

<!-- singularity-flow:inputs:start -->

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

<!-- singularity-flow:inputs:end -->
