package com.ls.ExchangeRate.rates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RateService {

    public ServiceResponse getExchangeRate(String from, String to) throws IOException {
        String url_str = String.format("https://api.exchangerate.host/convert?from=%s&to=%s", from, to);
        HttpURLConnection request = null;
        ServiceResponse response;

        try {
            URL url = new URL(url_str);
            request = (HttpURLConnection) url.openConnection();
            request.connect();

        } catch (Exception e) {
            response = ServiceResponse.builder().statusCode(502).message("Error connecting to external API")
                    .result(null)
                    .build();
            return response;
        }

        try {
            if (request.getResponseCode() == 200) {
                String jsonResponse = new BufferedReader(new InputStreamReader((InputStream) request.getContent()))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject obj = new JSONObject(jsonResponse);

                if (obj.get("result").toString() == "null") {
                    response = ServiceResponse.builder().statusCode(400).message("Unsuccessful request")
                            .result(null)
                            .build();

                    return response;
                }

                Object result = obj.get("result");

                response = ServiceResponse.builder().statusCode(request.getResponseCode()).message("Successful request")
                        .result(result)
                        .build();

                return response;
            }
        } catch (IOException e) {

            response = ServiceResponse.builder().statusCode(request.getResponseCode())
                    .message("Error processing request response")
                    .result(null)
                    .build();
            return response;
        }

        response = ServiceResponse.builder().statusCode(request.getResponseCode())
                .message("Error")
                .result(null)
                .build();

        return response;
    }

    public ServiceResponse getAllExchangeRates(String base) throws IOException {
        String url_str = String.format("https://api.exchangerate.host/latest?base=%s", base);
        HttpURLConnection request = null;
        ServiceResponse response;

        try {
            URL url = new URL(url_str);
            request = (HttpURLConnection) url.openConnection();
            request.connect();

        } catch (Exception e) {
            response = ServiceResponse.builder().statusCode(502).message("Error connecting to external API")
                    .result(null)
                    .build();
            return response;
        }

        try {
            if (request.getResponseCode() == 200) {
                String jsonResponse = new BufferedReader(new InputStreamReader((InputStream) request.getContent()))
                        .lines()
                        .collect(Collectors.joining("\n"));

                // TODO verificar se é usada a base default
                JSONObject obj = new JSONObject(jsonResponse);

                if (obj.get("rates").toString() == "null") {
                    response = ServiceResponse.builder().statusCode(400).message("Unsuccessful request")
                            .result(null)
                            .build();

                    return response;
                }

                Object result = obj.get("rates");

                response = ServiceResponse.builder().statusCode(request.getResponseCode()).message("Successful request")
                        .result(result)
                        .build();

                return response;
            }
        } catch (IOException e) {

            response = ServiceResponse.builder().statusCode(request.getResponseCode())
                    .message("Error processing request response")
                    .result(null)
                    .build();
            return response;
        }

        response = ServiceResponse.builder().statusCode(request.getResponseCode())
                .message("Error")
                .result(null)
                .build();

        return response;
    }

}