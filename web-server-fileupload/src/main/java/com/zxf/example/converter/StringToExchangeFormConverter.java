package com.zxf.example.converter;

import tools.jackson.databind.ObjectMapper;
import com.zxf.example.controllers.model.ExchangeForm;
import org.springframework.core.convert.converter.Converter;

public class StringToExchangeFormConverter implements Converter<String, ExchangeForm.Request> {
    @Override
    public ExchangeForm.Request convert(String source) {
        return new ObjectMapper().readValue(source, ExchangeForm.Request.class);
    }
}
