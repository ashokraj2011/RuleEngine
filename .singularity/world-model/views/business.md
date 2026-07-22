# Business View — rule-engine-api

Audience: product managers, business analysts, domain experts, business-facing
agents. Assumes `core/summary.md` is already loaded. See `evidence/evidence.jsonl`
for source-verified claims (IDs referenced in parentheses).

## 1. Capability map

| Capability | Description | Status |
|---|---|---|
| Rule evaluation | Given a JSON `data` payload and a JSON `rule` expression, return a single boolean decision. | observed, high (`ev-business-capability`) |
| Boolean composition | Combine multiple conditions with `all` (AND), `any` (OR), `not` (negation), including nesting. | observed, high (`ev-service-dispatch`) |
| Field-level comparisons | Compare a named field in `data` against a supplied value using one of 14 operators. | observed, high (`ev-service-condition`) |
| Existence/null checks | Determine whether a field is present/absent or null/non-null. | observed, high (`ev-service-condition`) |

This is a **decision-as-a-service** capability: the repository does not store,
manage, or version rules or data — every call is stateless (`ev-repo-purpose`).

## 2. Actors and personas visible in the code

- **Rule author / caller system**: any HTTP client that constructs a `data` +
  `rule` JSON payload and calls `POST /api/v1/rule-engine/evaluate`. No
  authentication, user identity, or role model is present in the code
  (observed — no security config found in this quick pass; see unknowns).
- **No end-user-facing UI, notification, or downstream persistence actor is
  observed.** The engine's only "customer" is the calling system/integration.

## 3. Business workflows

1. **Single-condition decision**: caller submits one `{field, op, value}`
   condition (e.g., "is age ≥ 21?") and receives `{result: true|false}`.
2. **Composite policy evaluation**: caller submits a nested tree of
   `all`/`any`/`not` groups representing a business policy (e.g., eligibility,
   discount, or compliance rule) and receives one aggregate boolean.
3. **Implicit top-level AND**: a bare JSON array of conditions is treated as
   "all must pass," useful for simple checklist-style policies.

These three shapes are the entire observable business workflow surface
(`ev-service-dispatch`, README.md:104-162).

## 4. Business entities and vocabulary

| Term | Business meaning |
|---|---|
| **Rule** | A codified business condition or policy expression. |
| **Data** | The business record/context being evaluated (e.g., a customer, order, or application record) — structure is caller-defined, not fixed by the engine. |
| **Operator** | The comparison semantics available to rule authors: `eq, ne, lt, lte, gt, gte` (comparison), `between, in` (range/membership), `contains, regex` (text/collection matching), `exists, not_exists, isNull, isNotNull` (data-quality checks). |
| **Result** | The single boolean business decision returned per call. |

No domain-specific business entities (e.g., "customer," "order," "invoice")
are modeled in code — `data` is a generic `Map<String,Object>`, so all
business semantics live entirely in the caller's choice of field names and
rule shape (`ev-service-dispatch`).

## 5. Business rules and policy locations

Business/policy logic is **entirely rule-author-defined at call time** — the
service itself hard-codes only the *mechanics* of evaluation, not any
specific business policy:

- Boolean composition and short-circuit semantics: `RuleEngineService.evalGroup`
  (`RuleEngineService.java:41-61`).
- Comparison/coercion policy (numeric → date/time → generic comparable →
  string fallback): `RuleEngineService.compare` (`ev-service-compare`).
- See `domains/rule-evaluation.md` for the full technical rule catalogue if a
  development-level view is later loaded.

## 6. User-visible failure behavior

- Malformed rule shapes (bad `between`/`in` value arrays, unknown `op`,
  non-object/array rule nodes) surface as **HTTP 400** with a JSON
  `{error, message}` body (`ev-exception-handler`).
- README documents **422** for request-level validation failures (null
  `data`/`rule`), but the code returns **400** for those too — a
  documentation/behavior mismatch a business stakeholder relying on the
  README's error-handling contract should be aware of (`ev-exception-handler`,
  conflict recorded in evidence).
- There is no partial-result or "unknown" outcome — every call resolves to
  `true`, `false`, or an error response.

## 7. Compliance or data-sensitivity indicators

None observed. The engine has no logging of `data` contents visible in this
quick pass, no PII-specific handling, no data retention, and no audit trail —
callers are responsible for what business data they submit. This has not
been deeply verified (quick-depth pass); treat as **unknown**, not confirmed
absent.

## 8. Business-impact map (for "implement tan math operator")

| Area | Impact |
|---|---|
| Rule authoring capability | Would **add** a new operator to the closed 14-operator vocabulary rule authors can use — currently no math/trigonometric function is expressible (`ev-operator-current-state`, `ev-no-math-operators-business-impact`). |
| Existing rules/policies | No existing operator behavior changes; this is additive, not modifying `eq/ne/lt/...` semantics — low risk of regression to current business rules **if implemented as a new enum case**. |
| API contract | Request/response JSON shape (`{data, rule}` → `{result}`) is unaffected; only the set of valid `op` string values grows. |
| Caller workarounds today | Any caller currently needing tangent-based logic must precompute it before calling the API — this task removes that workaround requirement. |

## 9. Unknown business assumptions

- What business use case motivates a `tan` operator (e.g., angle-based
  eligibility, geometry/engineering domain, sensor-data thresholding) — no
  requirements doc, ticket, or code comment states this.
- Expected semantics: should `tan` compare `tan(field)` to `value`, or is
  `tan` meant to transform `value` before comparison, and in what unit
  (radians vs degrees)? Not decidable from the repository alone.
- Whether authentication/authorization will ever gate who can submit rules
  (currently none exists) — relevant if math operators are seen as
  higher-privilege/compute-sensitive.

## 10. Suggested questions for domain owners

1. What real-world business condition needs a tangent computation, and on
   which field types (angles in degrees or radians)?
2. Should the result of `tan` be compared against `value` directly, or
   should there be a tolerance/precision rule (floating-point comparison)?
3. Should out-of-domain inputs (e.g., `tan` at asymptotic points) fail the
   condition, throw a 400, or return a defined error?
4. Are there other math operators anticipated after `tan` (e.g., `sin`,
   `cos`, `abs`) that should shape a general "math operator" design now
   rather than a one-off addition?

## Where to start

- Read `core/summary.md` for repository orientation.
- For the actual code change, load `views/development.md` (already generated)
  and `domains/rule-evaluation.md` — this business view intentionally does
  not cover implementation mechanics.
- For final confirmation of the current operator set, see evidence
  `ev-operator-current-state`.

## Questions this view does not answer

- How to implement the `tan` operator in `Operator.java` / `RuleEngineService`
  (see `views/development.md`).
- Test strategy or CI/CD implications (no `testing`/`release` view generated
  this run).
- Architectural or scalability implications of adding operators
  (no `architecture` view generated this run).
