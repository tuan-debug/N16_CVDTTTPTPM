package com.lighting.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // Redis key generation methods matching Node.js RedisKeys
    public String pinVerifyKey(String email) {
        return "pin_verify:" + email;
    }

    public String refreshTokenKey(String userId) {
        return "refresh_token:" + userId;
    }

    public String transactionKey(String transactionId) {
        return "transaction:" + transactionId;
    }

    public String allProductsKey() {
        return "products:all";
    }

    public String productDetailKey(String productId) {
        return "product:detail:" + productId;
    }
}
