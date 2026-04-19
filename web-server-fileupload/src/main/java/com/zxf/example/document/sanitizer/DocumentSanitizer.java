package com.zxf.example.document.sanitizer;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface DocumentSanitizer {
    byte[] sanitize(ByteArrayInputStream inputStream) throws Exception;

    Map<Class<? extends DocumentSanitizer>, DocumentSanitizer> documentSanitizers = new ConcurrentHashMap<>();

    static void register(Class<? extends DocumentSanitizer> type, DocumentSanitizer documentSanitizer) {
        documentSanitizers.put(type, documentSanitizer);
    }

    static DocumentSanitizer getDocumentSanitizer(Class<? extends DocumentSanitizer> type) {
        return documentSanitizers.get(type);
    }
}
