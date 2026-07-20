package com.finguard.account.config;
import com.finguard.account.domain.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;
import java.util.UUID;
@Configuration
public class DemoDataConfig {
 public static final UUID CUSTOMER_ID=UUID.fromString("11111111-1111-1111-1111-111111111111");
 public static final UUID PRIMARY=UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
 public static final UUID SAVINGS=UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
 public static final UUID DEMO_DESTINATION=UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
 @Bean CommandLineRunner seed(AccountRepository accounts){return args->{
  if(!accounts.existsById(PRIMARY)) accounts.save(new Account(PRIMARY,CUSTOMER_ID.toString(),"900000001234",new BigDecimal("12500.00"),"USD"));
  if(!accounts.existsById(SAVINGS)) accounts.save(new Account(SAVINGS,CUSTOMER_ID.toString(),"900000005678",new BigDecimal("32000.00"),"USD"));
  if(!accounts.existsById(DEMO_DESTINATION)) accounts.save(new Account(DEMO_DESTINATION,"33333333-3333-3333-3333-333333333333","900000009999",new BigDecimal("4100.00"),"USD"));
 };}
}
