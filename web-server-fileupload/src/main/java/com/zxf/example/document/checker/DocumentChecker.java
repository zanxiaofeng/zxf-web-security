package com.zxf.example.document.checker;

import com.aspose.words.FileFormatInfo;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public interface DocumentChecker {
    boolean isSafe(ByteArrayInputStream inputStream, String fileExtension);

    Map<Class<? extends DocumentChecker>, DocumentChecker> documentCheckers = new HashMap<>();

    static void register(Class<? extends DocumentChecker> type, DocumentChecker documentChecker) {
        documentCheckers.put(type, documentChecker);
    }

    static DocumentChecker getDocumentChecker(Class<? extends DocumentChecker> type) {
        return documentCheckers.get(type);
    }
}
