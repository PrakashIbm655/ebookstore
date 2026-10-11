package com.ebookstore.repository;

import com.ebookstore.model.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdAndUserId(Long id, Long userId);
}
