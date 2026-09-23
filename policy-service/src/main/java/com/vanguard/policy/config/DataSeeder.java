package com.vanguard.policy.config;

import com.vanguard.policy.model.Policy;
import com.vanguard.policy.repository.PolicyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// Seeds a couple of sample policies on startup so you can demo without
// manually inserting data first.
@Component
public class DataSeeder implements CommandLineRunner {

    private final PolicyRepository policyRepository;

    public DataSeeder(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public void run(String... args) {
        policyRepository.save(new Policy(101L, new BigDecimal("50000"), "ACTIVE"));
        policyRepository.save(new Policy(102L, new BigDecimal("20000"), "ACTIVE"));
        policyRepository.save(new Policy(103L, new BigDecimal("10000"), "EXPIRED"));
    }
}
