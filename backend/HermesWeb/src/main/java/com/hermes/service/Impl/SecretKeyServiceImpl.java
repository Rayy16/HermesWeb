package com.hermes.service.Impl;

import com.hermes.service.SecretKeyService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class SecretKeyServiceImpl implements SecretKeyService {
    private static SecretKeyAndVersion key;

    public List<String> getPublicKeyAndVersion() {
        PublicKey publicKey;
        String version;
        synchronized (SecretKeyServiceImpl.class) {
            publicKey = key.getSecretKey().getPublic();
            version = key.getVersion();
        }
        ArrayList<String> publicKeyAndVersion = new ArrayList<>();
        publicKeyAndVersion.add(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        publicKeyAndVersion.add(version);
        return publicKeyAndVersion;
    }
    /**
     * if PrivateKey is null, it means that secretKey was refreshed, front-end need re-get publicKey
     * */
    public PrivateKey getPrivateKeyAndCheckVersion(String version) {
        synchronized (SecretKeyServiceImpl.class) {
            if(!key.getVersion().equals(version)) {
                return null;
            }
            return key.getSecretKey().getPrivate();
        }
    }
    /**
     * it can decrypt base64 format data which encrypt by rsa/pcks1 algorithm
     * */
    public String decryptData(PrivateKey privateKey, String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decodedBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedRateString = "${hermes.service.secret-key.duration}")
    private static void refreshKeys() {
        synchronized (SecretKeyServiceImpl.class) {
            key = keyGenerator();
            log.info("<< refresh key >>");
        }
    }
    private static SecretKeyAndVersion keyGenerator() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            String version = String.format("%06d", new Random().nextInt(1000000));
            log.info("generate key with version: {}", version);
            return SecretKeyAndVersion.build(keyPair, version);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}

@Getter
class SecretKeyAndVersion {
    final private KeyPair secretKey;
    final private String version;
    private SecretKeyAndVersion(KeyPair secretKey, String version) {
        this.secretKey = secretKey;
        this.version = version;
    }
    public static SecretKeyAndVersion build(KeyPair secretKey, String version) {
        return new SecretKeyAndVersion(secretKey, version);
    }
}


