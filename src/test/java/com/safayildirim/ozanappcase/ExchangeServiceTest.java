package com.safayildirim.ozanappcase;

import com.safayildirim.ozanappcase.exceptions.ExchangeClientException;
import com.safayildirim.ozanappcase.exceptions.NotFoundException;
import com.safayildirim.ozanappcase.model.ConvertedAmountResponse;
import com.safayildirim.ozanappcase.model.ExchangeResponse;
import com.safayildirim.ozanappcase.model.Transaction;
import com.safayildirim.ozanappcase.service.ExchangeService;
import com.safayildirim.ozanappcase.service.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.xml.crypto.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ExchangeServiceTest {

    @InjectMocks
    private ExchangeService service;

    @Mock
    private TransactionRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void GivenCurrenciesWhenGetExchangeRateCalledThenShouldReturnRate() {
        ExchangeResponse e = new ExchangeResponse();
        e.setSuccess(true);
        e.setBase("EUR");
        e.setDate(new Date().toString());
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.18);
        rates.put("EUR", 1.0);
        rates.put("TRY", 21.45);
        e.setRates(rates);

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any(),
                Mockito.anyString())).thenReturn(new ResponseEntity<>(e, HttpStatus.OK));

        double actualRate = service.getExchangeRate("USD", "TRY");

        assertEquals(18.18, round(actualRate));
    }

    @Test
    public void GivenClientReturnedErrorWhenGetExchangeRateCalledThenShouldThrowError() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any(),
                Mockito.anyString())).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HttpServerErrorException.class, () -> service.getExchangeRate("USD", "TRY"));
    }

    @Test
    public void GivenRequestFailedWhenGetExchangeRateCalledThenShouldThrowError() {
        ExchangeResponse e = new ExchangeResponse();
        e.setSuccess(false);

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any(),
                Mockito.anyString())).thenReturn(new ResponseEntity<>(e, HttpStatus.OK));

        assertThrows(ExchangeClientException.class, () -> service.getExchangeRate("USD", "TRY"));
    }

    @Test
    public void GivenSourceCurrencyNotFoundWhenGetExchangeRateCalledThenShouldThrowNotFoundError() {
        ExchangeResponse e = new ExchangeResponse();
        e.setSuccess(true);
        e.setBase("EUR");
        e.setDate(new Date().toString());
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.18);
        rates.put("EUR", 1.0);
        e.setRates(rates);

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any(),
                Mockito.anyString())).thenReturn(new ResponseEntity<>(e, HttpStatus.OK));

        assertThrows(NotFoundException.class, () -> service.getExchangeRate("USD", "TRY"));
    }

    @Test
    public void GivenTargetCurrencyNotFoundWhenGetExchangeRateCalledThenShouldThrowNotFoundError() {
        ExchangeResponse e = new ExchangeResponse();
        e.setSuccess(true);
        e.setBase("EUR");
        e.setDate(new Date().toString());
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 1.0);
        rates.put("TRY", 21.45);
        e.setRates(rates);

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any(),
                Mockito.anyString())).thenReturn(new ResponseEntity<>(e, HttpStatus.OK));

        assertThrows(NotFoundException.class, () -> service.getExchangeRate("USD", "TRY"));
    }

    @Test
    public void GivenSourceCurrencyIsZeroWhenGetExchangeRateCalledThenShouldThrowIllegalArgumentException() {
        ExchangeResponse e = new ExchangeResponse();
        e.setSuccess(true);
        e.setBase("EUR");
        e.setDate(new Date().toString());
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 0.0);
        rates.put("EUR", 1.0);
        rates.put("TRY", 21.45);
        e.setRates(rates);

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any(),
                Mockito.anyString())).thenReturn(new ResponseEntity<>(e, HttpStatus.OK));

        assertThrows(IllegalArgumentException.class, () -> service.getExchangeRate("USD", "TRY"));
    }

    @Test
    public void GivenCurrenciesAndAmountWhenGetConvertedAmountCalledThenShouldReturnConvertedAmount() {
        ExchangeResponse e = new ExchangeResponse();
        e.setSuccess(true);
        e.setBase("EUR");
        e.setDate(new Date().toString());
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.18);
        rates.put("EUR", 1.0);
        rates.put("TRY", 21.45);
        e.setRates(rates);

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any(),
                Mockito.anyString())).thenReturn(new ResponseEntity<>(e, HttpStatus.OK));

        Transaction t = new Transaction();
        t.setId(1L);
        t.setDate(new Date());
        t.setSource("USD");
        t.setTarget("TRY");
        t.setAmount(10);
        t.setConvertedAmount(180.18);


        Mockito.when(repository.save(Mockito.any())).thenReturn(t);

        ConvertedAmountResponse response = service.getConvertedAmount("USD", "TRY", 10);

        assertEquals(181.78, round(response.getAmount()));
    }

    @Test
    public void GivenIDWhenGetTransactionCalledThenShouldReturnTransaction() {
        Transaction t = new Transaction();
        t.setId(1L);
        t.setDate(new Date());
        t.setSource("USD");
        t.setTarget("TRY");
        t.setRate(19.45);
        t.setAmount(10);
        t.setConvertedAmount(180.18);


        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(t));

        Transaction actualTransaction = service.getTransaction(1L);

        assertEquals(t, actualTransaction);
    }

    @Test
    public void GivenNotExistingTransactionIDWhenGetTransactionCalledThenShouldThrowNotFoundException() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getTransaction(1L));
        assertThrows(NotFoundException.class, () -> service.getTransaction(1L), "asd");
    }


    public double round(double val){
        return  Math.round(val * 100.0) / 100.0;
    }

}

