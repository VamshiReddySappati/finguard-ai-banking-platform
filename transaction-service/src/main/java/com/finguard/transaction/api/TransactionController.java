package com.finguard.transaction.api;
import com.finguard.transaction.service.TransferService;import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/transactions")
public class TransactionController {
 private final TransferService service;public TransactionController(TransferService service){this.service=service;}
 @PostMapping @ResponseStatus(HttpStatus.ACCEPTED) public TransactionResponse create(@RequestHeader("Idempotency-Key") String key,@Valid @RequestBody TransferRequest request,Authentication auth){return service.initiate(auth.getName(),isAdmin(auth),key,request);}
 @GetMapping public List<TransactionResponse> mine(Authentication auth){return service.mine(auth.getName());}
 @GetMapping("/{id}") public TransactionResponse one(@PathVariable UUID id,Authentication auth){return service.one(id,auth.getName(),isAdmin(auth));}
 @GetMapping("/admin/all") @PreAuthorize("hasRole('ADMIN')") public List<TransactionResponse> all(){return service.all();}
 private boolean isAdmin(Authentication auth){return auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));}
}
