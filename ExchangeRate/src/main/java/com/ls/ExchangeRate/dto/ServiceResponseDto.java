package com.ls.ExchangeRate.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceResponseDto {
    int statusCode;
    String message;
    Object result;
}