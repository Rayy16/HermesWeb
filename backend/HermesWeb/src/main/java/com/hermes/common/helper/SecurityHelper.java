package com.hermes.common.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityHelper {
    final private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public static String encodeData(String rawData) {
        return passwordEncoder.encode(rawData);
    }
    public static Boolean matches(String rawData, String decryptData) {
        return passwordEncoder.matches(rawData, decryptData);
    }
}
