package com.finguard.fraud.api;
import com.finguard.fraud.service.FraudDecisionService;import jakarta.validation.Valid;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/fraud") @PreAuthorize("hasRole('ADMIN')") public class FraudController {
 private final FraudDecisionService service;public FraudController(FraudDecisionService service){this.service=service;}
 @GetMapping public List<FraudDecisionResponse> all(){return service.all().stream().map(FraudDecisionResponse::from).toList();}
 @GetMapping("/{transactionId}") public FraudDecisionResponse one(@PathVariable UUID transactionId){return FraudDecisionResponse.from(service.one(transactionId));}
 @PostMapping("/{transactionId}/resolve") public FraudDecisionResponse resolve(@PathVariable UUID transactionId,@Valid @RequestBody ResolveDecisionRequest request){return FraudDecisionResponse.from(service.resolve(transactionId,request.decision(),request.note()));}
}
