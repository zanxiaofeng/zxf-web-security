package com.zxf.example.controllers.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class ExchangeRequest {
    private Request request;
    private MultipartFile[] attachments;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        @ToString.Exclude
        private String to;
        @NotBlank
        private String subject;
        @NotBlank
        private String content;
    }
}
