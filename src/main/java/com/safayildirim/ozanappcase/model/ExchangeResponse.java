package com.safayildirim.ozanappcase.model;

import lombok.Data;

import java.util.Map;

@Data
public class ExchangeResponse {
    private boolean success;
    private Long timestamp;

    private String base;

    private String date;

    private Map<String, Double> rates;
}
