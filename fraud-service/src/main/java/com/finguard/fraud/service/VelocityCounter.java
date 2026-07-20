package com.finguard.fraud.service;
import org.springframework.data.redis.core.StringRedisTemplate;import org.springframework.stereotype.Component;import java.time.Duration;import java.util.UUID;
@Component public class VelocityCounter {
 private final StringRedisTemplate redis;public VelocityCounter(StringRedisTemplate redis){this.redis=redis;}
 public long increment(UUID accountId){String key="fraud:velocity:"+accountId;Long count=redis.opsForValue().increment(key);if(count!=null&&count==1)redis.expire(key,Duration.ofMinutes(1));return count==null?1:count;}
}
