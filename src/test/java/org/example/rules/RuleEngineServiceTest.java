package org.example.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RuleEngineServiceTest {

    private RuleEngineService service;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        service = new RuleEngineService();
        mapper = new ObjectMapper();
    }

    @Test
    void testSimpleEquality() {
        Map<String, Object> data = Map.of("age", 30);
        ObjectNode rule = mapper.createObjectNode();
        rule.put("field", "age");
        rule.put("op", "eq");
        rule.put("value", 30);
        assertTrue(service.evaluate(data, rule));
    }

    @Test
    void testGreaterThanAndContains() {
        Map<String, Object> data = new HashMap<>();
        data.put("age", 25);
        data.put("tags", List.of("gold", "vip"));

        ObjectNode gt = mapper.createObjectNode();
        gt.put("field", "age");
        gt.put("op", "gt");
        gt.put("value", 18);

        ObjectNode contains = mapper.createObjectNode();
        contains.put("field", "tags");
        contains.put("op", "contains");
        contains.put("value", "vip");

        ObjectNode group = mapper.createObjectNode();
        ArrayNode all = mapper.createArrayNode();
        all.add(gt);
        all.add(contains);
        group.set("all", all);

        assertTrue(service.evaluate(data, group));
    }

    @Test
    void testAnyGroupFalse() {
        Map<String, Object> data = Map.of("age", 10);

        ObjectNode gt18 = mapper.createObjectNode();
        gt18.put("field", "age");
        gt18.put("op", "gt");
        gt18.put("value", 18);

        ObjectNode eq15 = mapper.createObjectNode();
        eq15.put("field", "age");
        eq15.put("op", "eq");
        eq15.put("value", 15);

        ObjectNode any = mapper.createObjectNode();
        ArrayNode arr = mapper.createArrayNode();
        arr.add(gt18);
        arr.add(eq15);
        any.set("any", arr);

        assertFalse(service.evaluate(data, any));
    }

    @Test
    void testBetweenAndRegex() {
        Map<String, Object> data = new HashMap<>();
        data.put("age", 42);
        data.put("email", "user42@example.com");

        ObjectNode between = mapper.createObjectNode();
        between.put("field", "age");
        between.put("op", "between");
        ArrayNode range = mapper.createArrayNode();
        range.add(40);
        range.add(50);
        between.set("value", range);

        ObjectNode regex = mapper.createObjectNode();
        regex.put("field", "email");
        regex.put("op", "regex");
        regex.put("value", "^user\\d+@example\\.com$");

        ObjectNode group = mapper.createObjectNode();
        ArrayNode all = mapper.createArrayNode();
        all.add(between);
        all.add(regex);
        group.set("all", all);

        assertTrue(service.evaluate(data, group));
    }

    @Test
    void testExistsAndNotExists() {
        Map<String, Object> data = Map.of("name", "Alice");

        ObjectNode exists = mapper.createObjectNode();
        exists.put("field", "name");
        exists.put("op", "exists");

        ObjectNode notExists = mapper.createObjectNode();
        notExists.put("field", "age");
        notExists.put("op", "not_exists");

        ObjectNode group = mapper.createObjectNode();
        ArrayNode all = mapper.createArrayNode();
        all.add(exists);
        all.add(notExists);
        group.set("all", all);

        assertTrue(service.evaluate(data, group));
    }

    @Test
    void testIsNull() {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Alice");
        user.put("phone", null);
        data.put("user", user);
        data.put("age", 30);

        // Field with explicit null value → isNull should be true
        ObjectNode isNullPhone = mapper.createObjectNode();
        isNullPhone.put("field", "user.phone");
        isNullPhone.put("op", "isNull");
        assertTrue(service.evaluate(data, isNullPhone));

        // Missing field → isNull should be true
        ObjectNode isNullMissing = mapper.createObjectNode();
        isNullMissing.put("field", "user.email");
        isNullMissing.put("op", "isNull");
        assertTrue(service.evaluate(data, isNullMissing));

        // Field with non-null value → isNull should be false
        ObjectNode isNullName = mapper.createObjectNode();
        isNullName.put("field", "user.name");
        isNullName.put("op", "isNull");
        assertFalse(service.evaluate(data, isNullName));

        // Top-level field with non-null value → isNull should be false
        ObjectNode isNullAge = mapper.createObjectNode();
        isNullAge.put("field", "age");
        isNullAge.put("op", "isNull");
        assertFalse(service.evaluate(data, isNullAge));
    }

    @Test
    void testIsNotNull() {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Alice");
        user.put("phone", null);
        data.put("user", user);
        data.put("age", 30);

        // Field with explicit null value → isNotNull should be false
        ObjectNode isNotNullPhone = mapper.createObjectNode();
        isNotNullPhone.put("field", "user.phone");
        isNotNullPhone.put("op", "isNotNull");
        assertFalse(service.evaluate(data, isNotNullPhone));

        // Missing field → isNotNull should be false
        ObjectNode isNotNullMissing = mapper.createObjectNode();
        isNotNullMissing.put("field", "user.email");
        isNotNullMissing.put("op", "isNotNull");
        assertFalse(service.evaluate(data, isNotNullMissing));

        // Field with non-null value → isNotNull should be true
        ObjectNode isNotNullName = mapper.createObjectNode();
        isNotNullName.put("field", "user.name");
        isNotNullName.put("op", "isNotNull");
        assertTrue(service.evaluate(data, isNotNullName));

        // Top-level field with non-null value → isNotNull should be true
        ObjectNode isNotNullAge = mapper.createObjectNode();
        isNotNullAge.put("field", "age");
        isNotNullAge.put("op", "isNotNull");
        assertTrue(service.evaluate(data, isNotNullAge));
    }

    private ObjectNode containsRule(String field, Object value) {
        ObjectNode rule = mapper.createObjectNode();
        rule.put("field", field);
        rule.put("op", "contains");
        if (value == null) {
            rule.putNull("value");
        } else {
            rule.set("value", mapper.valueToTree(value));
        }
        return rule;
    }

    @Test
    void testContainsArrayStringMatch() {
        // AC1: array contains matching string element
        Map<String, Object> data = Map.of("tags", List.of("vip", "gold"));
        assertTrue(service.evaluate(data, containsRule("tags", "vip")));
    }

    @Test
    void testContainsArrayStringNoMatch() {
        // AC2: array does not contain the given string element
        Map<String, Object> data = Map.of("tags", List.of("vip", "gold"));
        assertFalse(service.evaluate(data, containsRule("tags", "silver")));
    }

    @Test
    void testContainsStringSubstringMatch() {
        // AC3: string substring match
        Map<String, Object> data = Map.of("name", "hello world");
        assertTrue(service.evaluate(data, containsRule("name", "wor")));
    }

    @Test
    void testContainsStringIsCaseSensitive() {
        // AC4: substring match is case-sensitive
        Map<String, Object> data = Map.of("name", "hello world");
        assertFalse(service.evaluate(data, containsRule("name", "WOR")));
    }

    @Test
    void testContainsArrayNumericEquality() {
        // AC5: numeric equality, not string containment
        Map<String, Object> data = Map.of("scores", List.of(1, 2, 3));
        assertTrue(service.evaluate(data, containsRule("scores", 2)));
    }

    @Test
    void testContainsMapValueMatch() {
        // AC6: object value containment
        Map<String, Object> data = Map.of("user", Map.of("role", "admin"));
        assertTrue(service.evaluate(data, containsRule("user", "admin")));
    }

    @Test
    void testContainsMapKeyIsNotMatched() {
        // AC6b: keys are never matched, only values
        Map<String, Object> data = Map.of("user", Map.of("role", "admin"));
        assertFalse(service.evaluate(data, containsRule("user", "role")));
    }

    @Test
    void testContainsMissingFieldReturnsFalse() {
        // AC7: missing/null field resolves to false, not an error
        Map<String, Object> data = Map.of("tags", List.of("vip"));
        assertFalse(service.evaluate(data, containsRule("missing", "vip")));
    }

    @Test
    void testContainsNumberFieldReturnsFalse() {
        // AC8: number field returns false, not an error
        Map<String, Object> data = Map.of("age", 42);
        assertFalse(service.evaluate(data, containsRule("age", 42)));
    }

    @Test
    void testContainsBooleanFieldReturnsFalse() {
        // AC8: boolean field returns false, not an error
        Map<String, Object> data = Map.of("active", true);
        assertFalse(service.evaluate(data, containsRule("active", true)));
    }

    @Test
    void testContainsMissingValueThrows() {
        // AC9: missing 'value' raises a clear validation error
        Map<String, Object> data = Map.of("tags", List.of("vip"));
        ObjectNode rule = mapper.createObjectNode();
        rule.put("field", "tags");
        rule.put("op", "contains");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.evaluate(data, rule));
        assertTrue(ex.getMessage().toLowerCase().contains("value"));
    }

    @Test
    void testContainsArrayValueThrows() {
        // AC10: array 'value' is rejected with a clear validation error
        Map<String, Object> data = Map.of("tags", List.of("vip", "gold"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.evaluate(data, containsRule("tags", List.of("vip", "gold"))));
        assertTrue(ex.getMessage().toLowerCase().contains("array"));
    }

    @Test
    void testContainsComposesInGroups() {
        // AC11: contains composes correctly inside all/any/not groups
        Map<String, Object> data = Map.of("tags", List.of("vip", "gold"));

        ObjectNode all = mapper.createObjectNode();
        ArrayNode allArr = mapper.createArrayNode();
        allArr.add(containsRule("tags", "vip"));
        allArr.add(containsRule("tags", "gold"));
        all.set("all", allArr);
        assertTrue(service.evaluate(data, all));

        ObjectNode any = mapper.createObjectNode();
        ArrayNode anyArr = mapper.createArrayNode();
        anyArr.add(containsRule("tags", "silver"));
        anyArr.add(containsRule("tags", "gold"));
        any.set("any", anyArr);
        assertTrue(service.evaluate(data, any));

        ObjectNode not = mapper.createObjectNode();
        not.set("not", containsRule("tags", "silver"));
        assertTrue(service.evaluate(data, not));
    }

    @Test
    void testContainsEmptyArrayIsFalse() {
        Map<String, Object> data = Map.of("tags", List.<String>of());
        assertFalse(service.evaluate(data, containsRule("tags", "vip")));
    }

    @Test
    void testContainsEmptyStringEdgeCases() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "");
        assertFalse(service.evaluate(data, containsRule("name", "x")));
        assertTrue(service.evaluate(data, containsRule("name", "")));

        data.put("name", "hello");
        assertTrue(service.evaluate(data, containsRule("name", "")));
    }

    @Test
    void testContainsArrayOfObjectsDeepEquality() {
        Map<String, Object> data = Map.of("items", List.of(Map.of("id", 1)));
        assertTrue(service.evaluate(data, containsRule("items", Map.of("id", 1))));
        assertFalse(service.evaluate(data, containsRule("items", Map.of("id", 2))));
    }

    @Test
    void testContainsExplicitNullFieldReturnsFalse() {
        Map<String, Object> data = new HashMap<>();
        data.put("tags", null);
        assertFalse(service.evaluate(data, containsRule("tags", "vip")));
    }

    @Test
    void testContainsNestedObjectValueIsNotRecursive() {
        Map<String, Object> data = Map.of("user", Map.of("address", Map.of("city", "NY")));
        assertFalse(service.evaluate(data, containsRule("user", "NY")));
    }

    @Test
    void testContainsEmptyObjectIsFalse() {
        Map<String, Object> data = Map.of("user", Map.<String, Object>of());
        assertFalse(service.evaluate(data, containsRule("user", "admin")));
    }

    @Test
    void testContainsUnicodeSubstring() {
        Map<String, Object> data = Map.of("name", "café \uD83D\uDE00 world");
        assertTrue(service.evaluate(data, containsRule("name", "café")));
        assertTrue(service.evaluate(data, containsRule("name", "\uD83D\uDE00")));
        assertFalse(service.evaluate(data, containsRule("name", "cafe")));
    }
}
