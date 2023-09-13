package com.ls.ExchangeRate.rates;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping(path = "api/rates/")
public class RateController {
    private final RateService rateService;

    public enum Directions {
        ASC, DESC;
    }

    @Autowired
    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @Operation(summary = "Retrieve exchange rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Exchange Rate"),
            @ApiResponse(responseCode = "404", description = "Error", content = @Content) })
    @GetMapping(path = "getExchangeRate/")
    public ResponseEntity<Map<String, String>> getExchangeRate(@RequestParam("from") String from,
            @RequestParam("to") String to) throws IOException {
        Map<String, String> service_result = this.rateService.getExchangeRate(from, to);

        if (service_result.get("statusCode").startsWith("2")) {
            int statusCode = Integer.parseInt(service_result.get("statusCode"));
            service_result.remove("statusCode");

            return new ResponseEntity<Map<String, String>>(service_result,
                    HttpStatus.valueOf(statusCode));
        } else {
            int statusCode = Integer.parseInt(service_result.get("statusCode"));
            service_result.remove("statusCode");

            return new ResponseEntity<Map<String, String>>(service_result,
                    HttpStatus.valueOf(statusCode));
        }
    }
}
