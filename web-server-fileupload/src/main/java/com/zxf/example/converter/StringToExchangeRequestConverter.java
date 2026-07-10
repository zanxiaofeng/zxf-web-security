package com.zxf.example.converter;

import tools.jackson.databind.ObjectMapper;
import com.zxf.example.controllers.model.ExchangeRequest;
import org.springframework.core.convert.converter.Converter;

public class StringToExchangeRequestConverter implements Converter<String, ExchangeRequest.Request> {
    @Override
    public ExchangeRequest.Request convert(String source) {
        return new ObjectMapper().readValue(source, ExchangeRequest.Request.class);
    }
}
