package com.vanguard.claim.service;

import com.vanguard.claim.model.Claim;
import com.vanguard.claim.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ClaimProcessingService {

    private final ClaimRepository claimRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public ClaimProcessingService(ClaimRepository claimRepository, RestTemplate restTemplate) {
        this.claimRepository = claimRepository;
        this.restTemplate = restTemplate;
    }

    public Claim submitClaim(Claim claim) {
        claim.setStatus("SUBMITTED");
        Claim saved = claimRepository.save(claim);

        // Step 1: Claim -> Policy (verify coverage)
        String verifyUrl = String.format(
                "http://policy-service/api/policy/%d/verify?claimAmount=%s",
                saved.getPolicyId(), saved.getClaimAmount().toString());

        Map<?, ?> verifyResponse;
        try {
            verifyResponse = restTemplate.getForObject(verifyUrl, Map.class);
        } catch (Exception e) {
            saved.setStatus("REJECTED");
            return claimRepository.save(saved);
        }

        boolean valid = verifyResponse != null && Boolean.TRUE.equals(verifyResponse.get("valid"));
        if (!valid) {
            saved.setStatus("REJECTED");
            return claimRepository.save(saved);
        }

        saved.setStatus("VALIDATED");
        claimRepository.save(saved);

        // Step 2: Policy -> Settlement (create settlement record)
        Map<String, Object> settlementRequest = Map.of(
                "claimId", saved.getClaimId(),
                "approvedAmount", saved.getClaimAmount(),
                "paymentStatus", "PENDING"
        );

        try {
            restTemplate.postForObject("http://settlement-service/api/settlement",
                    settlementRequest, Map.class);
            saved.setStatus("SETTLEMENT_INITIATED");
        } catch (Exception e) {
            saved.setStatus("VALIDATED_SETTLEMENT_PENDING");
        }

        return claimRepository.save(saved);
    }
}
