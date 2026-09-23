package com.vanguard.policy.controller;

import com.vanguard.policy.model.Policy;
import com.vanguard.policy.repository.PolicyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policy")
public class PolicyController {

    private final PolicyRepository policyRepository;

    public PolicyController(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @PostMapping
    public Policy createPolicy(@RequestBody Policy policy) {
        return policyRepository.save(policy);
    }

    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicy(@PathVariable Long id) {
        return policyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Called by Claim Service to validate coverage before accepting a claim
    @GetMapping("/{id}/verify")
    public ResponseEntity<?> verifyCoverage(@PathVariable Long id, @RequestParam BigDecimal claimAmount) {
        return policyRepository.findById(id)
                .<ResponseEntity<?>>map(policy -> {
                    boolean active = "ACTIVE".equalsIgnoreCase(policy.getPolicyStatus());
                    boolean withinLimit = claimAmount.compareTo(policy.getCoverageAmount()) <= 0;
                    boolean valid = active && withinLimit;
                    return ResponseEntity.ok(Map.of(
                            "valid", valid,
                            "policyStatus", policy.getPolicyStatus(),
                            "coverageAmount", policy.getCoverageAmount()
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
