package com.zxf.example.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxf.example.controllers.model.ExchangeRequest;
import org.springframework.core.convert.converter.Converter;

public class StringToExchangeRequestConverter implements Converter<String, ExchangeRequest.Request> {
    @Override
    public ExchangeRequest.Request convert(String source) {
        try {
            return (ExchangeRequest.Request) new ObjectMapper().readValue(source, ExchangeRequest.Request.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
