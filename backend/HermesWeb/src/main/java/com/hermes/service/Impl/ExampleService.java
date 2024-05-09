package com.hermes.service.Impl;

import com.hermes.service.template.ServiceTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExampleService extends ServiceTemplate<String, String> {
    @Override
    protected String doProcess(String request) {
        return "hello "+request;
    }

    @Override
    protected void validParam(String request) {

    }
}
