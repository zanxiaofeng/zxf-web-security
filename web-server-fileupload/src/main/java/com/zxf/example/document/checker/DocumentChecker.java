package com.zxf.example.document.checker;

import com.aspose.words.FileFormatInfo;
import com.aspose.words.FileFormatUtil;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public interface DocumentChecker {
    boolean isSafe(ByteArrayInputStream inputStream, String fileName, FileFormatInfo fileFormatInfo);

    Map<String, DocumentChecker> documentCheckers = new HashMap<>();

    static void register(String type, DocumentChecker documentChecker) {
        documentCheckers.put(type, documentChecker);
    }

    static boolean isSafeDocument(InputStream inputStream, String fileName) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
        FileFormatInfo fileFormatInfo = FileFormatUtil.detectFileFormat(byteArrayInputStream);
        for (DocumentChecker documentChecker : documentCheckers.values()) {
            if (documentChecker.isSafe(byteArrayInputStream, fileName, fileFormatInfo)) {
                return true;
            }
        }
        return false;
    }
}
