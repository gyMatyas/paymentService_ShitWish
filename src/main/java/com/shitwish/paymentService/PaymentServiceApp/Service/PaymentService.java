package com.shitwish.paymentService.PaymentServiceApp.Service;

import com.shitwish.paymentService.PaymentServiceApp.Model.Payment;
import com.shitwish.paymentService.PaymentServiceApp.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository repository;

    public List<Payment> getPaymentsByBuyer(long id) {
        return repository.getAllByBuyer(id);
    }

    public List<Payment> getPaymentsBySeller(long id) {
        return repository.getAllBySeller(id);
    }

    public void savePayment(Payment payment) { repository.save(payment); }
}
