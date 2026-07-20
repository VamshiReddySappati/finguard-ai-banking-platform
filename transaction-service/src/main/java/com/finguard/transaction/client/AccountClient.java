package com.finguard.transaction.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class AccountClient {
    private final RestClient client;
    private final String token;

    public AccountClient(RestClient.Builder builder,
                         @Value("${clients.account-service.base-url}") String baseUrl,
                         @Value("${security.internal-token}") String token) {
        this.client = builder.baseUrl(baseUrl).build();
        this.token = token;
    }

    public AccountSnapshot get(UUID id) {
        return client.get()
                .uri("/internal/accounts/{id}", id)
                .header("X-Internal-Token", token)
                .retrieve()
                .body(AccountSnapshot.class);
    }

    public InternalTransferResponse transfer(UUID transactionId, UUID sourceAccountId,
                                             UUID destinationAccountId, BigDecimal amount) {
        return client.post()
                .uri("/internal/accounts/transfer")
                .header("X-Internal-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new InternalTransferRequest(transactionId, sourceAccountId,
                        destinationAccountId, amount))
                .retrieve()
                .body(InternalTransferResponse.class);
    }
}
