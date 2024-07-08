package com.hermes.service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public interface SecretKeyService {
    List<String> getPublicKeyAndVersion();
    PrivateKey getPrivateKeyAndCheckVersion(String version);
    String decryptData(PrivateKey privateKey, String encryptedData);
    String encryptData(PublicKey publicKey, String rawData);
    PublicKey getPublicKeyAndCheckVersion(String version);
}
