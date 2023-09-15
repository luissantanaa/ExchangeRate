package com.ls.ExchangeRate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ls.ExchangeRate.dto.ServiceResponseDto;
import com.ls.ExchangeRate.service.RateService;

@SpringBootTest
public class RateServiceTests {

    @Autowired
    private RateService rateService;

    @Test
    @DisplayName("Tests getExchangeRate endpoint with correct parameters and expects correct result")
    public void testGetExchangeRateSuccess() throws IOException {
        String from = "USD";
        String to = "EUR";

        ServiceResponseDto response = rateService.getExchangeRate(from, to);
        assertNotNull(response.getResult());
        assertEquals(response.getStatusCode(), 200);
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with incorrect \"from\" parameter and expects incorrect result")
    public void testGetExchangeRateFail1() throws IOException {
        String from = "US";
        String to = "EUR";

        ServiceResponseDto response = rateService.getExchangeRate(from, to);
        assertNull(response.getResult());
        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with incorrect \"to\" parameter and expects incorrect result")
    public void testGetExchangeRateFail2() throws IOException {
        String from = "USD";
        String to = "EU";

        ServiceResponseDto response = rateService.getExchangeRate(from, to);
        assertNull(response.getResult());
        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    @DisplayName("Tests getAllExchangeRates endpoint with correct parameter and expects correct result")
    public void testGetAllExchangeRatesSuccess() throws IOException {
        String base = "EUR";

        ServiceResponseDto response = rateService.getAllExchangeRates(base);
        assertNotNull(response.getResult());
        assertEquals(response.getStatusCode(), 200);
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with incorrect \"base\" parameter and expects incorrect result")
    public void testGetAllExchangeRatesFail() throws IOException {
        String base = "EU";

        ServiceResponseDto response = rateService.getAllExchangeRates(base);
        assertNull(response.getResult());
        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with correct parameters and expects correct result")
    public void testGetConvertedAmountSuccess() throws IOException {
        String from = "EUR";
        String to = "USD";
        int amount = 1;

        ServiceResponseDto response = rateService.getConvertedAmount(from, to, amount);
        assertNotNull(response.getResult());
        assertEquals(response.getStatusCode(), 200);
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with incorrect \"from\" parameter and expects incorrect result")
    public void testGetConvertedAmountFail1() throws IOException {
        String from = "EU";
        String to = "USD";
        int amount = 1;

        ServiceResponseDto response = rateService.getConvertedAmount(from, to, amount);
        assertNull(response.getResult());
        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with incorrect \"amount\" parameter and expects incorrect result")
    public void testGetConvertedAmountFail2() throws IOException {
        String from = "EUR";
        String to = "USD";
        int amount = -1;

        ServiceResponseDto response = rateService.getConvertedAmount(from, to, amount);
        assertNull(response.getResult());
        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    @DisplayName("Tests getConvertedAmountToAllCurr endpoint with correct parameters and expects correct result")
    public void testGetMultipleConvertedAmountSuccess() throws IOException {
        String from = "EUR";
        List<String> to = List.of("USD", "CZK");
        int amount = 1;

        ServiceResponseDto response = rateService.getConvertedAmountToAllCurr(from, to, amount);
        assertNotNull(response.getResult());
        assertEquals(response.getStatusCode(), 200);
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with incorrect \"from\" parameter and expects incorrect result")
    public void testGetMultipleConvertedAmountFail1() throws IOException {
        String from = "EU";
        List<String> to = List.of("USD", "CZK");
        int amount = 1;

        ServiceResponseDto response = rateService.getConvertedAmountToAllCurr(from, to, amount);
        assertNull(response.getResult());
        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with incorrect \"to\" parameter and expects incorrect result")
    public void testGetMultipleConvertedAmountFail2() throws IOException {
        String from = "EUR";
        List<String> to = List.of("US", "CZK");
        int amount = 1;

        ServiceResponseDto response = rateService.getConvertedAmountToAllCurr(from, to, amount);
        assertNull(response.getResult());
        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with incorrect \"amount\" parameter and expects incorrect result")
    public void testGetMultipleConvertedAmountFail3() throws IOException {
        String from = "EUR";
        List<String> to = List.of("USD", "CZK");
        int amount = -1;

        ServiceResponseDto response = rateService.getConvertedAmountToAllCurr(from, to, amount);
        assertNull(response.getResult());
        assertEquals(response.getStatusCode(), 400);
    }

}
