package com.zxf.example.controllers;

import com.zxf.example.controllers.model.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
public class ExchangeController {
    @PostMapping("/exchange")
    public String exchange(@Validated @RequestPart Request request, @RequestPart(required = false) MultipartFile[] attachments) throws IOException {
        log.info("exchange, request={}, attachments={}", request, attachments);
        for (int i = 0; i < attachments.length; i++) {
            log.info("{}, {}, {}, {}, {}", attachments[i].getName(), attachments[i].getOriginalFilename(), attachments[i].getContentType(),
                    attachments[i].getSize(), new String(attachments[i].getBytes()));
        }
        return request.toString() + "::" + attachments.length;
    }

    @GetMapping("/exchange/test")
    public String exchangeTest() {
        log.info("exchangeTest");
        Thread thread = new Thread(() -> {
            log.info("Start");
            Request request = new Request();
            request.setTo("test@163.com");
            request.setSubject("Test");
            request.setContent("Hi, Davis ZAN");


            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            multipartBodyBuilder.part("request", request, MediaType.APPLICATION_JSON);
            multipartBodyBuilder.part("attachments", "Hello Davis", MediaType.TEXT_PLAIN).filename("attachemend-1.txt");
            multipartBodyBuilder.part("attachments", "Hello Zan", MediaType.TEXT_PLAIN).filename("attachemend-2.txt");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, HttpEntity<?>>> multipartRequestEntity = new HttpEntity<>(multipartBodyBuilder.build(), headers);

            RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
            restTemplate.exchange("http://localhost:8085/exchange", HttpMethod.POST, multipartRequestEntity, String.class).getBody();
            log.info("End");
        });
        thread.start();
        return "OK";
    }
}
