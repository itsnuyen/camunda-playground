package com.camunda.tasklist.demo;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.impl.ZeebeClientBuilderImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

// Profile for local
@Slf4j
@Configuration
public class ClientConfigs {

    @Bean
    @Primary
    public ZeebeClient camundaTasklistClientConfiguration() {
        log.info("Creating ZeebeClient for local profile");
        log.warn("######################## NOT FOR PROD USAGE ########################");
        ZeebeClientBuilder zeebeClientBuilder = new ZeebeClientBuilderImpl();
        ZeebeClient zeebeClient = zeebeClientBuilder
                .usePlaintext()
                .preferRestOverGrpc(false)
                .withChainHandlers(
                        (request, producer, scope, chain, callback) -> {
                            request.setHeader("Cookie", "OPERATE-SESSION=427BF534D99A1256FF9A328885FB80E4");
                            chain.proceed(request, producer, scope, callback);
                        }).build();
        return zeebeClient;
    }
}
