package com.zxf.example.document.checker;

import com.aspose.words.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class WordDocumentChecker implements DocumentChecker {
    private static final List<String> ALLOWED_FORMAT = Arrays.asList("doc", "docx", "dot");

    @Override
    public boolean isSafe(BufferedInputStream inputStream, String fileName) {
        try {
            if (!isAllowedFormat(inputStream)) {
                return false;
            }

            return hasSafeContent(inputStream);
        } catch (Exception ex) {
            log.error("Exception during World file analysis.", ex);
            return false;
        }
    }

    private Boolean isAllowedFormat(BufferedInputStream stream) throws Exception {
        stream.reset();

        FileFormatInfo fileFormatInfo = FileFormatUtil.detectFileFormat(stream);
        String formatExtension = FileFormatUtil.loadFormatToExtension(fileFormatInfo.getLoadFormat());
        return ALLOWED_FORMAT.contains(formatExtension);
    }

    private Boolean hasSafeContent(BufferedInputStream stream) throws Exception {
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
