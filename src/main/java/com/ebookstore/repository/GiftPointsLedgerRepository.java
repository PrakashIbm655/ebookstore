package com.ebookstore.repository;

import com.ebookstore.model.GiftPointsLedger;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftPointsLedgerRepository extends JpaRepository<GiftPointsLedger, Long> {

    Optional<GiftPointsLedger> findByUserId(Long userId);
}
