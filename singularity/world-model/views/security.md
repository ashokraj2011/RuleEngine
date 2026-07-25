# Security View

Audience: security reviewers, security-focused agents.
Assumes `core/summary.md` and `core/model.json` are already loaded.

## Authentication
**None present.** No `spring-boot-starter-security` dependency, no `SecurityFilterChain`, no API-key/JWT/basic-auth code anywhere in the repository. `/api/v1/rule-engine/evaluate` is reachable by any client that can route to the service. (observed, high — ev-016)

## Authorization
**None present.** There is no concept of user, tenant, role, or scope in any DTO, controller, or service. All callers have identical, unrestricted access to rule evaluation. (observed, high — ev-016)

## Trust boundaries
The only boundary is the network/HTTP listener itself (Tomcat, embedded via `spring-boot-starter-web`). Once a request reaches `RuleEngineController`, it is treated as fully trusted — there is no input-origin distinction. (observed, high — ev-004, ev-016)

## Secret names and loading mechanisms
None found. No `application.properties`/`application.yml`, no environment-variable references, no `@Value` injections, no credentials or API keys anywhere in `src/main`. (observed, high)

## Sensitive data
The `data` field in requests is arbitrary caller-supplied JSON and could contain PII or other sensitive fields (e.g., `user.email`, `user.phone` appear in the README's own examples). The service does not persist, log, or forward this data anywhere — it is held only in memory for the request's lifetime. (observed, high — ev-018; inferred, medium — no logging of request bodies observed, ev-019)

## Input validation
- Bean validation (`@NotNull`) ensures `data` and `rule` are present (`EvaluateRequest.java:9,12`). (observed, high — ev-005)
- No validation on `data` size, `rule` nesting depth, or overall request payload size — Spring Boot's default embedded-server limits (not overridden in this repo) would be the only backstop. (observed, high; inferred, medium re: no explicit overrides found)
- `field` path strings are split on `.` and used only as `Map` keys — no reflection, no dynamic class loading, no expression-language evaluation (e.g., no SpEL), so field paths do not enable code execution. (observed, high — ev-010)

## Output encoding
Response is a single `{result: boolean}` JSON object with no reflection of caller-supplied string data — no XSS/injection surface in the response body itself. (observed, high — ev-004, ev-005)

## Cryptographic usage
None. No hashing, encryption, TLS configuration, or key management code exists in this repository (TLS termination, if any, would be external to this codebase). (observed, high)

## Dependency-risk surface
Dependencies are limited to Spring Boot 3.2.5 starters (`web`, `validation`), `jackson-databind`, and test-scope `spring-boot-starter-test`. No CVE scan was performed in this session; version currency should be checked against current Spring Boot/Jackson advisories separately. (observed, high — ev-002; unknown — no vulnerability scan executed)

## Network exposure
Single HTTP endpoint, default Spring Boot embedded Tomcat on port 8080, no TLS/HTTPS configuration present in the repository (would need to be supplied by deployment infrastructure, which is out of scope/unknown here — no CI/CD or deployment manifests exist, ev-020). (observed, high — ev-020)

## File and command execution
None. No `Runtime.exec`, `ProcessBuilder`, file I/O, or reflection-based class loading appears anywhere in `src/main`. (observed, high)

## Audit logging
None. No logging of requests, responses, or evaluation outcomes exists (ev-019). This means there is no forensic trail if the service is misused. (observed, high — ev-019)

## Security tests
None. Neither test file (`RuleEngineServiceTest`, `RuleEngineControllerTest`) includes adversarial-input, authentication, or authorization test cases. (observed, high — ev-012, ev-013)

## Privileged operations
None exist — the service performs no filesystem, network, or OS-level privileged actions.

## Security assumptions and unknowns
- **Assumption (unverified)**: the service is expected to run behind an API gateway or network perimeter that provides authentication, TLS termination, and rate limiting. No repository evidence confirms or denies this. (unknown)
- **Risk — ReDoS**: the `regex` operator (`RuleEngineService.java:115-119`) compiles and executes caller-supplied Java regex patterns against caller-supplied string data with no complexity limit, timeout, or pattern allow-list. A crafted catastrophic-backtracking pattern could cause CPU exhaustion. (inferred, medium — ev-017)
- **Risk — resource exhaustion**: no limit on JSON rule nesting depth or `data` payload size; deeply nested `all`/`any`/`not` trees are evaluated recursively with no depth cap, risking stack exhaustion or high CPU on adversarial input. (inferred, medium — ev-007)
- **Unknown**: whether the doc/code mismatch on validation error status (422 documented vs. 400 actual, ev-006) has any downstream security-relevant effect on integrating clients' error handling.

## Where to start
Review `RuleEngineService.evalCondition`'s `regex` case (`rules/RuleEngineService.java:115-119`) and the complete absence of a security filter chain (`pom.xml`, no `spring-boot-starter-security`) as the two highest-priority findings.

## Questions this view does not answer
- Business sensitivity classification of specific fields → `views/business.md` (not currently documented there either — see business view's "Compliance" section, itself marked observed-absent).
- Architectural placement of a future auth layer → `views/architecture.md`.
- Recommended new security test cases in implementation terms → `views/development.md` and `views/testing.md`.
