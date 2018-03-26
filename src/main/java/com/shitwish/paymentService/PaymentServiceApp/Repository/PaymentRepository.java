package com.shitwish.paymentService.PaymentServiceApp.Repository;

import com.shitwish.paymentService.PaymentServiceApp.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> getAllByBuyer(Long id);
    List<Payment> getAllBySeller(Long id);
}
