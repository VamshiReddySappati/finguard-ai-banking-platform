package com.finguard.account.api;

import com.finguard.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService service;
    public AccountController(AccountService service){this.service=service;}
    @GetMapping public List<AccountResponse> mine(Authentication auth){return service.ownedBy(auth.getName());}
    @GetMapping("/{id}") public AccountResponse one(@PathVariable UUID id,Authentication auth){return service.getOwned(id,auth.getName(),isAdmin(auth));}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public AccountResponse create(@Valid @RequestBody CreateAccountRequest request,Authentication auth){return service.create(auth.getName(),request);}
    @GetMapping("/admin/all") @PreAuthorize("hasRole('ADMIN')") public List<AccountResponse> all(){return service.all();}
    private boolean isAdmin(Authentication auth){return auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));}
}
