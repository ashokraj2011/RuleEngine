package org.example.api.dto;

import java.math.BigDecimal;

public class InterestResult {
    private BigDecimal principal;
    private BigDecimal rate;
    private BigDecimal period;
    private BigDecimal interest;
    private String resultType;

    public InterestResult() {
    }

    public InterestResult(BigDecimal principal, BigDecimal rate, BigDecimal period, BigDecimal interest, String resultType) {
        this.principal = principal;
        this.rate = rate;
        this.period = period;
        this.interest = interest;
        this.resultType = resultType;
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

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
