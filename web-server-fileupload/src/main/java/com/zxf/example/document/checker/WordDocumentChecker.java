package com.zxf.example.document.checker;

import org.springframework.web.multipart.MultipartFile;

public class WordDocumentChecker implements DocumentChecker{
    @Override
    public boolean isSafe(MultipartFile file) {
        //FileFormatUtil.detectFileFormat();
        return false;
    }
}
