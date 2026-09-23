package com.vanguard.claim.controller;

import com.vanguard.claim.model.Claim;
import com.vanguard.claim.repository.ClaimRepository;
import com.vanguard.claim.service.ClaimProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimRepository claimRepository;
    private final ClaimProcessingService claimProcessingService;

    public ClaimController(ClaimRepository claimRepository, ClaimProcessingService claimProcessingService) {
        this.claimRepository = claimRepository;
        this.claimProcessingService = claimProcessingService;
    }

    // Submits a claim and triggers Claim -> Policy -> Settlement flow
    @PostMapping
    public Claim submitClaim(@RequestBody Claim claim) {
        return claimProcessingService.submitClaim(claim);
    }

    @GetMapping
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaim(@PathVariable Long id) {
        return claimRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
