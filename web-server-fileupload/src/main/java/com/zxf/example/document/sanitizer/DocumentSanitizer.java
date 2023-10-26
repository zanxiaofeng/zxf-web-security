package com.zxf.example.document.sanitizer;

import java.io.InputStream;

public interface DocumentSanitizer {
    public byte[] sanitize(InputStream inputStream);
}
