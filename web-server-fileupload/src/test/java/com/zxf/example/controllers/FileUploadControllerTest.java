package com.zxf.example.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestRestTemplate
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

    @Test
    void uploadFilesWithAdditionalData() throws Exception {
        Path file1 = createTempFile(10 * 1024);
        Path file2 = createTempFile(20 * 1024);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", "Alice");
        body.add("email", "alice@example.com");
        body.add("files[0].file", new FileSystemResource(file1));
        body.add("files[0].category", "document");
        body.add("files[0].tags", "urgent");
        body.add("files[0].tags", "final");
        body.add("files[1].file", new FileSystemResource(file2));
        body.add("files[1].category", "photo");
        // 故意不发 files[1].tags，验证 tags 为空时绑定与渲染都正常

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<String> response = testRestTemplate.postForEntity("/uploadFilesWithAdditionalData",
                new HttpEntity<>(body, headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Alice", "alice@example.com",
                "document", "urgent", "final", "photo",
                file1.getFileName().toString(), file2.getFileName().toString());
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
