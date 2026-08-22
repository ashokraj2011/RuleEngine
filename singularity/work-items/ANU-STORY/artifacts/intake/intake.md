<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "ANU-STORY",
  "workType": "feature",
  "phase": "intake",
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
      "filename": "intake.md",
      "mediaType": "text/markdown",
      "sha256": "05c86408b0608f471d05809422ffbac68b025275fe93641e09d6216fed354f68",
      "bytes": 1247
    },
    "generation": 1,
    "publishedAt": "2026-08-22T12:36:25.942Z"
  },
  "sourceCommit": "97026e5d0e6fb4e53fa68d8ed071ffa76beedabb",
  "generationCommit": null,
  "publicationCommit": null,
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
  "approvals": [],
  "selfApproval": false,
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
