package com.zxf.example.document.checker;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface DocumentChecker {
    boolean isSafe(ByteArrayInputStream inputStream, String fileExtension);

    Map<Class<? extends DocumentChecker>, DocumentChecker> documentCheckers = new ConcurrentHashMap<>();

    static void register(Class<? extends DocumentChecker> type, DocumentChecker documentChecker) {
        documentCheckers.put(type, documentChecker);
    }

    static DocumentChecker getDocumentChecker(Class<? extends DocumentChecker> type) {
        return documentCheckers.get(type);
    }
}
