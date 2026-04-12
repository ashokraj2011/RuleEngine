package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api.dto.EvaluateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RuleEngineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void evaluateEndpointWorks() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("age", 33);
        Map<String, Object> rule = new HashMap<>();
        rule.put("field", "age");
        rule.put("op", "gte");
        rule.put("value", 21);

        Map<String, Object> payload = new HashMap<>();
        payload.put("data", data);
        payload.put("rule", rule);

        mockMvc.perform(post("/api/v1/rule-engine/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
    }
}
