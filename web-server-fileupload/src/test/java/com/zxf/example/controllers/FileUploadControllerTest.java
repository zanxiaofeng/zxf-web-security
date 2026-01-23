package com.zxf.example.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUploadControllerTest {

    @Test
    void testUploadMultiFile_withMultipleFiles() throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", new FileSystemResource(createTempFile(100 * 1024)));
        body.add("files", new FileSystemResource(createTempFile(200 * 1024)));
        body.add("files", new FileSystemResource(createTempFile(300 * 1024)));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        for (int i = 0; i < 10000; i++) {
            String requestUrl = "http://localhost:8085/uploadMultiFile";
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = new RestTemplate().postForEntity(requestUrl, requestEntity, String.class);
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
