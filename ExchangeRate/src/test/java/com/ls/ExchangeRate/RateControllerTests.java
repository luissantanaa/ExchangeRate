package com.ls.ExchangeRate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ls.ExchangeRate.controller.RateController;
import com.ls.ExchangeRate.dto.ServiceResponseDto;
import com.ls.ExchangeRate.service.RateService;

@SpringBootTest
public class RateControllerTests {

    @InjectMocks
    private RateController rateController;

    @Mock
    private RateService rateService;

    @Test
    @DisplayName("Tests getExchangeRate endpoint with correct parameters and expects correct result")
    public void testGetExchangeRateSuccess() throws IOException {
        String from = "EUR";
        String to = "USD";
        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(200, "", 1);

        Mockito.when(rateService.getExchangeRate(from, to)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getExchangeRate(from, to);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), "Exchange Rate: " + serviceResponse.getResult());
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with incorrect \"from\" parameter and expects incorrect result")
    public void testGetExchangeRateFail1() throws IOException {
        String from = "EU";
        String to = "USD";
        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(400, "Error processing request response", null);

        Mockito.when(rateService.getExchangeRate(from, to)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getExchangeRate(from, to);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getMessage());
    }

    @Test
    @DisplayName("Tests getExchangeRate endpoint with incorrect \"to\" parameter and expects incorrect result")
    public void testGetExchangeRateFail2() throws IOException {
        String from = "EUR";
        String to = "US";
        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(400, "Error processing request response", null);

        Mockito.when(rateService.getExchangeRate(from, to)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getExchangeRate(from, to);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getMessage());
    }

    @Test
    @DisplayName("Tests getAllExchangeRates endpoint with correct parameters and expects correct result")
    public void testGetAllExchangeRatesSuccess() throws IOException {
        String base = "EUR";
        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(200, "", List.of("1"));

        Mockito.when(rateService.getAllExchangeRates(base)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getAllExchangeRates(base);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
    }

    @Test
    @DisplayName("Tests getAllExchangeRates endpoint with incorrect \"base\" parameter and expects incorrect result")
    public void testGetAllExchangeRatesFail() throws IOException {
        String base = "EU";
        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(400, "Base currency not found", null);

        Mockito.when(rateService.getAllExchangeRates(base)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getAllExchangeRates(base);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getMessage());
    }

    @Test
    @DisplayName("Tests getConvertedAmount endpoint with correct parameters and expects correct result")
    public void testGetConvertedAmountSuccess() throws IOException {
        String from = "EUR";
        String to = "USD";
        int amount = 1;

        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(200, "", 1);

        Mockito.when(rateService.getConvertedAmount(from, to, amount)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getConvertedAmount(from, to, amount);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), "Converted amount: " + serviceResponse.getResult());
    }

    @Test
    @DisplayName("Tests getConvertedAmount endpoint with incorrect \"from\" parameter and expects incorrect result\"")
    public void testGetConvertedAmountFail1() throws IOException {
        String from = "EU";
        String to = "USD";
        int amount = 1;

        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(400,
                "Base or Final currency must exist and Amount must be over 0", null);

        Mockito.when(rateService.getConvertedAmount(from, to, amount)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getConvertedAmount(from, to, amount);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getMessage());
    }

    @Test
    @DisplayName("Tests getConvertedAmount endpoint with incorrect \"to\" parameter and expects incorrect result\"")
    public void testGetConvertedAmountFail2() throws IOException {
        String from = "EUR";
        String to = "US";
        int amount = 1;

        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(400,
                "Base or Final currency must exist and Amount must be over 0", null);

        Mockito.when(rateService.getConvertedAmount(from, to, amount)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getConvertedAmount(from, to, amount);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getMessage());
    }

    @Test
    @DisplayName("Tests getConvertedAmount endpoint with incorrect \"amount\" parameter and expects incorrect result\"")
    public void testGetConvertedAmountFail3() throws IOException {
        String from = "EUR";
        String to = "USD";
        int amount = -1;

        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(400,
                "Base or Final currency must exist and Amount must be over 0", null);

        Mockito.when(rateService.getConvertedAmount(from, to, amount)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getConvertedAmount(from, to, amount);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getMessage());
    }

    @Test
    @DisplayName("Tests getConvertedAmountToAllCurr endpoint with correct parameters and expects correct result")
    public void testGetConvertedAmountToCurrsSuccess1() throws IOException {
        String from = "EUR";
        List<String> to = List.of("USD");
        int amount = 1;

        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(200, "", 1);

        Mockito.when(rateService.getConvertedAmountToAllCurr(from, to, amount)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getConvertedAmountToAllCurr(from, to, amount);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getResult().toString());
    }

    @Test
    @DisplayName("Tests getConvertedAmountToAllCurr endpoint with correct parameters and expects correct result")
    public void testGetConvertedAmountToCurrsSuccess2() throws IOException {
        String from = "EUR";
        List<String> to = List.of("USD", "GBP");
        int amount = 1;
        List<String> res = Arrays.asList("1", "2");

        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(200, "", res);

        Mockito.when(rateService.getConvertedAmountToAllCurr(from, to, amount)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getConvertedAmountToAllCurr(from, to, amount);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getResult().toString().replace(",", "\n"));
    }

    @Test
    @DisplayName("Tests getConvertedAmountToAllCurr endpoint with incorrect \"from\" parameter and expects incorrect result\"")
    public void testGetConvertedAmountToCurrsFail1() throws IOException {
        String from = "EU";
        List<String> to = List.of("USD");
        int amount = 1;

        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(400,
                "Base or Final currency must exist and Amount must be over 0", null);

        Mockito.when(rateService.getConvertedAmountToAllCurr(from, to, amount)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getConvertedAmountToAllCurr(from, to, amount);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getMessage());
    }

    @Test
    @DisplayName("Tests getConvertedAmountToAllCurr endpoint with incorrect \"to\" parameter and expects incorrect result\"")
    public void testGetConvertedAmountToCurrsFail2() throws IOException {
        String from = "EUR";
        List<String> to = List.of("US");
        int amount = 1;

        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(400,
                "Base or Final currency must exist and Amount must be over 0", null);

        Mockito.when(rateService.getConvertedAmountToAllCurr(from, to, amount)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getConvertedAmountToAllCurr(from, to, amount);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getMessage());
    }

    @Test
    @DisplayName("Tests getConvertedAmountToAllCurr endpoint with incorrect \"to\" parameter and expects incorrect result\"")
    public void testGetConvertedAmountToCurrsFail3() throws IOException {
        String from = "EUR";
        List<String> to = List.of("USD", "GB");
        int amount = 1;

        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(400,
                "Base or Final currency must exist and Amount must be over 0", null);

        Mockito.when(rateService.getConvertedAmountToAllCurr(from, to, amount)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getConvertedAmountToAllCurr(from, to, amount);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getMessage());
    }

    @Test
    @DisplayName("Tests getConvertedAmountToAllCurr endpoint with incorrect \"amount\" parameter and expects incorrect result\"")
    public void testGetConvertedAmountToCurrsFail4() throws IOException {
        String from = "EUR";
        List<String> to = List.of("USD");
        int amount = -1;

        ServiceResponseDto serviceResponse = serviceResponseDtoBuilder(400,
                "Base or Final currency must exist and Amount must be over 0", null);

        Mockito.when(rateService.getConvertedAmountToAllCurr(from, to, amount)).thenReturn(serviceResponse);
        ResponseEntity<String> controllerResponse = rateController.getConvertedAmountToAllCurr(from, to, amount);

        assertEquals(controllerResponse.getStatusCode(), HttpStatus.valueOf(serviceResponse.getStatusCode()));
        assertEquals(controllerResponse.getBody(), serviceResponse.getMessage());
    }

    private ServiceResponseDto serviceResponseDtoBuilder(int statusCode, String message, Object result) {
        ServiceResponseDto response = ServiceResponseDto.builder().statusCode(statusCode)
                .message(message)
                .result(result)
                .build();
        return response;
    }
}
