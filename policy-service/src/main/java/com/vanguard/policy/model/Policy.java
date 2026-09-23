package com.vanguard.policy.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "policy")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    private Long userId;
    private BigDecimal coverageAmount;
    private String policyStatus; // ACTIVE, EXPIRED, CANCELLED

    public Policy() {}

    public Policy(Long userId, BigDecimal coverageAmount, String policyStatus) {
        this.userId = userId;
        this.coverageAmount = coverageAmount;
        this.policyStatus = policyStatus;
    }

    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getCoverageAmount() { return coverageAmount; }
    public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }
    public String getPolicyStatus() { return policyStatus; }
    public void setPolicyStatus(String policyStatus) { this.policyStatus = policyStatus; }
}
