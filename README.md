# Rule Engine API

A lightweight Spring Boot service that evaluates JSON rules against arbitrary input data.

The rule language supports:
- Group operators: `all` (AND), `any` (OR), `not`
- Condition operators: `eq`, `ne`, `lt`, `lte`, `gt`, `gte`, `between`, `in`, `contains`, `regex`, `exists`, `not_exists`

This document shows how to build, run, and use the API with practical examples.

---

## Build & Run

Requirements:
- Java 17+
- Maven 3.8+

Build:
```
mvn -q -DskipTests package
```

Run (from project root):
```
mvn spring-boot:run
```

By default, the application starts on `http://localhost:8080`.

---

## API Overview

- Base path: `/api/v1/rule-engine`
- Endpoint: `POST /evaluate`
- Request `Content-Type`: `application/json`
- Response `Content-Type`: `application/json`

### Request Schema
```
{
  "data": { "<string>": <any> },        // Required. Arbitrary JSON object used as input
  "rule": <Rule>                          // Required. Rule definition (object or array)
}
```

`<Rule>` can be one of:
- Group rule as object: `{ "all": [<Rule>, ...] }`, `{ "any": [<Rule>, ...] }`, `{ "not": <Rule> }`
- Group rule as array: `[ <Rule>, ... ]` (top‑level arrays default to `all`/AND)
- Condition rule as object: `{ "field": "a.b.c", "op": "gte", "value": 10 }`

Notes:
- `field` is a dot‑separated path into `data` (e.g., `user.age`, `address.city`).
- For `exists`/`not_exists`, the `field` is used and `value` is ignored.
- For `between`, `value` must be an array `[min, max]`.
- For `in`, `value` must be an array of possible values.
- For `regex`, `value` is a string pattern (Java regex).
- Numeric comparisons coerce compatible numbers; date/time comparisons support ISO‑8601 strings (e.g., `2024-01-30T12:00:00Z`).

### Response Schema
```
{
  "result": true | false
}
```

### Error Responses
- 400 Bad Request when the rule is structurally invalid (e.g., missing `op`, wrong `value` shape).
- 422 Unprocessable Entity when validation fails for request fields (e.g., `data` or `rule` is null).

Actual status codes may vary with configuration; see `GlobalExceptionHandler` for details.

---

## Quick Examples

### 1) Simple comparison
Request:
```
POST /api/v1/rule-engine/evaluate
Content-Type: application/json

{
  "data": { "age": 33 },
  "rule": { "field": "age", "op": "gte", "value": 21 }
}
```
Response:
```
{ "result": true }
```

Curl:
```
curl -s -X POST http://localhost:8080/api/v1/rule-engine/evaluate \
  -H 'Content-Type: application/json' \
  -d '{
        "data": { "age": 33 },
        "rule": { "field": "age", "op": "gte", "value": 21 }
      }'
```

### 2) Grouping with all/any/not
```
{
  "data": { "age": 33, "country": "US" },
  "rule": {
    "all": [
      { "field": "age", "op": "gte", "value": 21 },
      { "any": [
          { "field": "country", "op": "eq", "value": "US" },
          { "field": "country", "op": "eq", "value": "CA" }
        ]
      },
      { "not": { "field": "country", "op": "eq", "value": "MX" } }
    ]
  }
}
```

### 3) Between, in, contains, regex
- between numeric range:
```
{ "field": "price", "op": "between", "value": [10, 20] }
```
- in list of values:
```
{ "field": "status", "op": "in", "value": ["PENDING", "APPROVED"] }
```
- contains substring in a string:
```
{ "field": "title", "op": "contains", "value": "Pro" }
```
- contains element in a collection:
```
// data: { "tags": ["hot", "new"] }
{ "field": "tags", "op": "contains", "value": "new" }
```
- regex pattern (Java regex):
```
{ "field": "email", "op": "regex", "value": "^[^@]+@example\\.com$" }
```

### 4) Existence checks
```
{ "field": "user.address.city", "op": "exists" }
{ "field": "user.middleName", "op": "not_exists" }
```

### 5) Top‑level array (implicit AND)
```
{
  "data": { "age": 25, "active": true },
  "rule": [
    { "field": "age", "op": "gte", "value": 21 },
    { "field": "active", "op": "eq", "value": true }
  ]
}
```

---

## Full Operator Reference

- Comparison: `eq`, `ne`, `lt`, `lte`, `gt`, `gte`
- Collection/String: `contains`, `in`
- Pattern: `regex`
- Range: `between` (must supply `[min, max]`)
- Existence: `exists`, `not_exists`

Type coercion rules (summary of `RuleEngineService.compare`):
- Numbers are compared as `BigDecimal` if both sides numeric.
- ISO‑8601 date/time strings are compared as instants if both sides parse.
- Otherwise falls back to Java `Comparable` or string comparison.

---

## Java Client Example

```
ObjectMapper mapper = new ObjectMapper();

Map<String, Object> data = Map.of("age", 33, "country", "US");
Map<String, Object> rule = Map.of(
    "all", List.of(
        Map.of("field", "age", "op", "gte", "value", 21),
        Map.of("field", "country", "op", "in", "value", List.of("US", "CA"))
    )
);

Map<String, Object> payload = new HashMap<>();
payload.put("data", data);
payload.put("rule", rule);

HttpRequest req = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:8080/api/v1/rule-engine/evaluate"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
    .build();

HttpClient client = HttpClient.newHttpClient();
HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
System.out.println(res.statusCode());      // 200
System.out.println(res.body());            // {"result":true}
```

---

## Testing

Run tests:
```
mvn test
```

Example coverage: see `src/test/java/org/example/api/RuleEngineControllerTest.java` which posts a simple rule and expects `result=true`.

---

## Troubleshooting

- 400 Unknown operator: ensure `op` is one of the supported values listed above.
- 400 Wrong shape for `between` or `in`: supply arrays as documented.
- 422 Validation errors: ensure `data` and `rule` are present and not null.
- Debug rule logic by posting minimal rules and expanding step by step using `all`/`any`.
