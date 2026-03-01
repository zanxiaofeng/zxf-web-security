package com.zxf.example.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class FileUploadControllerTest {
    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void uploadMultiFileWithMultipleFiles() throws Exception {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", new FileSystemResource(createTempFile(100 * 1024)));
        body.add("files", new FileSystemResource(createTempFile(200 * 1024)));
        body.add("files", new FileSystemResource(createTempFile(300 * 1024)));
        body.add("close", false);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        for (int i = 0; i < 1; i++) {
            String requestUrl = "/uploadMultiFile";
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = testRestTemplate.postForEntity(requestUrl, requestEntity, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            System.out.println("Success--" + i);
        }
    }

    private Path createTempFile(int size) throws IOException {
        Path tempFile = Files.createTempFile("test-my-", ".tmp");
        byte[] content = new byte[size];
        for (int i = 0; i < size; i++) {
            content[i] = (byte) (i % 256);
        }
        Files.write(tempFile, content);
        return tempFile;
    }
}
