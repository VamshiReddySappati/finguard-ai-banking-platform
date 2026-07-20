package com.finguard.auth.security;

import com.finguard.auth.domain.AppUser;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    @Test void issuesSignedToken(){
        JwtService service=new JwtService("a-very-long-test-secret-with-more-than-thirty-two-characters",60);
        String token=service.issue(new AppUser(UUID.randomUUID(),"test@example.com","hash","CUSTOMER"));
        assertThat(token.split("\.")).hasSize(3);
    }
}
