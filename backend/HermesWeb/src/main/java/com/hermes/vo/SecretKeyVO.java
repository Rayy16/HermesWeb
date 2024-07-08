package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;

import java.security.PrivateKey;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SecretKeyVO {
    String publicKeyVersion;
    @ApiModelProperty(hidden = true)
    PrivateKey privateKey;

    public String getPublicKeyVersion() {
        return publicKeyVersion;
    }

    public void setPublicKeyVersion(String publicKeyVersion) {
        this.publicKeyVersion = publicKeyVersion;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
