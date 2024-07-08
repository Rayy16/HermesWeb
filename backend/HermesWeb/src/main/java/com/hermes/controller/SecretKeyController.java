package com.hermes.controller;

import com.hermes.common.Result;
import com.hermes.dto.PublicKeyDTO;
import com.hermes.service.SecretKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/secret_key")
public class SecretKeyController {
    @Autowired
    private SecretKeyService secretKeyService;

    @GetMapping("/rsa/public_key")
    public Result getEncodedRSAPublicKey() {
        return new HandlerTemplate<Object, Result>() {
            @Override
            protected Result doProcess(Object request) {
                List<String> publicKeyAndVersion = secretKeyService.getPublicKeyAndVersion();
                return Result.Success(PublicKeyDTO.builder().
                        encodedPublicKey(publicKeyAndVersion.get(0)).version(publicKeyAndVersion.get(1))
                        .build());
            }
        }.process(null);
    }
}
