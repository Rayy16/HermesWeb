package com.hermes.service;

import java.time.Duration;

public interface StringCacheService {
    String get(String key);
    Boolean set(String key, String val);
    Boolean set(String key, String val, Duration expiredTime);
    Boolean remove(String key);
}
