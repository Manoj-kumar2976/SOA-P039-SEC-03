package com.vanguard.claim.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "claim")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    private Long policyId;
    private BigDecimal claimAmount;
    private String status; // SUBMITTED, VALIDATED, REJECTED, SETTLED

    public Claim() {}

    public Claim(Long policyId, BigDecimal claimAmount, String status) {
        this.policyId = policyId;
        this.claimAmount = claimAmount;
        this.status = status;
    }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }
    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }
    public BigDecimal getClaimAmount() { return claimAmount; }
    public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
