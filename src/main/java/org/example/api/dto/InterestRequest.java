package org.example.api.dto;

import java.math.BigDecimal;

public class InterestRequest {
    private BigDecimal principal;
    private BigDecimal rate;
    private BigDecimal period;

    public InterestRequest() {
    }

    public InterestRequest(BigDecimal principal, BigDecimal rate, BigDecimal period) {
        this.principal = principal;
        this.rate = rate;
        this.period = period;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getPeriod() {
        return period;
    }

    public void setPeriod(BigDecimal period) {
        this.period = period;
    }
}
