# Release & Rollback Plan — `has_all_of` Operator

**Work Item:** WRK-09196  
**Service:** `rule-engine-api`  
**Version:** `1.0.0-SNAPSHOT` → `1.1.0`  
**Branch:** `wi/WRK-09196`  
**Date:** 2026-06-10

---

## 1. Change Summary

Added the `has_all_of` list-equality operator to the Rule Engine.

**Behaviour:** Given a `field` (must be a list) and a `value` (must be a JSON array), the operator returns `true` if both sides contain the **same set of distinct values** after:
1. Filtering to printable ASCII characters (`0x20–0x7E`).
2. Trimming leading/trailing whitespace.
3. Case-folding to lowercase (ASCII).
4. De-duplicating (set comparison — order is irrelevant).

**Files changed:**
| File | Change |
|------|--------|
| `src/main/java/org/example/rules/Operator.java` | `has_all_of` enum constant added |
| `src/main/java/org/example/rules/RuleEngineService.java` | `has_all_of` case in `evalCondition()` + `normalizedSet()` helper |
| `src/test/java/org/example/rules/RuleEngineServiceTest.java` | Tests for the new operator |

---

## 2. Pre-Release Checklist

- [ ] Feature branch `wi/WRK-09196` rebased / merged onto `main`
- [ ] All unit tests pass: `mvn test`
- [ ] Integration / controller tests pass: `mvn verify`
- [ ] Code review approved
- [ ] Version bumped in `pom.xml` (`1.0.0-SNAPSHOT` → `1.1.0`)
- [ ] Artifact built and published: `mvn clean package -DskipTests`
- [ ] Docker image tagged `rule-engine-api:1.1.0` (if containerised)
- [ ] Staging deployment verified (smoke test below)
- [ ] Change-management ticket approved (if required)

---

## 3. Deployment Steps

### 3.1 Build
```bash
cd <repo-root>
mvn clean package -DskipTests
# Produces: target/rule-engine-api-1.1.0.jar
```

### 3.2 Deploy (JAR / systemd)
```bash
# Stop current service
sudo systemctl stop rule-engine-api

# Back up current binary
cp /opt/rule-engine/rule-engine-api.jar \
   /opt/rule-engine/rule-engine-api.jar.1.0.0.bak

# Deploy new binary
cp target/rule-engine-api-1.1.0.jar /opt/rule-engine/rule-engine-api.jar

# Start new service
sudo systemctl start rule-engine-api
sudo systemctl status rule-engine-api
```

### 3.3 Deploy (Docker / Kubernetes — if applicable)
```bash
# Build and push image
docker build -t rule-engine-api:1.1.0 .
docker push <registry>/rule-engine-api:1.1.0

# Roll out (Kubernetes)
kubectl set image deployment/rule-engine-api \
  rule-engine-api=<registry>/rule-engine-api:1.1.0 \
  --record

kubectl rollout status deployment/rule-engine-api
```

---

## 4. Smoke Test

Run immediately after each environment promotion.

```bash
BASE=http://localhost:8080

# Test 1 — matching sets (should return true)
curl -s -X POST "$BASE/api/rules/evaluate" \
  -H "Content-Type: application/json" \
  -d '{
    "data": {"tags": ["Apple", " banana ", "Cherry"]},
    "rule": {"field": "tags", "op": "has_all_of", "value": ["cherry", "BANANA", "apple"]}
  }' | grep '"result":true'

# Test 2 — non-matching sets (should return false)
curl -s -X POST "$BASE/api/rules/evaluate" \
  -H "Content-Type: application/json" \
  -d '{
    "data": {"tags": ["a", "b"]},
    "rule": {"field": "tags", "op": "has_all_of", "value": ["a", "b", "c"]}
  }' | grep '"result":false'

# Test 3 — dedup: duplicate entries collapse (should return true)
curl -s -X POST "$BASE/api/rules/evaluate" \
  -H "Content-Type: application/json" \
  -d '{
    "data": {"tags": ["x", "x", "y"]},
    "rule": {"field": "tags", "op": "has_all_of", "value": ["x", "y"]}
  }' | grep '"result":true'
```

All three `grep` commands must match.  A non-zero exit from any curl/grep chain signals a failed deployment — proceed to rollback immediately.

---

## 5. Rollback Plan

**Trigger:** Any smoke test failure, unexpected 5xx rate increase, or P1 alert within 30 minutes of deployment.

### 5.1 JAR / systemd rollback
```bash
sudo systemctl stop rule-engine-api

cp /opt/rule-engine/rule-engine-api.jar.1.0.0.bak \
   /opt/rule-engine/rule-engine-api.jar

sudo systemctl start rule-engine-api
sudo systemctl status rule-engine-api
```

### 5.2 Kubernetes rollback
```bash
kubectl rollout undo deployment/rule-engine-api
kubectl rollout status deployment/rule-engine-api
```

### 5.3 Verify rollback
Re-run smoke tests.  `has_all_of` requests will now return `400 Bad Request` (unknown operator) — this is the expected v1.0.0 behaviour and confirms the rollback succeeded.

### 5.4 Post-rollback actions
1. Open an incident ticket.
2. Preserve logs from the failed deployment: `sudo journalctl -u rule-engine-api > /tmp/rule-engine-api-failed.log`
3. Notify stakeholders.
4. Root-cause before re-attempting the release.

---

## 6. Ops Runbook

### 6.1 Service health check
```bash
curl -s http://localhost:8080/actuator/health | python3 -m json.tool
# Expect: {"status":"UP"}
```

### 6.2 Confirming the operator is live
```bash
curl -s -X POST http://localhost:8080/api/rules/evaluate \
  -H "Content-Type: application/json" \
  -d '{"data":{"x":["a","b"]},"rule":{"field":"x","op":"has_all_of","value":["b","a"]}}' \
  | python3 -m json.tool
# Expect: {"result": true}
```

### 6.3 What a bad `has_all_of` call looks like
| Mistake | HTTP status | Error message |
|---------|------------|---------------|
| `value` is not a JSON array | 400 | `has_all_of requires array value` |
| `field` resolves to a non-list | 400 | `has_all_of requires field to be a list` |
| `field` path missing from data | 400 | `has_all_of requires field to be a list` |

### 6.4 Log grep patterns
```bash
# Application errors related to has_all_of
grep -i "has_all_of" /var/log/rule-engine/application.log

# All rule-evaluation 400s in the last hour
journalctl -u rule-engine-api --since "1 hour ago" | grep "400"
```

### 6.5 Key metrics to watch (first 30 min post-deploy)
| Metric | Threshold | Action |
|--------|-----------|--------|
| HTTP 5xx rate | > 1 % | Rollback |
| P99 latency `/api/rules/evaluate` | > 500 ms | Investigate then rollback |
| JVM heap usage | > 85 % | Heap-dump, alert + monitor |
| Error log rate | > 10/min | Investigate |

### 6.6 Contacts
| Role | Name / Channel |
|------|---------------|
| Dev owner | `@dev-team` |
| On-call engineer | PagerDuty escalation policy `rule-engine` |
| Change manager | `#change-management` Slack channel |
