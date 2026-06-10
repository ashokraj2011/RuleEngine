# Security Risk Assessment — `has_all_of` Operator

**Feature:** `has_all_of` operator in `RuleEngineService`  
**Files changed:** `Operator.java`, `RuleEngineService.java`  
**Assessment date:** 2026-06-10  
**Assessor role:** Security

---

## 1. Summary

The `has_all_of` operator normalises two lists (ASCII filter, trim, lowercase, dedup) and compares their resulting sets for equality. The logic is straightforward and contains no injection vectors. However, several risks are introduced or surfaced by this change.

---

## 2. Risk Register

### 2.1 Denial-of-Service via Unbounded List Size — **HIGH**

| Attribute | Detail |
|-----------|--------|
| **Location** | `RuleEngineService.normalizedSet()` (lines 146–160) |
| **Description** | There is no upper bound on the number of elements in either the `field` collection from caller data or the `value` array from the rule JSON. A single request can pass millions of elements, causing heap exhaustion, CPU saturation, or GC pressure. The char-by-char loop inside `normalizedSet` compounds CPU cost for long strings. |
| **Attack vector** | Unauthenticated HTTP POST to `/api/v1/rule-engine/evaluate` with `value: [<1M entries>]`. |
| **Likelihood** | High — endpoint is unauthenticated (see §2.5). |
| **Impact** | Service unavailability (DoS). |
| **Recommended mitigation** | Enforce a configurable maximum list size (e.g., 1 000 elements) and maximum string length per element (e.g., 1 000 chars) before calling `normalizedSet`. Throw `IllegalArgumentException` with a safe message if limits are exceeded. |

---

### 2.2 Unhandled `ClassCastException` — **MEDIUM**

| Attribute | Detail |
|-----------|--------|
| **Location** | `RuleEngineService.evalCondition()` line 140 |
| **Description** | `jsonToJava(valueNode)` returns `Object`; the code casts it to `List<?>` without a checked cast or try-catch. Although `valueNode.isArray()` is checked first so the cast should always succeed, the dependency on that ordering is fragile. A future refactor could break the guard, resulting in an unhandled `ClassCastException` that bypasses `GlobalExceptionHandler` (which only catches `IllegalArgumentException`) and returns a 500 with a full stack trace. |
| **Likelihood** | Low currently; medium after any refactor. |
| **Impact** | Internal stack traces exposed to callers; information disclosure. |
| **Recommended mitigation** | Replace the raw cast with an explicit `instanceof List<?>` check and throw a descriptive `IllegalArgumentException` on failure. |

```java
// Before
return normalizedSet((Collection<?>) left).equals(normalizedSet((List<?>) jsonToJava(valueNode)));

// After
Object rightRaw = jsonToJava(valueNode);
if (!(rightRaw instanceof List<?>)) {
    throw new IllegalArgumentException("has_all_of value must resolve to a list");
}
return normalizedSet((Collection<?>) left).equals(normalizedSet((List<?>) rightRaw));
```

---

### 2.3 Error Message Information Disclosure — **MEDIUM**

| Attribute | Detail |
|-----------|--------|
| **Location** | `GlobalExceptionHandler` + all `IllegalArgumentException` messages in `evalCondition` |
| **Description** | Raw exception messages (e.g., `"has_all_of requires field to be a list"`, `"Unknown operator: …"`) are returned verbatim in HTTP 400 responses. This discloses internal operator names, field names, and type information to unauthenticated callers, aiding reconnaissance. |
| **Likelihood** | High — no authentication is required. |
| **Impact** | Low–medium; aids an attacker in crafting further requests. |
| **Recommended mitigation** | Log the full message server-side and return a generic error code/key to the client (e.g., `"error": "INVALID_RULE"` without the raw message). |

---

### 2.4 Silent Null-Element Discard Changes Comparison Semantics — **LOW**

| Attribute | Detail |
|-----------|--------|
| **Location** | `normalizedSet()` line 149 — `if (item == null) continue;` |
| **Description** | Null elements are silently dropped before set comparison. A list `["a", null, "b"]` is treated as `{"a","b"}`. While this is a defined normalisation, the behaviour is not surfaced to callers and could produce false-positive rule matches if callers expect nulls to be significant. This can lead to incorrect access-control decisions that depend on rule evaluation results. |
| **Likelihood** | Medium — callers are unlikely to be aware of this semantic. |
| **Impact** | Logic/authorisation bypass in downstream rule-based decisions. |
| **Recommended mitigation** | Document this behaviour explicitly in the API. Optionally, add a strict mode that treats null elements as an error. |

---

### 2.5 No Authentication or Authorisation on the Evaluate Endpoint — **HIGH** *(pre-existing, amplified)*

| Attribute | Detail |
|-----------|--------|
| **Location** | `RuleEngineController` — `/api/v1/rule-engine/evaluate` |
| **Description** | The endpoint is fully open. Any network-reachable client can submit arbitrary rules and data. The `has_all_of` operator does not introduce this, but it increases the attack surface by providing a new computationally expensive path (see §2.1). |
| **Likelihood** | High in any internet-facing deployment. |
| **Impact** | Unauthorised rule evaluation; DoS amplification. |
| **Recommended mitigation** | Add Spring Security with at minimum API-key or JWT authentication. Apply rate limiting (e.g., Spring Cloud Gateway, Bucket4j). |

---

### 2.6 ReDoS on `regex` Operator — **HIGH** *(pre-existing, noted for completeness)*

| Attribute | Detail |
|-----------|--------|
| **Location** | `RuleEngineService.evalCondition()` — `case regex` |
| **Description** | `Pattern.compile(pattern).matcher(…).find()` is called with a caller-supplied pattern and no timeout. A pathological regex causes catastrophic backtracking and blocks the JVM thread indefinitely. Not introduced by this PR but relevant to the same endpoint. |
| **Recommended mitigation** | Compile patterns with a timeout via `Pattern.compile` + a `ThreadLocal` with `interrupt`, or validate patterns against a safe allow-list/complexity limit. |

---

## 3. Secrets & Dependency Review

| Area | Finding |
|------|---------|
| **Secrets** | No credentials, tokens, or keys were introduced. ✅ |
| **Dependencies** | No new dependencies added. Spring Boot 3.2.5 + Jackson are used. Spring Boot 3.2.5 reached end-of-OSS support; upgrade to 3.3.x or 3.4.x is recommended to receive security patches. ⚠️ |
| **Transitive CVEs** | Run `mvn dependency-check:check` (OWASP) or equivalent to scan transitive deps for known CVEs before release. |

---

## 4. Risk Summary Matrix

| ID  | Risk | Severity | Status |
|-----|------|----------|--------|
| 2.1 | DoS via unbounded list | **HIGH** | ❌ Open |
| 2.2 | Unsafe cast / stack trace disclosure | **MEDIUM** | ❌ Open |
| 2.3 | Error message info disclosure | **MEDIUM** | ❌ Open |
| 2.4 | Silent null discard — logic bypass | **LOW** | ❌ Open |
| 2.5 | No authn/authz on endpoint (pre-existing) | **HIGH** | ❌ Open |
| 2.6 | ReDoS on regex operator (pre-existing) | **HIGH** | ❌ Open |

---

## 5. Recommendation

**Do not deploy to production** until items 2.1 and 2.2 are resolved. Items 2.3 and 2.4 should be addressed in the same sprint. Items 2.5 and 2.6 are pre-existing and must have a tracked remediation story before any production exposure.
