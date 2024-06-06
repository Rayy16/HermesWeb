package com.hermes.service.Impl;

import com.hermes.service.StringCacheService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MemStringCacheService implements StringCacheService {
    final private static ConcurrentHashMap<String, WrapExpire> data = new ConcurrentHashMap<>();

    @Override
    public String get(String key) {
        WrapExpire wrapExpire = data.get(key);
        if (wrapExpire == null) {
            return null;
        }
        // if expiredAt is null, it means that the key will never expired
        if (wrapExpire.getExpiredAt() == null) {
            return wrapExpire.getVal();
        }
        // if expiredTime is before now then clean key
        if (wrapExpire.getExpiredAt().isBefore(LocalDateTime.now())) {
            data.remove(key, wrapExpire);
            log.info("lazy remove {} = {}", key, wrapExpire);
            return null;
        }
        return wrapExpire.getVal();
    }

    @Override
    public Boolean set(String key, String val) {
        WrapExpire wrapExpire = WrapExpire.build(val, null);
        data.put(key, wrapExpire);
        log.info("set {} = {}", key, wrapExpire);
        return true;
    }

    @Override
    public Boolean set(String key, String val, Duration expiredTime) {
        WrapExpire wrapExpire = WrapExpire.build(val, LocalDateTime.now().plus(expiredTime));
        data.put(key, wrapExpire);
        log.info("set {} = {}", key, wrapExpire);
        return true;
    }

    @Override
    public Boolean remove(String key) {
        data.remove(key);
        log.info("remove {}", key);
        return true;
    }

}

@Getter
class WrapExpire {
    public static WrapExpire build(String val, LocalDateTime expiredAt) {
        return new WrapExpire(val, expiredAt);
    }
    private WrapExpire(String val, LocalDateTime expiredAt) {
        this.val = val;
        this.expiredAt = expiredAt;
    }
    final private String val;
    final private LocalDateTime expiredAt;

    @Override
    public String toString() {
        return "WrapExpire{" +
                "val='" + val + '\'' +
                ", expiredAt=" + expiredAt +
                '}';
    }
}
