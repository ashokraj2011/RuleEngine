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
            case log:
                if (left == null) return false;
                if (!(left instanceof Number)) {
                    throw new IllegalArgumentException("log operator requires numeric value");
                }
                
                double numVal = ((Number) left).doubleValue();
                if (numVal <= 0) {
                    throw new IllegalArgumentException("log requires positive value");
                }
                
                double logVal = Math.log10(numVal);
                
                String logCompOp = textOrNull(cond, "comparison");
                if (logCompOp == null) logCompOp = "eq";
                Object logExpectedValue = jsonToJava(valueNode);
                if (!(logExpectedValue instanceof Number)) {
                    throw new IllegalArgumentException("log expected value must be numeric");
                }
                double logTarget = ((Number) logExpectedValue).doubleValue();
                
                switch (logCompOp) {
                    case "eq":
                        return Math.abs(logVal - logTarget) < 0.0001;
                    case "ne":
                        return Math.abs(logVal - logTarget) >= 0.0001;
                    case "lt":
                        return logVal < logTarget;
                    case "lte":
                        return logVal <= logTarget;
                    case "gt":
                        return logVal > logTarget;
                    case "gte":
                        return logVal >= logTarget;
                    default:
                        throw new IllegalArgumentException("Unsupported comparison operator for log: " + logCompOp);
                }
            case length:
                if (left == null) return false;
                int len = 0;
                if (left instanceof String s) {
                    len = s.length();
                } else if (left instanceof Collection<?> c) {
                    len = c.size();
                } else {
                    return false;
                }
                
                String compOp = textOrNull(cond, "comparison");
                if (compOp == null) compOp = "eq";
                Object compValue = jsonToJava(valueNode);
                int targetVal = ((Number) compValue).intValue();
                
                switch (compOp) {
                    case "eq":
                        return len == targetVal;
                    case "ne":
                        return len != targetVal;
                    case "lt":
                        return len < targetVal;
                    case "lte":
                        return len <= targetVal;
                    case "gt":
                        return len > targetVal;
                    case "gte":
                        return len >= targetVal;
                    case "between":
                        if (valueNode == null || !valueNode.isArray() || valueNode.size() != 2) {
                            throw new IllegalArgumentException("between requires array [min, max]");
                        }
                        int min = ((Number) jsonToJava(valueNode.get(0))).intValue();
                        int max = ((Number) jsonToJava(valueNode.get(1))).intValue();
                        return len >= min && len <= max;
                    default:
                        throw new IllegalArgumentException("Unsupported comparison operator: " + compOp);
                }
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
                if (left == null) return false;
                Object right = jsonToJava(valueNode);
                if (left instanceof Collection<?>) {
                    for (Object item : (Collection<?>) left) {
                        if (compare(item, right) == 0) return true;
                    }
                    return false;
                } else if (left instanceof String s) {
                    return right != null && s.contains(String.valueOf(right));
                }
                return false;
            case notContains:
                if (left == null) return false;
                Object rightNot = jsonToJava(valueNode);
                if (left instanceof Collection<?>) {
                    for (Object item : (Collection<?>) left) {
                        if (compare(item, rightNot) == 0) return false;
                    }
                    return true;
                } else if (left instanceof String s) {
                    return rightNot == null || !s.contains(String.valueOf(rightNot));
                }
                return false;
            case startsWith:
                if (left == null) return false;
                Object prefix = jsonToJava(valueNode);
                if (left instanceof String s) {
                    return prefix != null && s.startsWith(String.valueOf(prefix));
                }
                return false;
            case notStartsWith:
                if (left == null) return false;
                Object prefixNot = jsonToJava(valueNode);
                if (left instanceof String s) {
                    return prefixNot == null || !s.startsWith(String.valueOf(prefixNot));
                }
                return false;
            case endsWith:
                if (left == null) return false;
                Object suffix = jsonToJava(valueNode);
                if (left instanceof String s) {
                    return suffix != null && s.toLowerCase().endsWith(String.valueOf(suffix).toLowerCase());
                }
                return false;
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
                throw new IllegalArgumentException("Unsupported operator: " + op);
        }
    }

    private String textOrNull(ObjectNode obj, String key) {
        JsonNode node = obj.get(key);
        return (node != null && node.isTextual()) ? node.asText() : null;
    }

    private Object resolvePath(Map<String, Object> data, String path) {
        if (path == null || path.isEmpty()) return null;
        String[] parts = path.split("\\.");
        Object current = data;
        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                return null;
            }
        }
        return current;
    }

    private boolean dataContainsPath(Map<String, Object> data, String path) {
        if (path == null || path.isEmpty()) return false;
        String[] parts = path.split("\\.");
        Object current = data;
        for (String part : parts) {
            if (current instanceof Map) {
                if (!((Map<?, ?>) current).containsKey(part)) {
                    return false;
                }
                current = ((Map<?, ?>) current).get(part);
            } else {
                return false;
            }
        }
        return true;
    }

    private Object jsonToJava(JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isBoolean()) return node.asBoolean();
        if (node.isInt()) return node.asInt();
        if (node.isLong()) return node.asLong();
        if (node.isDouble() || node.isFloat()) return node.asDouble();
        if (node.isTextual()) return node.asText();
        if (node.isArray()) {
            List<Object> list = new ArrayList<>();
            for (JsonNode el : node) {
                list.add(jsonToJava(el));
            }
            return list;
        }
        if (node.isObject()) {
            Map<String, Object> map = new HashMap<>();
            node.fields().forEachRemaining(e -> map.put(e.getKey(), jsonToJava(e.getValue())));
            return map;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private int compare(Object a, Object b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        
        if (a.equals(b)) return 0;
        
        if (a instanceof Number && b instanceof Number) {
            BigDecimal aD = new BigDecimal(a.toString());
            BigDecimal bD = new BigDecimal(b.toString());
            return aD.compareTo(bD);
        }
        
        if (a instanceof String && b instanceof String) {
            return ((String) a).compareTo((String) b);
        }
        
        if (a instanceof Comparable && b instanceof Comparable) {
            try {
                return ((Comparable) a).compareTo(b);
            } catch (ClassCastException e) {
                // fall through
            }
        }
        
        return a.toString().compareTo(b.toString());
    }
}
