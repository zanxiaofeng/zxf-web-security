package com.zxf.example.controllers;

import com.zxf.example.controllers.model.ExchangeForm;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ExchangeController {
    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/exchange1")
    public String exchange1(@Validated @ModelAttribute ExchangeForm request) throws IOException {
        log.info("exchange1, request={}", request);
        List<ExchangeForm.Attachment> attachments = request.getAttachments() == null ? List.of() : request.getAttachments();
        for (ExchangeForm.Attachment attachment : attachments) {
            MultipartFile file = attachment.getFile();
            log.info("{}, {}, {}, {}, {}, category={}, tags={}", file.getName(), file.getOriginalFilename(), file.getContentType(),
                    file.getSize(), new String(file.getBytes()), attachment.getCategory(), attachment.getTags());
        }
        return request.toString() + "::" + attachments.size();
    }

    @PostMapping("/exchange2")
    public String exchange2(@Validated @RequestPart ExchangeForm.Request request, @RequestPart(required = false) MultipartFile[] files) throws IOException {
        log.info("exchange2, request={}, files={}", request, files);
        // B1: 元数据随 request JSON part 传入, 按 filename 与文件 part 关联
        Map<String, ExchangeForm.AttachmentMeta> metasByFilename = request.getAttachments() == null ? Map.of() :
                request.getAttachments().stream().collect(Collectors.toMap(ExchangeForm.AttachmentMeta::getFilename, Function.identity()));
        int count = 0;
        if (files != null) {
            for (MultipartFile file : files) {
                ExchangeForm.AttachmentMeta meta = metasByFilename.get(file.getOriginalFilename());
                if (meta == null) {
                    log.warn("No attachment meta found for {}", file.getOriginalFilename());
                }
                log.info("{}, {}, {}, {}, {}, category={}, tags={}", file.getName(), file.getOriginalFilename(), file.getContentType(),
                        file.getSize(), new String(file.getBytes()), meta == null ? null : meta.getCategory(),
                        meta == null ? null : meta.getTags());
            }
            count = files.length;
        }
        return request.toString() + "::" + count;
    }

    @GetMapping("/exchange1/test")
    public String exchange1Test(@RequestParam(required = false) String to) {
        log.info("exchange1Test");
        Thread thread = new Thread(() -> {
            log.info("Start Exchange-1 Test");

            ExchangeForm.Request request = new ExchangeForm.Request(to, "Test Exchange 1", "Hi, Davis ZAN", null);

            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            multipartBodyBuilder.part("request", request, MediaType.APPLICATION_JSON);
            // 索引绑定: part 名对应属性路径 attachments[i].file / attachments[i].category / attachments[i].tags[j]
            multipartBodyBuilder.part("attachments[0].file", "Hello Davis", MediaType.TEXT_PLAIN).filename("attachment-1.txt");
            multipartBodyBuilder.part("attachments[0].category", "invoice", MediaType.TEXT_PLAIN);
            multipartBodyBuilder.part("attachments[0].tags[0]", "2026", MediaType.TEXT_PLAIN);
            multipartBodyBuilder.part("attachments[0].tags[1]", "finance", MediaType.TEXT_PLAIN);
            multipartBodyBuilder.part("attachments[1].file", "Hello Zan", MediaType.TEXT_PLAIN).filename("attachment-2.txt");
            multipartBodyBuilder.part("attachments[1].category", "contract", MediaType.TEXT_PLAIN);
            multipartBodyBuilder.part("attachments[1].tags[0]", "legal", MediaType.TEXT_PLAIN);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, HttpEntity<?>>> multipartRequestEntity = new HttpEntity<>(multipartBodyBuilder.build(), headers);

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

            ExchangeForm.Request request = new ExchangeForm.Request(to, "Test Exchange 2", "Hi, Davis ZAN", List.of(
                    new ExchangeForm.AttachmentMeta("attachment-1.txt", "invoice", List.of("2026", "finance")),
                    new ExchangeForm.AttachmentMeta("attachment-2.txt", "contract", List.of("legal"))));

            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            multipartBodyBuilder.part("request", request, MediaType.APPLICATION_JSON);
            multipartBodyBuilder.part("files", "Hello Davis", MediaType.TEXT_PLAIN).filename("attachment-1.txt");
            multipartBodyBuilder.part("files", "Hello Zan", MediaType.TEXT_PLAIN).filename("attachment-2.txt");

            restTemplate.postForObject("http://localhost:8085/exchange2", multipartBodyBuilder.build(), String.class);
            log.info("End Exchange-2 Test");
        });
        thread.start();
        return "OK";
    }
}
