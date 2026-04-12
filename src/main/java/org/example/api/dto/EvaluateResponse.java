package org.example.api.dto;

public class EvaluateResponse {
    private boolean result;

    public EvaluateResponse() {}

    public EvaluateResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
