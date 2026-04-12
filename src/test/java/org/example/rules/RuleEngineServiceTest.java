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
}
