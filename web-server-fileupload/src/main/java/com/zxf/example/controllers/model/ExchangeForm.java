package com.zxf.example.controllers.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@ToString
public class ExchangeForm {
    private Request request;
    @Valid
    private List<Attachment> attachments;

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
        /**
         * exchange2 (B1) 的附件元数据: 随 request JSON part 传递,
         * 服务端按 filename 与文件 part 关联, exchange1 不使用该字段
         */
        @Valid
        private List<AttachmentMeta> attachments;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attachment {
        @NotNull
        @ToString.Exclude
        private MultipartFile file;
        @NotBlank
        @Pattern(regexp = "[a-zA-Z0-9_-]+")
        private String category;
        @Size(max = 10)
        private List<@NotBlank @Size(max = 32) String> tags;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentMeta {
        @NotBlank
        private String filename;
        @NotBlank
        @Pattern(regexp = "[a-zA-Z0-9_-]+")
        private String category;
        @Size(max = 10)
        private List<@NotBlank @Size(max = 32) String> tags;
    }
}
