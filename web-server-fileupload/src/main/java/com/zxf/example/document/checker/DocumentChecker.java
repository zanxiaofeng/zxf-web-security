package com.zxf.example.document.checker;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentChecker {
    boolean isSafe(MultipartFile file);
}
