package com.safayildirim.ozanappcase.service;

import com.safayildirim.ozanappcase.exceptions.ExchangeClientException;
import com.safayildirim.ozanappcase.exceptions.NotFoundException;
import com.safayildirim.ozanappcase.model.ConvertedAmountResponse;
import com.safayildirim.ozanappcase.model.ExchangeResponse;
import com.safayildirim.ozanappcase.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class ExchangeService {

    @Autowired
    private RestTemplate client;

    @Autowired
    private TransactionRepository transactionRepository;

    private static final String SERVER_URL = "http://data.fixer.io/api";
    private static final String apiKey = "182bcef7efcf5c5f0a826ef0921f0592";

    public ExchangeService() {
    }

    public double getExchangeRate(String source, String target) {
        ResponseEntity<ExchangeResponse> response =
                this.client.getForEntity(SERVER_URL + "/latest?access_key={key}", ExchangeResponse.class, apiKey);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new HttpServerErrorException(response.getStatusCode());
        }
        if (!response.getBody().isSuccess()) {
            throw new ExchangeClientException("Request failed, try again...");
        }
        Map<String, Double> rates = response.getBody().getRates();
        if (!rates.containsKey(source)) {
            throw new NotFoundException(String.format("Currency %s is not found.", source));
        }
        if (!rates.containsKey(target)) {
            throw new NotFoundException(String.format("Currency %s is not found.", target));
        }
        if (rates.get(source) == 0) {
            throw new IllegalArgumentException(String.format("Currency %s can not be zero.", source));
        }

        return rates.get(target) / rates.get(source);
    }

    public ConvertedAmountResponse getConvertedAmount(String source, String target, double amount) {
        double rate = this.getExchangeRate(source, target);
        double convertedAmount = rate * amount;
        Transaction t = transactionRepository.save(new Transaction(new Date(), source, target, amount, rate, convertedAmount));
        return new ConvertedAmountResponse(t.getId(), convertedAmount);
    }

    public Transaction getTransaction(Long id) {
        Optional<Transaction> t = transactionRepository.findById(id);
        if (t.isEmpty()) {
            throw new NotFoundException(String.format("Transaction %d is not found.", id));
        }
        return t.get();
    }
}
