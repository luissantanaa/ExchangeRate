package com.ls.ExchangeRate.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ls.ExchangeRate.ExchangeRateApplication;
import com.ls.ExchangeRate.dto.ServiceResponseDto;

import jakarta.annotation.PostConstruct;

@Service
public class RateService {
    private List<String> availableRates;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateApplication.class);

    @PostConstruct
    public void init() throws FileNotFoundException, IOException {
        String url_str = "https://api.exchangerate.host/symbols";
        Map<String, HttpURLConnection> connection;
        HttpURLConnection request;

        connection = getConnection(url_str);
        request = connection.get("connection");

        LOGGER.info("INIT RATE SERVICE");

        if (request != null) {
            try {
                request.connect();

                if (request.getResponseCode() == 200) {
                    String jsonResponse = new BufferedReader(new InputStreamReader((InputStream) request.getContent()))
                            .lines()
                            .collect(Collectors.joining("\n"));

                    JSONObject obj = new JSONObject(jsonResponse);

                    if (obj.getJSONObject("symbols") != null) {
                        availableRates = new ArrayList<>(obj.getJSONObject("symbols").toMap().keySet());
                    }

                }
            } catch (IOException e) {
                LOGGER.warn("REQUEST ERROR. USING FALLBACK CURRENCY LIST");

                File file = new File("src/main/resources/static/rates.csv");
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(",");
                        availableRates = Arrays.asList(values);
                    }
                }

            }
        }
    }

    @Cacheable("exchangeRate")
    public ServiceResponseDto getExchangeRate(String from, String to) throws IOException {
        String url_str = String.format("https://api.exchangerate.host/convert?from=%s&to=%s", from, to);
        Map<String, HttpURLConnection> connection;
        HttpURLConnection request;
        ServiceResponseDto response;

        LOGGER.info("GET EXCHANGE RATE REQUEST");

        if (!availableRates.contains(from) || !availableRates.contains(to)) {

            LOGGER.error("INVALID ARGUMENTS");

            response = serviceResponseDtoBuilder(400, "Base or Final currency not found",
                    null);
            return response;
        }

        connection = getConnection(url_str);
        request = connection.get("connection");

        if (request == null) {

            LOGGER.error("ERROR CONNECTING TO EXTERNAL API");

            response = serviceResponseDtoBuilder(502, "Error connecting to external API", null);
            return response;
        }

        request.connect();

        try {
            if (request.getResponseCode() == 200) {
                String jsonResponse = new BufferedReader(new InputStreamReader((InputStream) request.getContent()))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject obj = new JSONObject(jsonResponse);

                Object result = obj.get("result");

                response = serviceResponseDtoBuilder(request.getResponseCode(), "Successful request", result);
                return response;
            }
        } catch (IOException e) {

            LOGGER.error("ERROR PROCESSING REQUEST RESPONSE");

            response = serviceResponseDtoBuilder(request.getResponseCode(), "Error processing request response", null);
            return response;
        }

        response = serviceResponseDtoBuilder(request.getResponseCode(), "Error", null);
        return response;
    }

    @Cacheable("exchangeRateList")
    public ServiceResponseDto getAllExchangeRates(String base) throws IOException {
        String url_str = String.format("https://api.exchangerate.host/latest?base=%s", base);
        Map<String, HttpURLConnection> connection;
        HttpURLConnection request;
        ServiceResponseDto response;

        LOGGER.info("GET ALL EXCHANGE RATES REQUEST");

        if (!availableRates.contains(base)) {

            LOGGER.error("INVALID ARGUMENT");

            response = serviceResponseDtoBuilder(400, "Base currency not found",
                    null);
            return response;
        }

        connection = getConnection(url_str);
        request = connection.get("connection");

        if (request == null) {

            LOGGER.error("ERROR CONNECTING TO EXTERNAL API");

            response = serviceResponseDtoBuilder(502, "Error connecting to external API", null);
            return response;
        }

        request.connect();

        try {
            if (request.getResponseCode() == 200) {
                String jsonResponse = new BufferedReader(new InputStreamReader((InputStream) request.getContent()))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject obj = new JSONObject(jsonResponse);

                Object result = obj.get("rates");

                response = serviceResponseDtoBuilder(request.getResponseCode(), "Successful request", result);
                return response;
            }
        } catch (IOException e) {

            LOGGER.error("ERROR PROCESSING REQUEST RESPONSE");

            response = serviceResponseDtoBuilder(request.getResponseCode(), "Error processing request response", null);
            return response;
        }

        response = serviceResponseDtoBuilder(request.getResponseCode(), "Error", null);
        return response;
    }

    @Cacheable("convertedAmount")
    public ServiceResponseDto getConvertedAmount(String from, String to, int amount) throws IOException {
        String url_str = String.format("https://api.exchangerate.host/convert?from=%s&to=%s&amount=%d", from, to,
                amount);
        Map<String, HttpURLConnection> connection;
        HttpURLConnection request;
        ServiceResponseDto response;

        LOGGER.info("GET CONVERTED AMOUNT REQUEST");

        if (!availableRates.contains(from) || !availableRates.contains(to) || !(amount > 0)) {

            LOGGER.error("INVALID ARGUMENTS");

            response = serviceResponseDtoBuilder(400, "Base or Final currency must exist and Amount must be over 0",
                    null);
            return response;
        }

        connection = getConnection(url_str);
        request = connection.get("connection");

        if (request == null) {

            LOGGER.error("ERROR CONNECTING TO EXTERNAL API");

            response = serviceResponseDtoBuilder(502, "Error connecting to external API", null);
            return response;
        }

        request.connect();

        try {
            if (request.getResponseCode() == 200) {
                String jsonResponse = new BufferedReader(new InputStreamReader((InputStream) request.getContent()))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject obj = new JSONObject(jsonResponse);
                Object result = obj.get("result");

                response = serviceResponseDtoBuilder(request.getResponseCode(), "Successful request", result);
                return response;
            }
        } catch (IOException e) {

            LOGGER.error("ERROR PROCESSING REQUEST RESPONSE");

            response = serviceResponseDtoBuilder(request.getResponseCode(), "Error processing request response", null);
            return response;
        }

        response = serviceResponseDtoBuilder(request.getResponseCode(), "Error", null);
        return response;
    }

    @Cacheable("convertedAmountList")
    public ServiceResponseDto getConvertedAmountToAllCurr(String from, List<String> to, int amount) throws IOException {
        String finalCurrencies = String.join(",", to);

        String url_str = String.format("https://api.exchangerate.host/latest?base=%s&symbols=%s&amount=%d", from,
                finalCurrencies, amount);

        Map<String, HttpURLConnection> connection;
        HttpURLConnection request;
        ServiceResponseDto response;

        LOGGER.info("GET MULTIPLE CONVERTED AMOUNT REQUEST");

        boolean validToRates = to.stream().allMatch(elem -> availableRates.contains(elem));

        if (!availableRates.contains(from) || !validToRates || !(amount > 0)) {

            LOGGER.error("INVALID ARGUMENTS");

            response = serviceResponseDtoBuilder(400, "Base or Final currency must exist and Amount must be over 0",
                    null);
            return response;
        }

        connection = getConnection(url_str);
        request = connection.get("connection");

        if (request == null) {

            LOGGER.error("ERROR CONNECTING TO EXTERNAL API");

            response = serviceResponseDtoBuilder(502, "Error connecting to external API", null);
            return response;
        }

        request.connect();

        try {
            if (request.getResponseCode() == 200) {
                String jsonResponse = new BufferedReader(new InputStreamReader((InputStream) request.getContent()))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject obj = new JSONObject(jsonResponse);

                Object result = obj.get("rates");
                response = serviceResponseDtoBuilder(request.getResponseCode(), "Successful request", result);
                return response;
            }
        } catch (IOException e) {

            LOGGER.error("ERROR PROCESSING REQUEST RESPONSE");

            response = serviceResponseDtoBuilder(request.getResponseCode(), "Error processing request response", null);
            return response;
        }

        response = serviceResponseDtoBuilder(request.getResponseCode(), "Error", null);
        return response;
    }

    private Map<String, HttpURLConnection> getConnection(String url_str) {
        HttpURLConnection request = null;
        Map<String, HttpURLConnection> result = new HashMap<String, HttpURLConnection>();

        try {
            URL url = new URL(url_str);
            request = (HttpURLConnection) url.openConnection();
            result.put("connection", request);
            return result;

        } catch (Exception e) {
            result.put("connection", null);
            return result;
        }
    }

    private ServiceResponseDto serviceResponseDtoBuilder(int statusCode, String message, Object result) {
        ServiceResponseDto response = ServiceResponseDto.builder().statusCode(statusCode)
                .message(message)
                .result(result)
                .build();
        return response;
    }
}
