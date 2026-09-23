package com.vanguard.claim.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // @LoadBalanced lets us call other services by their Eureka app name
    // (e.g. http://policy-service/...) instead of a hardcoded host:port,
    // and spreads calls across instances if more than one is running.
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
