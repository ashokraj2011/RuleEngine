package org.example.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class RuleEngineService {

    public boolean evaluate(Map<String, Object> data, JsonNode rule) {
        if (rule == null || rule.isNull()) {
            throw new IllegalArgumentException("Rule cannot be null");
        }
        if (rule.isObject()) {
            ObjectNode obj = (ObjectNode) rule;
            // Group operators: all/any/not
            if (obj.has("all")) {
                return evalGroup(data, obj.get("all"), true);
            } else if (obj.has("any")) {
                return evalGroup(data, obj.get("any"), false);
            } else if (obj.has("not")) {
                return !evaluate(data, obj.get("not"));
            } else {
                // condition
                return evalCondition(data, obj);
            }
        } else if (rule.isArray()) {
            // default for top-level array: AND all
            return evalGroup(data, rule, true);
        }
        throw new IllegalArgumentException("Unsupported rule type: " + rule.getNodeType());
    }

    private boolean evalGroup(Map<String, Object> data, JsonNode arrNode, boolean andLogic) {
        if (arrNode == null || arrNode.isNull()) {
            return andLogic; // empty follows identity
        }
        if (!arrNode.isArray()) {
            throw new IllegalArgumentException("Group must be an array");
        }
        ArrayNode arr = (ArrayNode) arrNode;
        boolean result = andLogic;
        for (JsonNode child : arr) {
            boolean childRes = evaluate(data, child);
            if (andLogic) {
                result = childRes;
                if (!result) return false; // short-circuit
            } else {
                result = childRes;
                if (result) return true; // short-circuit
            }
        }
        return result;
    }

    private boolean evalCondition(Map<String, Object> data, ObjectNode cond) {
        String field = textOrNull(cond, "field");
        String opStr = textOrNull(cond, "op");
        JsonNode valueNode = cond.get("value");
        if (opStr == null) {
            throw new IllegalArgumentException("Condition missing 'op'");
        }
        Operator op;
        try {
            op = Operator.valueOf(opStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown operator: " + opStr);
        }

        Object left = field != null ? resolvePath(data, field) : null;

        switch (op) {
            case exists:
                return field != null && dataContainsPath(data, field);
            case not_exists:
                return field == null || !dataContainsPath(data, field);
            case isNull:
                return field != null && left == null;
            case isNotNull:
                return field != null && left != null;
            case between:
                if (valueNode == null || !valueNode.isArray() || valueNode.size() != 2) {
                    throw new IllegalArgumentException("between requires array [min, max]");
                }
                Object min = jsonToJava(valueNode.get(0));
                Object max = jsonToJava(valueNode.get(1));
                return compare(left, min) >= 0 && compare(left, max) <= 0;
            case in:
                if (valueNode == null || !valueNode.isArray()) {
                    throw new IllegalArgumentException("in requires array of values");
                }
                for (JsonNode v : valueNode) {
                    if (compare(left, jsonToJava(v)) == 0) return true;
                }
                return false;
            case contains:
                return evalContains(left, valueNode);
            case regex:
                if (!(left instanceof String)) return false;
                String pattern = valueNode != null && valueNode.isTextual() ? valueNode.asText() : null;
                if (pattern == null) throw new IllegalArgumentException("regex requires string pattern");
                return Pattern.compile(pattern).matcher((String) left).find();
            case eq:
                return compare(left, jsonToJava(valueNode)) == 0;
            case ne:
                return compare(left, jsonToJava(valueNode)) != 0;
            case lt:
                return compare(left, jsonToJava(valueNode)) < 0;
            case lte:
                return compare(left, jsonToJava(valueNode)) <= 0;
            case gt:
                return compare(left, jsonToJava(valueNode)) > 0;
            case gte:
                return compare(left, jsonToJava(valueNode)) >= 0;
            default:
                throw new IllegalArgumentException("Operator not implemented: " + op);
        }
    }

    /**
     * Evaluates the {@code contains} operator.
     * - Collection: true if any element is equal (via {@link #compare}) to {@code value}.
     * - String: true if it contains {@code value}'s string representation as a substring.
     * - Map/Object: true if any top-level value equals {@code value} (keys are not matched,
     *   nested objects/arrays are not recursively searched).
     * - Any other left type (Number, Boolean, null): false.
     * Throws IllegalArgumentException if {@code value} is missing/null or is an array.
     */
    private boolean evalContains(Object left, JsonNode valueNode) {
        if (valueNode == null || valueNode.isNull()) {
            throw new IllegalArgumentException("contains requires a non-null 'value'");
        }
        if (valueNode.isArray()) {
            throw new IllegalArgumentException(
                    "contains does not support an array 'value'; use 'in' for value-membership checks");
        }
        if (left == null) return false;

        Object right = jsonToJava(valueNode);
        if (left instanceof Map<?, ?> map) {
            for (Object v : map.values()) {
                if (compare(v, right) == 0) return true;
            }
            return false;
        }
        if (left instanceof Collection<?> collection) {
            for (Object item : collection) {
                if (compare(item, right) == 0) return true;
            }
            return false;
        }
        if (left instanceof String s) {
            return s.contains(String.valueOf(right));
        }
        return false; // Number, Boolean, or any other unsupported left type
    }

    private String textOrNull(ObjectNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && v.isTextual() ? v.asText() : null;
    }

    private Object jsonToJava(JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isTextual()) return node.asText();
        if (node.isNumber()) {
            if (node.isIntegralNumber()) return node.asLong();
            return node.decimalValue();
        }
        if (node.isBoolean()) return node.asBoolean();
        if (node.isArray()) {
            List<Object> list = new ArrayList<>();
            node.forEach(n -> list.add(jsonToJava(n)));
            return list;
        }
        // for objects, convert to map of simple values
        if (node.isObject()) {
            Map<String, Object> map = new LinkedHashMap<>();
            node.fields().forEachRemaining(e -> map.put(e.getKey(), jsonToJava(e.getValue())));
            return map;
        }
        return null;
    }

    private boolean dataContainsPath(Map<String, Object> data, String path) {
        return resolvePathInternal(data, path, false) != null;
    }

    private Object resolvePath(Map<String, Object> data, String path) {
        return resolvePathInternal(data, path, true);
    }

    private Object resolvePathInternal(Map<String, Object> data, String path, boolean returnValue) {
        String[] parts = path.split("\\.");
        Object current = data;
        for (String p : parts) {
            if (current instanceof Map<?, ?> map) {
                if (!map.containsKey(p)) return null;
                current = map.get(p);
            } else {
                return null;
            }
        }
        return returnValue ? current : new Object();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int compare(Object a, Object b) {
        if (Objects.equals(a, b)) return 0;
        if (a == null) return -1;
        if (b == null) return 1;

        // Try numeric comparison using BigDecimal when possible
        BigDecimal na = toBigDecimalOrNull(a);
        BigDecimal nb = toBigDecimalOrNull(b);
        if (na != null && nb != null) {
            return na.compareTo(nb);
        }

        // Try instant (ISO-8601 string) comparison
        Instant ia = toInstantOrNull(a);
        Instant ib = toInstantOrNull(b);
        if (ia != null && ib != null) {
            return ia.compareTo(ib);
        }

        // Fallback to Comparable or string comparison
        if (a instanceof Comparable<?> && a.getClass().isInstance(b)) {
            // Use raw Comparable to avoid wildcard capture issues when comparing with Object
            return ((Comparable) a).compareTo(b);
        }
        return String.valueOf(a).compareTo(String.valueOf(b));
    }

    private BigDecimal toBigDecimalOrNull(Object obj) {
        if (obj == null) return null;
        if (obj instanceof BigDecimal bd) return bd;
        if (obj instanceof Number n) return new BigDecimal(n.doubleValue());
        if (obj instanceof String s) {
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Instant toInstantOrNull(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Instant i) return i;
        if (obj instanceof String s) {
            try {
                return Instant.parse(s);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }
}
