package org.example.api;

import jakarta.validation.Valid;
import org.example.api.dto.EvaluateRequest;
import org.example.api.dto.EvaluateResponse;
import org.example.rules.RuleEngineService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/rule-engine", produces = MediaType.APPLICATION_JSON_VALUE)
public class RuleEngineController {

    private final RuleEngineService ruleEngineService;

    public RuleEngineController(RuleEngineService ruleEngineService) {
        this.ruleEngineService = ruleEngineService;
    }

    @PostMapping(path = "/evaluate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EvaluateResponse evaluate(@Valid @RequestBody EvaluateRequest request) {
        boolean result = ruleEngineService.evaluate(request.getData(), request.getRule());
        return new EvaluateResponse(result);
    }
}
