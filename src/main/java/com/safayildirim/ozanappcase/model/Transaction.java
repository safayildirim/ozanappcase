package com.safayildirim.ozanappcase.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;

    private Date date;
    private String source;
    private String target;
    private double amount;

    private double rate;

    private double convertedAmount;

    public Transaction(Date date, String source, String target, double amount, double rate, double convertedAmount) {
        this.date = date;
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.rate = rate;
        this.convertedAmount = convertedAmount;
    }

    public Transaction() {

    }
}
