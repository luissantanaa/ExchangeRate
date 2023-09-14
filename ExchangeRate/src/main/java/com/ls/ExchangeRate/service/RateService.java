package com.ls.ExchangeRate.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.ls.ExchangeRate.dto.ServiceResponseDto;

import jakarta.annotation.PostConstruct;

@Service
public class RateService {
    private List<String> availableRates;

    @PostConstruct
    public void init() throws FileNotFoundException, IOException {
        String url_str = "https://api.exchangerate.host/symbols";
        Map<String, HttpURLConnection> connection;
        HttpURLConnection request;

        connection = getConnection(url_str);
        request = connection.get("connection");

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

    public ServiceResponseDto getExchangeRate(String from, String to) throws IOException {
        String url_str = String.format("https://api.exchangerate.host/convert?from=%s&to=%s", from, to);
        Map<String, HttpURLConnection> connection;
        HttpURLConnection request;
        ServiceResponseDto response;

        if (!availableRates.contains(from) || !availableRates.contains(to)) {
            response = ServiceResponseDto.builder().statusCode(400).message("Base or Final currency not found")
                    .result(null)
                    .build();
            return response;
        }

        connection = getConnection(url_str);
        request = connection.get("connection");

        if (request == null) {
            response = ServiceResponseDto.builder().statusCode(502).message("Error connecting to external API")
                    .result(null)
                    .build();
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

                response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                        .message("Successful request")
                        .result(result)
                        .build();

                return response;
            }
        } catch (IOException e) {

            response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                    .message("Error processing request response")
                    .result(null)
                    .build();
            return response;
        }

        response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                .message("Error")
                .result(null)
                .build();

        return response;
    }

    public ServiceResponseDto getAllExchangeRates(String base) throws IOException {
        String url_str = String.format("https://api.exchangerate.host/latest?base=%s", base);
        Map<String, HttpURLConnection> connection;
        HttpURLConnection request;
        ServiceResponseDto response;

        if (!availableRates.contains(base)) {
            response = ServiceResponseDto.builder().statusCode(400).message("Base currency not found")
                    .result(null)
                    .build();
            return response;
        }

        connection = getConnection(url_str);
        request = connection.get("connection");

        if (request == null) {
            response = ServiceResponseDto.builder().statusCode(502).message("Error connecting to external API")
                    .result(null)
                    .build();
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

                response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                        .message("Successful request")
                        .result(result)
                        .build();

                return response;
            }
        } catch (IOException e) {

            response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                    .message("Error processing request response")
                    .result(null)
                    .build();
            return response;
        }

        response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                .message("Error")
                .result(null)
                .build();

        return response;
    }

    public ServiceResponseDto getConvertedAmount(String from, String to, int amount) throws IOException {
        String url_str = String.format("https://api.exchangerate.host/convert?from=%s&to=%s&amount=%d", from, to,
                amount);
        Map<String, HttpURLConnection> connection;
        HttpURLConnection request;
        ServiceResponseDto response;

        if (!availableRates.contains(from) || !availableRates.contains(to) || !(amount > 0)) {
            response = ServiceResponseDto.builder().statusCode(400)
                    .message("Base or Final currency must exist and Amount must be over 0")
                    .result(null)
                    .build();
            return response;
        }

        connection = getConnection(url_str);
        request = connection.get("connection");

        if (request == null) {
            response = ServiceResponseDto.builder().statusCode(502).message("Error connecting to external API")
                    .result(null)
                    .build();
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

                response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                        .message("Successful request")
                        .result(result)
                        .build();

                return response;
            }
        } catch (IOException e) {

            response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                    .message("Error processing request response")
                    .result(null)
                    .build();
            return response;
        }

        response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                .message("Error")
                .result(null)
                .build();

        return response;
    }

    public ServiceResponseDto getConvertedAmountToAllCurr(String from, List<String> to, int amount) throws IOException {
        String finalCurrencies = String.join(",", to);

        String url_str = String.format("https://api.exchangerate.host/latest?base=%s&symbols=%s&amount=%d", from,
                finalCurrencies, amount);

        Map<String, HttpURLConnection> connection;
        HttpURLConnection request;
        ServiceResponseDto response;

        boolean validToRates = to.stream().allMatch(elem -> availableRates.contains(elem));

        if (!availableRates.contains(from) || !validToRates || !(amount > 0)) {
            response = ServiceResponseDto.builder().statusCode(400)
                    .message("Base or Final currency must exist and Amount must be over 0")
                    .result(null)
                    .build();
            return response;
        }

        connection = getConnection(url_str);
        request = connection.get("connection");

        if (request == null) {
            response = ServiceResponseDto.builder().statusCode(502).message("Error connecting to external API")
                    .result(null)
                    .build();
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
                response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                        .message("Successful request")
                        .result(result)
                        .build();

                return response;
            }
        } catch (IOException e) {

            response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                    .message("Error processing request response")
                    .result(null)
                    .build();
            return response;
        }

        response = ServiceResponseDto.builder().statusCode(request.getResponseCode())
                .message("Error")
                .result(null)
                .build();

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
}
