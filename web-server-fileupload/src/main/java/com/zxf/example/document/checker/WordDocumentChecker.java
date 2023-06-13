package com.zxf.example.document.checker;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WordDocumentChecker implements DocumentChecker{

    @Override
    public boolean isSafe(BufferedInputStream inputStream, String fileName) {
        return false;
    }

    private Boolean isAllowedFormat(BufferedInputStream stream) throws IOException {
        stream.reset();

        return false;
    }

    private Boolean hasSafeContent(BufferedInputStream stream) throws IOException {
        stream.reset();

        return false;
    }
}
