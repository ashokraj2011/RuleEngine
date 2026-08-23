> **Grounding** · RuleEngine @ `5268d8abf4c696e8ff5bdeb9eb956bede7c75cc8` · view: `security` · tier: `full`
> **Generated** 22 August 2026 (2026-08-22T14:44:16.846Z) · depth: `deep` · builder `2.0`
> **Authoritative for:** file locations, entry points, commands, structural relationships as of the commit above.
> **Not authoritative for:** current file contents. If this document conflicts with code you have read, trust the code and say so explicitly in your output.
> **Unknowns are marked.** Do not resolve them by inference. If the repository has changed since the date above, treat locations as hints, not facts.

## TL;DR {#sec.tldr}
The repository exposes a public, unauthenticated HTTP endpoint for evaluating arbitrary rule trees. The service accepts user-controlled `data` and `rule` input, compiles regex patterns from the incoming rule, and performs dotted-path lookup against a map structure without visible size limits or allow-listing. The biggest security concerns are unauthenticated exposure, denial-of-service risk from regex and large payloads, and weakly specified error behavior. The current tests cover nominal behavior but not security abuse cases.

## Facts {#sec.facts}
```yaml
entrypoints:
  - { path: src/main/java/org/example/api/RuleEngineController.java, line: 23, kind: http }
  - { path: src/main/java/org/example/rules/RuleEngineService.java, line: 115, kind: regex }
controls: [validation, exception-mapping]
missing_controls:
  - { kind: authentication, status: "not visible" }
  - { kind: rate-limiting, status: "not visible" }
  - { kind: payload-size-limit, status: "not visible" }
```

## Attack surface {#sec.surface}
The main attack surface is the public endpoint `POST /api/v1/rule-engine/evaluate`. It is exposed without any authentication or authorization layer in the visible code. The request contains two untrusted fields: `data` and `rule`, both accepted as arbitrary JSON structures. Evidence: [e-sec-surface], [e-sec-auth].

## Authentication and authorization {#sec.authz}
No Spring Security starter or auth layer is declared in `pom.xml`, and the controller does not perform user checks. The repository therefore does not show a visible authorization boundary around rule evaluation. This is important because the endpoint effectively becomes a public evaluator.

## Input handling and execution {#sec.input}
The service evaluates nested rule trees and condition operators from the request. `regex` uses `Pattern.compile(...)` with a pattern supplied by the request rule. The service also traverses dotted field paths through nested maps and lacks an apparent payload depth or field allow-list guard. These characteristics make the endpoint susceptible to resource exhaustion from crafted input.

## Trust boundaries and secret handling {#sec.trust}
The visible trust boundary is the HTTP request boundary. There is no visible persistence, secret loading, or outbound integration layer in the inspected code; no secrets are present in the source snapshot. The main concern is processing untrusted input rather than secret exposure.

## Security tests and gaps {#sec.tests}
The repository includes controller and service tests for successful evaluation paths, but no visible tests cover malicious regex patterns, oversized payloads, deep nesting, or invalid rule structures that could trigger errors or excessive work. Evidence: [e-sec-tests].

## Where to start {#sec.start}
For a security review, start with `RuleEngineController`, `EvaluateRequest`, `RuleEngineService`, and `GlobalExceptionHandler`. If you need to harden the service, the regex compilation and path-resolution paths are the first places to inspect.

## Questions this view does not answer {#sec.limits}
This view does not cover deployment firewalls, reverse proxies, cloud network controls, or external secrets management because those artifacts are not present in the repository snapshot.
