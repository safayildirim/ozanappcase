package com.safayildirim.ozanappcase.controller;

import com.safayildirim.ozanappcase.model.ConvertedAmountResponse;
import com.safayildirim.ozanappcase.model.Transaction;
import com.safayildirim.ozanappcase.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exchange")
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @GetMapping()
    public double getExchangeRate(@RequestParam String source, @RequestParam String target) {
        return exchangeService.getExchangeRate(source, target);
    }

    @GetMapping("/convert")
    public ResponseEntity<ConvertedAmountResponse> getConvertedAmount(@RequestParam String source, @RequestParam String target, @RequestParam double amount){
        return new ResponseEntity<>(exchangeService.getConvertedAmount(source, target, amount), HttpStatus.OK);
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id){
        return new ResponseEntity<>(exchangeService.getTransaction(id), HttpStatus.OK);
    }

}
