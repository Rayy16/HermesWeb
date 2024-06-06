package com.hermes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicKeyDTO {
    private String encodedPublicKey;
    private String version;
}
