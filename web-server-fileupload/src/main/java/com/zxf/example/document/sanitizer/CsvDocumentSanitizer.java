package com.zxf.example.document.sanitizer;

import java.io.InputStream;

public class CsvDocumentSanitizer implements DocumentSanitizer {
    @Override
    public byte[] sanitize(InputStream inputStream) {
        return new byte[0];
    }
}
