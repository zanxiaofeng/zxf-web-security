package com.zxf.example.controllers.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class FileUploadForm {

    /** 所有文件共享的附加数据 */
    private String name;
    private String email;

    /**
     * 每个文件独立的数据，通过索引参数绑定：files[0].file / files[0].category / files[0].tags
     */
    private List<FileItem> files = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @ToString
    public static class FileItem {
        // 未选择文件的input：part可能绑定为空MultipartFile，也可能整个缺失
        private MultipartFile file;
        private String category;
        private List<String> tags = new ArrayList<>();
    }
}
