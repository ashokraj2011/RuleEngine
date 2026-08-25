<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "WRK-PROMPT",
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
      "sha256": "29e689177f31dbdc1c80fb966bced29b0871a175d99f50cf5fec1daba9af0ea3",
      "bytes": 1121
    },
    "generation": 1,
    "publishedAt": "2026-08-25T02:46:15.497Z"
  },
  "sourceCommit": "5fa08f2e5c594a75ea536c144681a4a75fb0a7f3",
  "generationCommit": null,
  "publicationCommit": null,
  "configSha256": "1ea6f00db44b552caa91f3539c344c4bb7d2a14afe13fa08fa692c8526940064",
  "sourceSha256": "6f7e8852f87c47065c36c41a1f146e8623fe6f2bd7b7ec7d9337d214c46ba892",
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
    "path": "singularity/work-items/WRK-PROMPT/context/clarifications-intake-gen1.json",
    "sha256": "8beaac9e4d606a207ffd015ec93b50c60170f9f42e4bd99fad54d1c43e3d9862",
    "promptSha256": "45422fcfbc0118e86206f827b8e897b1ee1e3e01a15cb68e1a927e2bcf1b919b",
    "responses": 1,
    "markers": [],
    "recordedAt": "2026-08-25T02:46:11.353Z",
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
      "path": "singularity/work-items/WRK-PROMPT/telemetry/intake-gen1.json",
      "sha256": "d1dcf1df0bf234945414b06103a6ea333f4ea0383509c71ba3cf758da488c841",
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
      "startedAt": "2026-08-25T02:46:15.497Z",
      "completedAt": "2026-08-25T02:46:15.497Z",
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

# WRK-PROMPT — Feature Intake

## User and outcome

Business users of the rule engine need a way to express that an entity has none of a set of values. The current capability gap blocks rules that must enforce absence, which makes some business scenarios difficult or impossible to model. The measurable outcome is support for a working "has none of" operator in the rule engine with regression coverage for the new behavior.

## Proposed capability

Add support for a "has none of" operator in the rule engine so rules can express that an entity does not contain any of the specified values. The capability should fit the existing rule language and validation model without introducing a separate workflow or UX path.

## Scope, constraints, and stakeholders

In scope are parser and evaluator support for the new operator, validation of the new syntax, and test coverage for positive and negative cases. Out of scope are unrelated rule operators, broader DSL redesign, and non-rule-engine user experience changes. Stakeholders include the rule engine maintainers, QA, and the product owner who requested the feature.
