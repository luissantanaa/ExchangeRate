package com.ls.ExchangeRate.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ls.ExchangeRate.dto.ServiceResponseDto;
import com.ls.ExchangeRate.service.RateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping(path = "api/rates/")
public class RateController {
    private final RateService rateService;

    @Autowired
    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @Operation(summary = "Retrieve exchange rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Exchange Rate"),
            @ApiResponse(responseCode = "400", description = "Unsuccessful request", content = @Content),
            @ApiResponse(responseCode = "502", description = "Error connecting to external API", content = @Content),
    })

    @GetMapping(path = "getExchangeRate/")
    public ResponseEntity<String> getExchangeRate(@RequestParam("from") String from,
            @RequestParam("to") String to) throws IOException {
        ServiceResponseDto service_result = this.rateService.getExchangeRate(from, to);
        int statusCode = service_result.getStatusCode();

        if (statusCode == 200) {
            return new ResponseEntity<>("Exchange Rate: " + service_result.getResult(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(service_result.getMessage(), HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "Retrieve all exchange rates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Exchange Rates"),
            @ApiResponse(responseCode = "400", description = "Unsuccessful request", content = @Content),
            @ApiResponse(responseCode = "502", description = "Error connecting to external API", content = @Content),
    })

    @GetMapping(path = "getAllExchangeRates/")
    public ResponseEntity<String> getAllExchangeRates(@RequestParam("base") String base) throws IOException {
        ServiceResponseDto service_result = this.rateService.getAllExchangeRates(base);
        int statusCode = service_result.getStatusCode();

        if (statusCode == 200) {
            String formattedResults = service_result.getResult().toString().replaceAll("[{}]", "").replace(",", "\n");
            return new ResponseEntity<>(
                    formattedResults,
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(service_result.getMessage(), HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "Convert amount to currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Converted amount"),
            @ApiResponse(responseCode = "400", description = "Unsuccessful request", content = @Content),
            @ApiResponse(responseCode = "502", description = "Error connecting to external API", content = @Content),
    })

    @GetMapping(path = "getConvertedAmount/")
    public ResponseEntity<String> getConvertedAmount(@RequestParam("from") String from, @RequestParam("to") String to,
            @RequestParam("amount") int amount) throws IOException {

        ServiceResponseDto service_result = this.rateService.getConvertedAmount(from, to, amount);
        int statusCode = service_result.getStatusCode();

        if (statusCode == 200) {
            return new ResponseEntity<>("Converted amount: " + service_result.getResult(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(service_result.getMessage(), HttpStatus.valueOf(statusCode));
        }
    }

    @Operation(summary = "Convert amount to requested currencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Converted amount"),
            @ApiResponse(responseCode = "400", description = "Unsuccessful request", content = @Content),
            @ApiResponse(responseCode = "502", description = "Error connecting to external API", content = @Content),
    })

    @GetMapping(path = "getConvertedAmountToAllCurr/")
    public ResponseEntity<String> getConvertedAmountToCurrs(@RequestParam("from") String from,
            @RequestParam("to") List<String> to,
            @RequestParam("amount") int amount) throws IOException {
        ServiceResponseDto service_result = this.rateService.getConvertedAmountToAllCurr(from, to, amount);
        int statusCode = service_result.getStatusCode();

        if (statusCode == 200) {
            String formattedResults = service_result.getResult().toString().replaceAll("[{}]", "").replace(",", "\n");
            return new ResponseEntity<>(
                    formattedResults,
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(service_result.getMessage(), HttpStatus.valueOf(statusCode));
        }
    }

}
