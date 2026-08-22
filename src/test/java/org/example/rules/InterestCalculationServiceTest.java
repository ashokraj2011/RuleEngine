package org.example.rules;

import org.example.api.dto.InterestRequest;
import org.example.api.dto.InterestResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Acceptance coverage for the interest calculation contract.
 *
 * @ac:AC-001
 * @ifc:IFC-002
 */
class InterestCalculationServiceTest {

    private final InterestCalculationService service = new InterestCalculationService();

    @Test
    void calculatesSimpleInterestForKnownExample() {
        InterestRequest request = new InterestRequest(
                new BigDecimal("1000"),
                new BigDecimal("0.05"),
                new BigDecimal("2")
        );

        InterestResult result = service.calculate(request);

        assertEquals(new BigDecimal("1000"), result.getPrincipal());
        assertEquals(new BigDecimal("0.05"), result.getRate());
        assertEquals(new BigDecimal("2"), result.getPeriod());
        assertEquals(new BigDecimal("100.00"), result.getInterest());
        assertEquals("simple-interest", result.getResultType());
    }

    @Test
    void rejectsNegativePrincipal() {
        InterestRequest request = new InterestRequest(
                new BigDecimal("-1"),
                new BigDecimal("0.05"),
                new BigDecimal("2")
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.calculate(request));
        assertTrue(ex.getMessage().contains("principal"));
    }

    @Test
    void rejectsNegativeRate() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.calculate(
                request("1000", "-0.01", "2")
        ));

        assertTrue(ex.getMessage().contains("rate"));
    }

    @Test
    void rejectsNegativePeriod() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.calculate(
                request("1000", "0.05", "-1")
        ));

        assertTrue(ex.getMessage().contains("period"));
    }

    @Test
    void rejectsNullRequest() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.calculate(null));

        assertTrue(ex.getMessage().contains("Request body"));
    }

    @Test
    void rejectsEveryMissingRequiredValue() {
        assertTrue(assertThrows(IllegalArgumentException.class, () -> service.calculate(
                new InterestRequest(null, new BigDecimal("0.05"), new BigDecimal("2"))
        )).getMessage().contains("required"));
        assertTrue(assertThrows(IllegalArgumentException.class, () -> service.calculate(
                new InterestRequest(new BigDecimal("1000"), null, new BigDecimal("2"))
        )).getMessage().contains("required"));
        assertTrue(assertThrows(IllegalArgumentException.class, () -> service.calculate(
                new InterestRequest(new BigDecimal("1000"), new BigDecimal("0.05"), null)
        )).getMessage().contains("required"));
    }

    @Test
    void acceptsZeroBoundaryValues() {
        InterestResult result = service.calculate(request("0", "0", "0"));

        assertEquals(new BigDecimal("0.00"), result.getInterest());
    }

    @Test
    void roundsInterestToTwoDecimalPlacesUsingHalfUp() {
        InterestResult result = service.calculate(request("100", "0.01235", "1"));

        assertEquals(new BigDecimal("1.24"), result.getInterest());
    }

    private InterestRequest request(String principal, String rate, String period) {
        return new InterestRequest(new BigDecimal(principal), new BigDecimal(rate), new BigDecimal(period));
    }
}
