package com.finguard.investigation.api;

import com.finguard.investigation.service.InvestigationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investigations")
@PreAuthorize("hasRole('ADMIN')")
public class InvestigationController {
    private final InvestigationService service;

    public InvestigationController(InvestigationService service) {
        this.service = service;
    }

    @PostMapping("/brief")
    public InvestigationResponse brief(@Valid @RequestBody InvestigationRequest request) {
        return service.generate(request);
    }
}
