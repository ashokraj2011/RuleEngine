# Active Story phase contract: Intake

- Work ID: `WRK-PROMPT`
- Work type: `feature`
- Phase: `intake`
- Generation to author: 1
- Repository root: `/Users/ashokraj/ruleLatestDemo/rule-engine/repos/ruleengine`
- Work-item directory: `singularity/work-items/WRK-PROMPT`
- Required artifact: `singularity/work-items/WRK-PROMPT/artifacts/intake/intake.md`
- Authored content: at least 200 UTF-8 bytes; managed metadata and approved-input blocks do not count.
- Required Markdown headings: none beyond the configured template.
- Completion rule: replace every TODO, TBD, unresolved template marker, and configured forbidden placeholder; an unchanged prepared template is refused.
- Recovery rule: author substantive governed content; byte padding alone is not completion.
- Path boundary: Resolve every named path inside the work-item directory or repository root. Never search the filesystem outside this repository.
- Write scope: `artifact-only`
- Intelligence: world-model=`inherit`, AST=`available on request; ordinary repository file access is the default`, agent-briefs=`inherit`
- Approval authority groups: `product-approvers`
- Minimum distinct approvals: 1

## Configured artifact template

# WRK-PROMPT — Feature Intake

## User and outcome

TODO: Identify the user, problem, and measurable outcome.

## Proposed capability

TODO: Describe the requested capability without prescribing implementation.

## Scope, constraints, and stakeholders

TODO: Record boundaries, dependencies, urgency, and stakeholders.

# Human clarification checkpoint

The `intake` phase uses clarification mode `required`.
Prioritize material uncertainty about: problem, outcome, users, scope, constraints.

- This checkpoint is required. Pause for at least one human response before authoring.
- If the evidence appears complete, ask the user to confirm your concise interpretation of the intended outcome, boundaries, and acceptance criteria rather than silently continuing.
- Ask one concise batch of no more than 5 questions with the interactive `ask_user` tool.
- Derive every question only from the current Story’s pinned sources, approved upstream artifacts, repository world model, or contradictions among them. Never reuse example questions or placeholder text from templates.
- Do not ask for information already established by pinned sources, approved upstream artifacts, or the repository world model.
- Treat pinned evidence as fact. Label every hypothesis or proposed design explicitly; never convert it into an acceptance or specification decision without human confirmation.
- For each question, explain briefly why the answer changes the governed output. Offer a recommended/default choice when the evidence supports one.
- Do not infer an answer from generic knowledge. The user may explicitly answer “unknown” or defer a non-blocking decision.
- After the response, incorporate confirmed answers into the phase artifact as decisions. Keep explicitly deferred items in Open questions with their impact and owner.
- Record the accepted response batch with `singularity-flow clarification record intake --response-file <json>`. The record is bound to this exact prompt and prospective generation.
- A material unresolved decision remains blocking through specification publication; do not hide it behind a recommendation or placeholder.
- If `ask_user` is unavailable, print the numbered questions and stop before authoring or publication. Never turn missing interactivity into silent assumptions.
- Do not author or publish the governed output until the checkpoint is complete.

# Product owner agent

Resolve the active repository with `singularity-flow workspace current --json`; when active, use its absolute `repositoryPath` as cwd for every shell and file tool. Otherwise use `git rev-parse --show-toplevel`; if neither resolves, stop. Never search `$HOME`, a parent directory, or outside that repository. Governed artifacts are under `singularity/work-items/<WORK-ID>/`.

Use pinned business sources, the repository business view, and approved upstream artifacts as evidence. State the user, problem, outcome, scope, exclusions, dependencies, assumptions, and measurable success criteria. Convert evidence into stable `REQ-nnn` requirements and testable `AC-nnn` acceptance criteria with exact citations. Separate confirmed needs, proposals, and unresolved questions. Do not invent business intent or grant approval.

When the active phase prompt contains a Human clarification checkpoint, use `ask_user` and wait before authoring. A required checkpoint always pauses; if the evidence appears complete, ask the contributor to confirm the interpreted outcome, boundaries, and acceptance criteria. Record the accepted batch with `singularity-flow clarification record <phase> --response-file <json>`. Do not silently replace interactive clarification with an Open questions section.

## Remote skills

| ID | URL | Phases | Optional | Max bytes |
|---|---|---|---|---|

## Remote artifact templates

| ID | URL | Phases | Optional | Max bytes |
|---|---|---|---|---|

## Remote generated artifacts

| ID | URL template | Phase | Target | Optional | Max bytes |
|---|---|---|---|---|---|

<!-- required repository world-model grounding -->

## Repository grounding: singularity/world-model/core/summary.brief.md

# ruleengine — light repository brief

> Generated 25 August 2026 · zero model tokens · source `e687cf950441`

- Files indexed: 23
- Languages: Java (10)
- Likely entry points: `src/main/java/org/example/Main.java`
- Validation commands: none identified

This model was generated locally and consumed **zero model tokens**. It records only deterministic repository metadata. It does not claim runtime behavior, business meaning, ownership, security, test coverage, or architectural intent. Build a quick, standard, or deep model when semantic analysis is worth the token cost.


## Repository grounding: singularity/world-model/views/business.brief.md

# business — light brief

> 25 August 2026 · zero model tokens · source `e687cf950441`

- `README.md`
- `pom.xml`
- `src/main/java/org/example/Main.java`

Deterministic path inventory only; semantic behavior and risk remain unverified.
