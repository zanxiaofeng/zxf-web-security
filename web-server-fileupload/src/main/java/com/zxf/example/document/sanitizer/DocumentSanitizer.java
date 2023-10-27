package com.zxf.example.document.sanitizer;

import com.opencsv.exceptions.CsvException;
import com.zxf.example.document.checker.DocumentChecker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public interface DocumentSanitizer {
    public byte[] sanitize(ByteArrayInputStream inputStream) throws IOException, CsvException;

    Map<Class<? extends DocumentSanitizer>, DocumentSanitizer> documentSanitizers = new HashMap<>();

    static void register(Class<? extends DocumentSanitizer> type, DocumentSanitizer documentSanitizer) {
        documentSanitizers.put(type, documentSanitizer);
    }

    static DocumentSanitizer getDocumentSanitizer(Class<? extends DocumentSanitizer> type) {
        return documentSanitizers.get(type);
    }
}
