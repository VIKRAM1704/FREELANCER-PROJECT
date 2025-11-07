package com.freelancenexus.payment.repository;

import com.freelancenexus.payment.model.Payment;
import com.freelancenexus.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByProjectId(Long projectId);
    
    List<Payment> findByPayerId(Long payerId);
    
    List<Payment> findByPayeeId(Long payeeId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByPayerIdOrPayeeId(Long payerId, Long payeeId);
}