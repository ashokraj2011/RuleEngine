package org.example.api;

import jakarta.validation.Valid;
import org.example.api.dto.InterestRequest;
import org.example.api.dto.InterestResult;
import org.example.rules.InterestCalculationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/interest", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterestController {

    private final InterestCalculationService interestCalculationService;

    public InterestController(InterestCalculationService interestCalculationService) {
        this.interestCalculationService = interestCalculationService;
    }

    @PostMapping(path = "/calculate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public InterestResult calculate(@Valid @RequestBody InterestRequest request) {
        return interestCalculationService.calculate(request);
    }
}
