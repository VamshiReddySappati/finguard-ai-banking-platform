package com.finguard.account.api;

import com.finguard.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/internal/accounts")
public class InternalAccountController {
    private final AccountService service;
    private final String internalToken;

    public InternalAccountController(AccountService service,
                                     @Value("${security.internal-token}") String internalToken) {
        this.service = service;
        this.internalToken = internalToken;
    }

    @GetMapping("/{id}")
    public AccountSnapshot snapshot(@PathVariable UUID id,
                                    @RequestHeader("X-Internal-Token") String token) {
        verify(token);
        return service.snapshot(id);
    }

    @PostMapping("/transfer")
    public InternalTransferResponse transfer(@Valid @RequestBody InternalTransferRequest request,
                                             @RequestHeader("X-Internal-Token") String token) {
        verify(token);
        return service.transfer(request);
    }

    private void verify(String supplied) {
        if (!internalToken.equals(supplied)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal service token");
        }
    }
}
