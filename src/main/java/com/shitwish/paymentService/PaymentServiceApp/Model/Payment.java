package com.shitwish.paymentService.PaymentServiceApp.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateOfTransaction;

    @Column(nullable = false)
    private long seller;

    @Column(nullable = false)
    private long buyer;

    @Column(nullable = false)
    private BigDecimal amount;

    public Payment(long seller, long buyer, int amount) {
        this.seller = seller;
        this.buyer = buyer;
        this.amount = BigDecimal.valueOf(amount);
        this.dateOfTransaction = new Date();
    }
}
