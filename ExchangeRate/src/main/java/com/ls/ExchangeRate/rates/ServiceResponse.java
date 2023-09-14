package com.ls.ExchangeRate.rates;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ServiceResponse {
    int statusCode;
    String message;
    Object result;
}