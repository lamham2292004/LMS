package com.app.lms.repository;

import com.app.lms.entity.Payment;
import com.app.lms.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByVnpayTxnRef(String vnpayTxnRef);

    List<Payment> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Payment> findByStudentIdAndStatus(Long studentId, PaymentStatus status);

    List<Payment> findByCourse_IdOrderByCreatedAtDesc(Long courseId);

    Boolean existsByVnpayTxnRef(String vnpayTxnRef);

    List<Payment> findAllByOrderByCreatedAtDesc();
}
