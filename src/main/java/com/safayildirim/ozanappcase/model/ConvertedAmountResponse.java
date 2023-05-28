package com.safayildirim.ozanappcase.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConvertedAmountResponse {
    private Long transactionID;
    private double amount;
}
