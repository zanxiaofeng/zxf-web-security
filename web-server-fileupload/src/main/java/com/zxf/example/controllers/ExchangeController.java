package com.zxf.example.controllers;

import com.zxf.example.controllers.model.ExchangeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
public class ExchangeController {
    @Autowired
    private RestTemplate restTemplate;

    //Validated do not take effective
    @PostMapping("/exchange1")
    public String exchange1(@Validated @ModelAttribute ExchangeRequest request) throws IOException {
        log.info("exchange1, request={}", request);
        for (int i = 0; i < request.getAttachments().length; i++) {
            MultipartFile attachment = request.getAttachments()[i];
            log.info("{}, {}, {}, {}, {}", attachment.getName(), attachment.getOriginalFilename(), attachment.getContentType(),
                    attachment.getSize(), new String(attachment.getBytes()));
        }
        return request.toString() + "::" + request.getAttachments().length;
    }

    @PostMapping("/exchange2")
    public String exchange2(@Validated @RequestPart ExchangeRequest.Request request, @RequestPart(required = false) MultipartFile[] attachments) throws IOException {
        log.info("exchange2, request={}, attachments={}", request, attachments);
        for (int i = 0; i < attachments.length; i++) {
            MultipartFile attachment = attachments[i];
            log.info("{}, {}, {}, {}, {}", attachment.getName(), attachment.getOriginalFilename(), attachment.getContentType(),
                    attachment.getSize(), new String(attachment.getBytes()));
        }
        return request.toString() + "::" + attachments.length;
    }

    @GetMapping("/exchange1/test")
    public String exchange1Test(@RequestParam(required = false) String to) {
        log.info("exchange1Test");
        Thread thread = new Thread(() -> {
            log.info("Start Exchange-1 Test");

            ExchangeRequest.Request request = new ExchangeRequest.Request(to, "Test Exchange 1", "Hi, Davis ZAN");

            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            multipartBodyBuilder.part("request", request, MediaType.APPLICATION_JSON);
            multipartBodyBuilder.part("attachments", "Hello Davis", MediaType.TEXT_PLAIN).filename("attachemend-1.txt");
            multipartBodyBuilder.part("attachments", "Hello Zan", MediaType.TEXT_PLAIN).filename("attachemend-2.txt");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, HttpEntity<?>>> multipartRequestEntity = new HttpEntity<>(multipartBodyBuilder.build());

            restTemplate.exchange("http://localhost:8085/exchange1", HttpMethod.POST, multipartRequestEntity, String.class).getBody();
            log.info("End Exchange-1 Test");
        });
        thread.start();
        return "OK";
    }

    @GetMapping("/exchange2/test")
    public String exchange2Test(@RequestParam(required = false) String to) {
        log.info("exchange2Test");
        Thread thread = new Thread(() -> {
            log.info("Start Exchange-2 Test");

            ExchangeRequest.Request request = new ExchangeRequest.Request(to, "Test Exchange 2", "Hi, Davis ZAN");

            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            multipartBodyBuilder.part("request", request, MediaType.APPLICATION_JSON);
            multipartBodyBuilder.part("attachments", "Hello Davis", MediaType.TEXT_PLAIN).filename("attachemend-1.txt");
            multipartBodyBuilder.part("attachments", "Hello Zan", MediaType.TEXT_PLAIN).filename("attachemend-2.txt");

            restTemplate.postForObject("http://localhost:8085/exchange2", multipartBodyBuilder.build(), String.class);
            log.info("End Exchange-2 Test");
        });
        thread.start();
        return "OK";
    }
}
