<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "WRk-2020",
  "workType": "chore",
  "phase": "intake",
  "generation": 1,
  "status": "awaiting_approval",
  "generatedBy": {
    "name": "Ashok Raj",
    "email": "88361104+ashokraj2011@users.noreply.github.com",
    "login": "ashokraj2011",
    "githubLookup": "resolved"
  },
  "generatedAgent": null,
  "authorship": {
    "schemaVersion": 1,
    "producer": "human",
    "channel": "manual-in-place",
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
      "sha256": "7f02bbea0ff18adff3e3096d9e7cb544e326d1c65a14c7a814212514dc80b6f6",
      "bytes": 824
    },
    "generation": 1,
    "publishedAt": "2026-08-24T12:48:22.007Z"
  },
  "sourceCommit": "0a9b810353620129aacc8f95e736bc58616b7a14",
  "generationCommit": "aee819bb7cc957fb74c685b4062f35815ae564b4",
  "publicationCommit": "aee819bb7cc957fb74c685b4062f35815ae564b4",
  "configSha256": "eedf45d3dc2293fb9d7c9fcff51a0b99bc9704f352df44b8d34ac7d727e556e8",
  "sourceSha256": "ca1996d6033e835a0511ddf9ad433aaf360260dbcceef251d676d7589047449a",
  "template": {
    "path": "singularity/templates/chore/intake.md",
    "sha256": "6e84e6cee5c5c25c7bad11809f245126b646ad9e4c76503876bd77cfaf08112d"
  },
  "inputs": null,
  "designSources": {
    "sets": [],
    "approved": null
  },
  "remoteAgent": null,
  "clarification": null,
  "telemetry": [
    {
      "generation": 1,
      "path": "singularity/work-items/WRk-2020/telemetry/intake-gen1.json",
      "sha256": "a37ef10d0b1dcf738610b12b9dc507a52e83c80c6c63ead68e1ff6059095bad9",
      "status": "not-invoked",
      "models": [],
      "providerCost": null
    }
  ],
  "remoteOutputs": [],
  "usage": [],
  "sequenceOverrides": [],
  "approvals": [],
  "selfApproval": false,
  "conformanceTree": null
}
-->

# WRk-2020 — Chore Intake

## Request

Implement a new `startwith` operator for the rule engine. The operator should accept a string and a character, determine whether the string begins with that character, trim the input string before comparison, and perform the comparison in a case-insensitive manner.

## Scope and urgency

- Implement the operator in the rule engine expression evaluation flow.
- Keep the change scoped to the relevant rule engine logic and tests.
- Cover the intended behavior with tests for trimmed input, case-insensitive matching, and negative cases.
- Treat this as a contained maintenance change with no external integration or UI changes.

## Initial evidence

- Story: WRk-2020 — implement startwith
- Repository context: rule-engine source and test suite under the repository source tree.
