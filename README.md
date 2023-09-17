# ExchangeRate

## How to run

Navigate to the directory containg the pom.xml file and run:
  - ./mvnw spring-boot:run

Or use the jar file present in the repository with command:
  - java -jar .\ExchangeRate-0.0.1-SNAPSHOT.jar

## Description

This repository contains a Springboot application that allows the user to get the exchange rate or convert a given amount from currency A to B or multiple currencies. An external API [(exchangerate.host)
](https://exchangerate.host/#/) is used to carry out the tasks described before.

This application contains the following endpoints:
  - **http://localhost:8080/api/rates/getExchangeRate/**

    -  Example: http://localhost:8080/api/rates/getExchangeRate/?from=EUR&to=USD
  - **http://localhost:8080/api/rates/getConvertedAmountToAllCurr/**
  
    -  Example: http://localhost:8080/api/rates/getConvertedAmountToAllCurr/?from=USD&to=EUR&to=CZK&amount=10
  - **http://localhost:8080/api/rates/getConvertedAmount/**

    -  Example: http://localhost:8080/api/rates/getConvertedAmount/?from=EUR&to=USD&amount=100 
  - **http://localhost:8080/api/rates/getAllExchangeRates/**

    -  Example: http://localhost:8080/api/rates/getAllExchangeRates/?base=EUR   
  - **http://localhost:8080/**

This final endpoint contains a swagger UI page that allows a user to test the exposed endpoints.

## Rate Controller
This controller takes care of calling the service methods for each request and generates the response based on the results obtained.

```
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
            @ApiResponse(responseCode = "200", description = "Successful request"),
            @ApiResponse(responseCode = "400", description = "Base or Final currency not found", content = @Content),
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
            @ApiResponse(responseCode = "200", description = "Successful request"),
            @ApiResponse(responseCode = "400", description = "Base currency not found", content = @Content),
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
            @ApiResponse(responseCode = "200", description = "Successful request"),
            @ApiResponse(responseCode = "400", description = "Base or Final currency must exist and Amount must be over 0", content = @Content),
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
            @ApiResponse(responseCode = "200", description = "Successful request"),
            @ApiResponse(responseCode = "400", description = "Base or Final currency must exist and Amount must be over 0", content = @Content),
            @ApiResponse(responseCode = "502", description = "Error connecting to external API", content = @Content),
    })

    @GetMapping(path = "getConvertedAmountToAllCurr/")
    public ResponseEntity<String> getConvertedAmountToAllCurr(@RequestParam("from") String from,
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
```

## Rate Service

This service executes the necessary calls to the external API. The init method runs on application startup and populates availableRates with the available currency codes that can be used for the requests. This list is used throughout the service in order to verify the validity of the passed currency codes by the user. In the event that the call to the external API fails, availableRates is populated with the currency codes present in a .CSV file.

- The method getExchangeRate, like the name indicates, takes two valid currency codes and returns the given exchange rate.

- The method getAllExchangeRates, like the name indicates, takes a valid base currency and returns the exchange rates for all the other available currencies.

- The method getConvertedAmount, like the name indicates, takes two valid currency codes and an amount. The result is the converted amount.

- The method getConvertedAmountToAllCurr, like the name indicates, takes a valid base currency, a list of other currencies and an amount. The result is the converted amount in all the required currencies.

- The methods getConnection and serviceResponseDtoBuilder are auxiliary functions. getConnection tries to connect to the external API and returns a Map<String, HttpURLConnection> with a "connection" key which the value can either be a valid connection or null. serviceResponseDtoBuilder is used to create the serviceResponseDto which is the result passed from the service to the controller.

After successfully establishing a connection and, if needed, verifing the validity of the user inputs, the external API response is collected and transformed into a string called jsonResponse. This string is then used to create a JSONObject from which we can extract the needed data. This process is fairly similar in all methods. From this data we can create the ServiceResponseDto and pass the result back to the rate controller.

All the non-auxiliary methods implement caches for the results of the given requests. This process allows to minimize calls to the external API as it returns the result of a known request without actually executing the called method.
```
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
```

## Service Response Dto

This is a simple data transfer object which is passed from the service to the controller. These objects contain the statusCode of the request to the external API, a message detailing an error or success case and an object containg the actual result of the request.

```
@Getter
@Builder
public class ServiceResponseDto {
    int statusCode;
    String message;
    Object result;
}
```
## Unit tests

The application contains tests for the service and controller modules. It uses frameworks such as JUnit and Mockito. These tests contain sucess and fail cases for all the methods present in both modules and can be run with:
  - mvn test

Rate Controller Test examples:
```
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
```

Rate Service Test examples:
```
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
```
