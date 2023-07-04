package com.zxf.example.document.checker;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;

@Slf4j
public class PdfDocumentChecker implements DocumentChecker {
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
        return true;
    }

    private Boolean hasSafeContent(BufferedInputStream stream) throws Exception {
        stream.reset();

        PdfReader pdfReader = new PdfReader(stream);
        if (pdfReader.getJavaScript() != null) {
            return false;
        }

        PdfDictionary names = pdfReader.getCatalog().getAsDict(PdfName.NAMES);
        if (names == null) {
            return true;
        }

        PdfArray embeddedFileNames = names.getAsDict(PdfName.EMBEDDEDFILES).getAsArray(PdfName.NAMES);
        if (embeddedFileNames != null && !embeddedFileNames.isEmpty()) {
            return false;
        }

        return true;
    }
}
