package com.vanguard.settlement.repository;

import com.vanguard.settlement.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}
