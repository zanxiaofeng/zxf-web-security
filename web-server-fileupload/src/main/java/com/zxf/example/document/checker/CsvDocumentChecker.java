package com.zxf.example.document.checker;

import com.aspose.words.FileFormatInfo;
import com.aspose.words.FileFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CsvDocumentChecker implements DocumentChecker {

    private static final List<String> ALLOWED_FORMATS = Arrays.asList(".txt");

    public CsvDocumentChecker() {
        DocumentChecker.register(CsvDocumentChecker.class, this);
    }

    @Override
    public boolean isSafe(ByteArrayInputStream inputStream, FileFormatInfo fileFormatInfo) {
        try {
            if (!isAllowedFormat(fileFormatInfo)) {
                return false;
            }

            return hasSafeContent(inputStream);
        } catch (Exception ex) {
            log.error("Exception during csv file analysis.", ex);
            return false;
        }
    }

    private Boolean isAllowedFormat(FileFormatInfo fileFormatInfo) throws Exception {
        String formatExtension = FileFormatUtil.loadFormatToExtension(fileFormatInfo.getLoadFormat());
        return ALLOWED_FORMATS.contains(formatExtension);
    }

    private Boolean hasSafeContent(ByteArrayInputStream stream) throws Exception {
        return false;
    }
}
