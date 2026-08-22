> **Grounding** · RuleEngine @ `5268d8abf4c696e8ff5bdeb9eb956bede7c75cc8` · view: `development` · tier: `full`
> **Generated** 22 August 2026 (2026-08-22T14:44:16.846Z) · depth: `deep` · builder `2.0`
> **Authoritative for:** file locations, entry points, commands, structural relationships as of the commit above.
> **Not authoritative for:** current file contents. If this document conflicts with code you have read, trust the code and say so explicitly in your output.
> **Unknowns are marked.** Do not resolve them by inference. If the repository has changed since the date above, treat locations as hints, not facts.

## TL;DR {#dev.tldr}
For implementation work, start in `RuleEngineService` and the controller layer. The main change surface is the rule evaluator and its operator semantics, with request validation in the API boundary and tests in the corresponding test packages. The likely development loop is edit Java source, run `mvn -q test`, then exercise the endpoint locally with `mvn spring-boot:run`. The most important convention is that rule structure and operator shape are enforced explicitly in the service; malformed rules throw `IllegalArgumentException` and are mapped to a 400 response.

## Facts {#dev.facts}
```yaml
components: [api-boundary, rule-engine-service, tests]
entrypoints:
  - { id: spring-boot-main, path: src/main/java/org/example/RuleEngineApplication.java, line: 6, invocation: "mvn spring-boot:run" }
  - { id: evaluate-endpoint, path: src/main/java/org/example/api/RuleEngineController.java, line: 23, invocation: "POST /api/v1/rule-engine/evaluate" }
important_symbols:
  - { name: RuleEngineService.evaluate, path: src/main/java/org/example/rules/RuleEngineService.java, line: 17, role: "primary entry point" }
  - { name: RuleEngineService.evalCondition, path: src/main/java/org/example/rules/RuleEngineService.java, line: 63, role: "operator dispatch" }
commands:
  - { command: "mvn -q test", purpose: "execute test suite", source: "README.md:247-252" }
```

## Developer entry points {#dev.entrypoints}
The practical starting points are `RuleEngineApplication` for startup, `RuleEngineController` for the HTTP boundary, `RuleEngineService` for core behavior, and the two test classes for expected usage. For a feature change, begin in `RuleEngineService` unless the change is purely about validation or the external JSON contract.

## Source tree map {#dev.tree}
- `src/main/java/org/example/api/` — controller, DTOs, and exception handling.
- `src/main/java/org/example/rules/` — evaluator service and supported operators.
- `src/test/java/org/example/api/` — controller-level integration test.
- `src/test/java/org/example/rules/` — service-level unit-style tests.
The `src/main/java/org/example/Main.java` class appears to be leftover scaffolding rather than the application entry point.

## Important modules and symbols {#dev.symbols}
- `RuleEngineService.evaluate(Map<String, Object> data, JsonNode rule)` is the public evaluator entry point.
- `evalGroup(...)` implements `all`/`any`/`not` group semantics and short-circuiting.
- `evalCondition(...)` dispatches to operator-specific logic such as `between`, `in`, `regex`, and `exists`.
- `resolvePath(...)` and `resolvePathInternal(...)` implement dotted-path lookup for nested maps.
- `compare(...)` handles numeric, temporal, and fallback comparison. Evidence: [e-dev-symbols], [e-dev-operators].

## Common implementation flows {#dev.flows}
Typical change paths:
1. Add or adjust operator behavior in `RuleEngineService.evalCondition(...)`.
2. Adjust request validation or the controller contract in the API package.
3. Add or update tests in the relevant test class.
4. Validate with `mvn -q test` and, if necessary, `mvn spring-boot:run` for manual API checks.

## Error handling and validation {#dev.errors}
The controller validates request bodies with `@Valid`, while the service throws `IllegalArgumentException` for unsupported or malformed rules. `GlobalExceptionHandler` converts these into JSON error bodies. The documented status expectation in the README and the implemented status code diverge, so verify behavior before relying on the contract.

## Validation commands {#dev.commands}
- `mvn -q test` — observed passing baseline in this environment.
- `mvn -q -DskipTests package` — build without tests.
- `mvn spring-boot:run` — manual endpoint exercise.

## Change-impact guide {#dev.impact}
Changes to operator semantics can affect both the controller response and the service tests. Changes to request DTOs affect API validation behavior and the README contract. Changes to path resolution affect nested-map access and can alter evaluation outcomes in subtle ways. Evidence: [e-dev-impact].

## Known implementation hotspots {#dev.hotspots}
The highest-risk implementation surface is `src/main/java/org/example/rules/RuleEngineService.java`, especially the operator dispatch, path resolve logic, and comparison helpers. The controller and DTOs are smaller but directly affect the contract and validation semantics.

## Where to start {#dev.start}
If you are implementing a new operator or changing behavior, start in `RuleEngineService`; if you are changing the HTTP contract or error handling, start in `src/main/java/org/example/api/`.

## Questions this view does not answer {#dev.limits}
This view does not replace the architecture or security view. It also does not cover deployment, persistence, or wider product behavior beyond the Java implementation that is currently visible.
