# Business View

Audience: product managers, business analysts, domain experts, business-facing agents.
Assumes `core/summary.md` and `core/model.json` are already loaded.

## Capability map
This repository provides exactly one business capability: **rule-based decisioning as a service**. A caller submits a JSON `data` payload and a JSON `rule`, and the service returns a boolean verdict. There is no UI, no persistence of past decisions, and no batch/reporting capability. (observed, high — ev-001, ev-004, ev-018)

Typical use cases implied by the grammar (eligibility checks, feature gating, segmentation, validation logic) are **inferred** from the operator set (`eq`, `between`, `in`, `regex`, `exists`, etc.) — no concrete business scenario or calling system is documented in the repository. (inferred, medium — ev-008)

## Actors, personas, external systems
- **Caller / integrating system**: any HTTP client that POSTs to `/api/v1/rule-engine/evaluate`. No caller identity, tenant, or role concept exists in the code. (observed, high — ev-004, ev-016)
- **No end-user-facing UI, no admin persona, no batch job persona** are present in the repository. (observed, high)

## Business workflows
Single workflow — **Rule Evaluation**:
1. Caller submits `{data, rule}` to `/evaluate`.
2. `comp-rules-engine` recursively evaluates rule groups (`all`/`any`/`not`) and leaf conditions against `data`.
3. Service returns `{result: true|false}`.
(observed, high — ev-004, ev-007)

There is no workflow for rule *authoring*, storage, versioning, approval, or auditing — rules are supplied fresh on every call and are not persisted. (observed, high — ev-018)

## Business entities and vocabulary
| Term | Meaning | Evidence |
|---|---|---|
| `data` | Arbitrary JSON object supplied by the caller as the subject of evaluation | ev-005 |
| `rule` | JSON tree combining group operators (`all`, `any`, `not`) and leaf conditions (`field`, `op`, `value`) | ev-007, ev-008 |
| `field` | Dot-separated path into `data` (e.g. `user.age`) | ev-010 |
| `op` | One of 14 supported operators | ev-008 |
| `result` | Boolean verdict returned to the caller | ev-004 |

No domain-specific business nouns (e.g., "order", "invoice", "policy") exist in the code — the engine is generic and domain-agnostic. (observed, high)

## Business rules and policy locations
All rule-evaluation policy is generic and structural, implemented in `RuleEngineService.evalCondition` (`src/main/java/org/example/rules/RuleEngineService.java:63-135`). There are **no hardcoded business policies** (e.g., specific age thresholds, pricing rules) in this repository — actual business rules are expected to be supplied by callers at request time, not embedded in code. (observed, high — ev-007, ev-008)

## User-visible failure behavior
- Malformed rule (bad operator, wrong value shape) → HTTP 400 with `{error: "BAD_REQUEST", message: ...}`. (observed, high — ev-006)
- Missing `data`/`rule` → HTTP 400 with `{error: "VALIDATION_ERROR", message: ...}` (README claims 422; code returns 400 — see conflict below). (observed, high — ev-006)
- No partial results or explanations are returned — the response is a single boolean with no reasoning trace.

## Compliance / data-sensitivity indicators
No field-level redaction, PII classification, encryption, or audit logging exists. Because `data` is arbitrary caller-supplied JSON, any sensitive data (PII, financial data) sent to this service is processed in memory only and not persisted — but it also is **not protected by authentication**, which may be a compliance concern depending on deployment context. (observed, high — ev-016, ev-018)

## Business-impact map
| Change area | Business impact if changed |
|---|---|
| Operator semantics (`eq`, `between`, etc.) | Directly changes decisioning outcomes for every caller — high impact |
| Group logic (`all`/`any`/`not` short-circuit) | Changes combinational logic outcomes — high impact |
| Response shape (`result` field) | Breaking change for all integrators — high impact |
| Error status codes | Affects how caller systems handle invalid requests — medium impact |

## Unknown business assumptions
- Who the real callers/integrators of this service are.
- Whether a specific business domain (e.g., credit approval, promotions) drives the operator set design.
- Whether the 400 vs 422 discrepancy (ev-006) is a documentation bug or a deliberate deviation business stakeholders rely on.

## Suggested questions for domain owners
1. Which business processes currently call (or plan to call) this rule engine, and what rules do they submit?
2. Is authentication/authorization intentionally deferred to an API gateway, or is this a gap?
3. Should the HTTP error contract match the README (422) or the current code (400)?
4. Are there compliance requirements (PII handling, audit trail) that this stateless design needs to satisfy?

## Where to start
Read `README.md` sections "API Overview" and "Quick Examples" for the externally observable contract, then this view's "Business workflows" and "Business rules" sections.

## Questions this view does not answer
- Internal code structure and symbols → see `views/development.md`.
- System boundaries, scalability, and coupling → see `views/architecture.md`.
- Test coverage of business scenarios → see `views/testing.md`.
- Authn/authz and data-handling risk detail → see `views/security.md`.
