package com.ls.ExchangeRate.rates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RateService {

    public Map<String, String> getExchangeRate(String from, String to) throws IOException {
        String url_str = String.format("https://api.exchangerate.host/convert?from=%s&to=%s", from, to);
        HttpURLConnection request = null;
        Map<String, String> serviceResponseMap = new HashMap<String, String>();

        try {
            URL url = new URL(url_str);
            request = (HttpURLConnection) url.openConnection();
            request.connect();

        } catch (Exception e) {
            serviceResponseMap.put("statusCode", "500");
            serviceResponseMap.put("message", "Error connecting request");
            return serviceResponseMap;
        }

        try {
            if (String.valueOf(request.getResponseCode()).startsWith("2")) {
                String jsonResponse = new BufferedReader(new InputStreamReader((InputStream) request.getContent()))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject obj = new JSONObject(jsonResponse);

                if (obj.get("result").toString() == "null") {
                    serviceResponseMap.put("statusCode", "400");
                    serviceResponseMap.put("message", "Unsuccessful request");
                    return serviceResponseMap;
                }

                String result = String.valueOf(obj.getDouble("result"));

                serviceResponseMap.put("statusCode", String.valueOf(request.getResponseCode()));
                serviceResponseMap.put("Exchange Rate", result);
                return serviceResponseMap;
            }
        } catch (IOException e) {
            serviceResponseMap.put("statusCode", String.valueOf(request.getResponseCode()));
            serviceResponseMap.put("message", "Error processing request response");
            return serviceResponseMap;
        }

        serviceResponseMap.put("statusCode", String.valueOf(request.getResponseCode()));
        serviceResponseMap.put("message", "Error");
        return serviceResponseMap;
    }
}
