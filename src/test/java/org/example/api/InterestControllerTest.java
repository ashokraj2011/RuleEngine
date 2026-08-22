package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rules.InterestCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @ac:AC-001
 * @ifc:IFC-001
 * @ifc:IFC-003
 */
class InterestControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new InterestController(new InterestCalculationService()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void interestEndpointCalculatesSimpleInterest() throws Exception {
        Map<String, Object> payload = Map.of(
                "principal", 1000,
                "rate", 0.05,
                "period", 2
        );

        mockMvc.perform(post("/interest/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal").value(1000))
                .andExpect(jsonPath("$.rate").value(0.05))
                .andExpect(jsonPath("$.period").value(2))
                .andExpect(jsonPath("$.interest").value(100.0))
                .andExpect(jsonPath("$.resultType").value("simple-interest"));
    }

    @Test
    void interestEndpointRejectsNegativePrincipal() throws Exception {
        Map<String, Object> payload = Map.of(
                "principal", -1,
                "rate", 0.05,
                "period", 2
        );

        mockMvc.perform(post("/interest/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("principal must be non-negative"));
    }

    @Test
    void interestEndpointRejectsNegativeRate() throws Exception {
        assertInvalid(Map.of("principal", 1000, "rate", -0.01, "period", 2), "rate must be non-negative");
    }

    @Test
    void interestEndpointRejectsNegativePeriod() throws Exception {
        assertInvalid(Map.of("principal", 1000, "rate", 0.05, "period", -1), "period must be non-negative");
    }

    @Test
    void interestEndpointRejectsMissingRequiredValue() throws Exception {
        assertInvalid(Map.of("principal", 1000, "rate", 0.05), "principal, rate, and period are required");
    }

    @Test
    void interestEndpointRejectsMalformedJsonWithStableErrorContract() throws Exception {
        mockMvc.perform(post("/interest/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"principal\": 1000,"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }

    private void assertInvalid(Map<String, Object> payload, String message) throws Exception {
        mockMvc.perform(post("/interest/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(message));
    }
}
