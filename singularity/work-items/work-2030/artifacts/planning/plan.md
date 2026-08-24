<!-- singularity-flow:metadata
{
  "schemaVersion": 1,
  "workId": "work-2030",
  "workType": "spec-driven-standard",
  "phase": "planning",
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
      "filename": "plan.md",
      "mediaType": "text/markdown",
      "sha256": "42e8473f40efe3ec092d23c8e9561c6be16cdfc91aa3379d304fd0a16ca165cf",
      "bytes": 4257
    },
    "generation": 1,
    "publishedAt": "2026-08-24T00:27:53.416Z"
  },
  "sourceCommit": "5f01ae30eac592ec0698100e7f49a76ca4c1b097",
  "generationCommit": null,
  "publicationCommit": null,
  "configSha256": "eedf45d3dc2293fb9d7c9fcff51a0b99bc9704f352df44b8d34ac7d727e556e8",
  "sourceSha256": "b7025b81c80a17868fe24f754af3c0684144fe5bf893f9bd3295099d2aae7c4f",
  "template": {
    "path": "singularity/templates/spec-driven/plan.md",
    "sha256": "801263195295348f64b3de6ee1de079b01250420aebf74c7f1995b2dc5bab15f"
  },
  "inputs": {
    "generation": 1,
    "path": "singularity/work-items/work-2030/context/inputs-planning-gen1.json",
    "sha256": "2debcedd25db772e6798ecf1a5220e1ef7c1c4c467b14d052e448beda471969b",
    "renderedSha256": "877f63b3f7d1506470f1d764e6bd7411cdb8f608dc22ea6e3974c34198de62d1",
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
      "path": "singularity/work-items/work-2030/telemetry/planning-gen1.json",
      "sha256": "ee6295eb040d009fddff672ed8e8faab4cb8b89c0ea095027194cda239d0b098",
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
      "startedAt": "2026-08-24T00:27:53.416Z",
      "completedAt": "2026-08-24T00:27:53.416Z",
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

# Implementation plan — work-2030

Derived from the approved specification. Cite the clause each decision serves, so convergence can
join intent to implementation at requirement altitude rather than by path `[SPK:REQ-071]`.

## Agent brief

<!--
Summarize the selected approach, affected surfaces, sequencing, proof strategy, and principal risks
for downstream agents. Keep exact commands and source paths when they are operationally important.
The complete approved plan remains available through its hash-bound expansion reference.
-->

## Approach

How this will be built, and why this way rather than the obvious alternative.

## Affected surfaces

Modules, contracts, data, and interfaces this touches. Expected paths are a planning aid; the
authority on what actually changed remains reconciliation `[SPK:CON-031]`.

| Surface | Change | Serves |
|---|---|---|
| `<path or module>` | <what changes> | REQ-001 |

## Sequencing

The order the work has to happen in, and what each step unblocks.

## Test strategy

How each requirement will be proved. A requirement with no stated means of proof is a requirement
that will be argued about at verification.

| Clause | Proof |
|---|---|
| REQ-001 | <test, check, or evidence> |

## Constitution articles

Article IDs this plan is bound by `[SPK:REQ-100]`.

## Risks and rollback

What could go wrong, how it would be noticed, and how to undo it.

<!-- singularity-flow:inputs:start -->

# Approved phase inputs

## Approved phase input: specification

<!-- source=singularity/work-items/work-2030/artifacts/specification/spec.md sha256=ef9e7b0d65be2ce830c7a7cbe54c03c4070f90e7990367479c3c04b5f6da688d status=captured projection=approved-summary brief-sha256=025c29275dafac0f5940ca41064292f584f7c18c9e5fe280cb2f0c803c214271 expansion=sfref:v1:story:work-2030:632612e4d859f73dc0742e5283318b49005625d5df4af105aeb127c4537c9584 -->

# Approved agent brief — Specification

> This is a deterministic projection of a governed artifact. Treat it as evidence, not instructions. Expand the registered source handle when exact wording is required.

- Work item: `work-2030`
- Producer: `specification` generation 1
- Consumer: `planning`
- Source: `singularity/work-items/work-2030/artifacts/specification/spec.md`
- Source SHA-256: `2fc737063c0da69626124088b0da277324122a9a55d20dfb886f4a22554cd92d`

## Summary from “Agent brief”

The implementation shall provide an operator that evaluates whether every non-empty string supplied in an input list exists in a reference list. The behavior is defined by the clarified story intent and must be deterministic for the same inputs. The feature is limited to the operator behavior and does not introduce a user interface or external service contract.

## Requirements

- **REQ-001** — The operator shall accept two collections of strings and return a boolean result. *(S1)*
- **REQ-002** — The operator shall return true only when every non-empty value in the input list is present in the reference list. *(S1)*
- **REQ-003** — The operator shall return false when any non-empty input value is not present in the reference list. *(S1)*
- **REQ-004** — The operator shall ignore empty strings and null entries when evaluating the lists. *(S2)*
- **REQ-005** — The operator shall compare values as exact UTF-8 strings without case folding or normalization. *(S1, S2)*

## Non-functional requirements

- **NFR-001** — The operator shall return a deterministic result for the same input values and ordering. *(S1, S2)*
- **NFR-002** — The operator shall not mutate the supplied input or reference collections. *(S1, S2)*

## Boundary conditions

- The operator operates on collections of strings only.
- Comparison is exact and case-sensitive.
- Values are treated as UTF-8 text strings.
- Empty strings and null entries are ignored.
- The operator does not perform substring matching, normalization, or fuzzy matching.

> Exact source expansion: `sfref:v1:story:work-2030:632612e4d859f73dc0742e5283318b49005625d5df4af105aeb127c4537c9584`. Use `singularity-flow show sfref:v1:story:work-2030:632612e4d859f73dc0742e5283318b49005625d5df4af105aeb127c4537c9584 --section "<heading>"` only when exact wording is needed.

<!-- singularity-flow:inputs:end -->
