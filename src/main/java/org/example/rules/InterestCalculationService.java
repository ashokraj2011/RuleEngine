package org.example.rules;

import org.example.api.dto.InterestRequest;
import org.example.api.dto.InterestResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class InterestCalculationService {

    public InterestResult calculate(InterestRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body must not be null");
        }

        BigDecimal principal = request.getPrincipal();
        BigDecimal rate = request.getRate();
        BigDecimal period = request.getPeriod();

        if (principal == null || rate == null || period == null) {
            throw new IllegalArgumentException("principal, rate, and period are required");
        }

        if (principal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("principal must be non-negative");
        }
        if (rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("rate must be non-negative");
        }
        if (period.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("period must be non-negative");
        }

        BigDecimal interest = principal.multiply(rate).multiply(period).setScale(2, RoundingMode.HALF_UP);
        return new InterestResult(principal, rate, period, interest, "simple-interest");
    }
}
