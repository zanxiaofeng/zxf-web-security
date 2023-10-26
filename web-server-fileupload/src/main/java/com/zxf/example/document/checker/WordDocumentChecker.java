package com.zxf.example.document.checker;

import com.aspose.words.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Component
public class WordDocumentChecker implements DocumentChecker {
    private static final List<String> ALLOWED_FORMATS = Arrays.asList(".doc", ".docx", ".dot");

    public WordDocumentChecker() {
        DocumentChecker.register("word", this);
    }

    @Override
    public boolean isSafe(ByteArrayInputStream inputStream, String fileName, FileFormatInfo fileFormatInfo) {
        try {
            if (!isAllowedFormat(fileFormatInfo)) {
                return false;
            }

            return hasSafeContent(inputStream);
        } catch (Exception ex) {
            log.error("Exception during World file analysis.", ex);
            return false;
        }
    }

    private Boolean isAllowedFormat(FileFormatInfo fileFormatInfo) throws Exception {
        String formatExtension = FileFormatUtil.loadFormatToExtension(fileFormatInfo.getLoadFormat());
        return ALLOWED_FORMATS.contains(formatExtension);
    }

    private Boolean hasSafeContent(ByteArrayInputStream stream) throws Exception {
        stream.reset();

        Document document = new Document(stream);
        if (document.hasMacros()) {
            return false;
        }

        NodeCollection shapes = document.getChildNodes(NodeType.SHAPE, true);
        for (int i = 0; i < shapes.getCount(); i++) {
            if (((Shape) shapes.get(i)).getOleFormat() != null) {
                return false;
            }
        }

        return true;
    }
}
