package com.hermes.common.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityHelper {
    final private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public static String encodeData(String rawData) {
        return passwordEncoder.encode(rawData);
    }
    public static Boolean matches(String encodedData, String decryptData) {
        return passwordEncoder.matches(decryptData, encodedData);
    }

    public static void main(String[] args) {
        String encodeData = SecurityHelper.encodeData("123456");
        System.out.println(SecurityHelper.matches(encodeData, "123456"));
    }
}
