package com.hermes.common.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Date;

public class JwtTokenHelper {
    private static final String SECRET_KEY = generateSecretKey();

    private static String generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HMACSHA256");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate secret key", e);
        }
    }
    public static String createToken(Map<String, Object> claims, long expireTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expireTime * 1000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
