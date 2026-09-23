package com.vanguard.settlement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "settlement")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settlementId;

    private Long claimId;
    private BigDecimal approvedAmount;
    private String paymentStatus; // PENDING, APPROVED, PAID, REJECTED

    public Settlement() {}

    public Settlement(Long claimId, BigDecimal approvedAmount, String paymentStatus) {
        this.claimId = claimId;
        this.approvedAmount = approvedAmount;
        this.paymentStatus = paymentStatus;
    }

    public Long getSettlementId() { return settlementId; }
    public void setSettlementId(Long settlementId) { this.settlementId = settlementId; }
    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
