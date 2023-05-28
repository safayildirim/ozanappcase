package com.safayildirim.ozanappcase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.safayildirim.ozanappcase.controller.ExchangeController;
import com.safayildirim.ozanappcase.exceptions.NotFoundException;
import com.safayildirim.ozanappcase.model.ConvertedAmountResponse;
import com.safayildirim.ozanappcase.service.ExchangeService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = ExchangeController.class)
public class ExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeService exchangeService;

    @Test
    public void GivenCurrenciesWhenGetExchangeRateCalledThenShouldReturnExchangeRate() throws Exception {

        Mockito.when(exchangeService.getExchangeRate(Mockito.anyString(), Mockito.anyString())).thenReturn(21.45);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/exchange").queryParam("source", "EUR").queryParam("target", "TRY");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());


        assertEquals("21.45", result.getResponse().getContentAsString());
    }

    @Test
    public void GivenCurrencyNotFoundWhenGetExchangeRateCalledThenShouldReturnNotFoundError() throws Exception {

        Mockito.when(exchangeService.getExchangeRate(Mockito.anyString(), Mockito.anyString())).thenThrow(NotFoundException.class);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/exchange").queryParam("source", "AAA").queryParam("target", "TRY");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());


        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    public void GivenCurrenciesAndAmountWhenGetConvertedAmountCalledThenShouldReturnConvertedAmount() throws Exception {

        Mockito.when(exchangeService.getConvertedAmount(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyDouble())).thenReturn(new ConvertedAmountResponse(1L, 250.0));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/exchange/convert").queryParam("source", "EUR").queryParam("target", "TRY").
                queryParam("amount", "15");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse());


        JSONObject o = new JSONObject(result.getResponse().getContentAsString());

        assertEquals(1, o.get("transactionID"));
        assertEquals(250.0, o.get("amount"));
    }

    @Test
    public void GivenCurrencyNotFoundWhenGetConvertedAmountCalledThenShouldReturnNotFoundError() throws Exception {

        Mockito.when(exchangeService.getConvertedAmount(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyDouble())).thenThrow(NotFoundException.class);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        "/exchange/convert").queryParam("source", "TTT").queryParam("target", "TRY").
                queryParam("amount", "15");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(result.getResponse().getContentAsString());


        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }
}
