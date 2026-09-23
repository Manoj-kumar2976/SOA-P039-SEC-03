package com.vanguard.settlement.controller;

import com.vanguard.settlement.model.Settlement;
import com.vanguard.settlement.repository.SettlementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settlement")
public class SettlementController {

    private final SettlementRepository settlementRepository;

    public SettlementController(SettlementRepository settlementRepository) {
        this.settlementRepository = settlementRepository;
    }

    // Called by Claim Service once a claim is validated
    @PostMapping
    public Settlement createSettlement(@RequestBody Settlement settlement) {
        return settlementRepository.save(settlement);
    }

    @GetMapping
    public List<Settlement> getAllSettlements() {
        return settlementRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Settlement> getSettlement(@PathVariable Long id) {
        return settlementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Adjuster/Admin approves and pays out the settlement
    @PutMapping("/{id}/approve")
    public ResponseEntity<Settlement> approveSettlement(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return settlementRepository.findById(id).map(settlement -> {
            settlement.setPaymentStatus("PAID");
            if (body.containsKey("approvedAmount")) {
                settlement.setApprovedAmount(new java.math.BigDecimal(body.get("approvedAmount").toString()));
            }
            return ResponseEntity.ok(settlementRepository.save(settlement));
        }).orElse(ResponseEntity.notFound().build());
    }
}
