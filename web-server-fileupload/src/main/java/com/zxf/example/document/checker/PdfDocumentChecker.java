package com.zxf.example.document.checker;

import com.aspose.words.FileFormatInfo;
import com.aspose.words.FileFormatUtil;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class PdfDocumentChecker implements DocumentChecker {

    private static final List<String> ALLOWED_FORMATS = Arrays.asList(".pdf");

    public PdfDocumentChecker() {
        DocumentChecker.register(PdfDocumentChecker.class, this);
    }

    @Override
    public boolean isSafe(ByteArrayInputStream inputStream, String fileExtension) {
        try {
            FileFormatInfo fileFormatInfo = FileFormatUtil.detectFileFormat(inputStream);
            if (!isAllowedFormat(fileFormatInfo)) {
                return false;
            }

            return hasSafeContent(inputStream);
        } catch (Exception ex) {
            log.error("Exception during PDF file analysis.", ex);
            return false;
        }
    }

    private boolean isAllowedFormat(FileFormatInfo fileFormatInfo) throws Exception {
        String formatExtension = FileFormatUtil.loadFormatToExtension(fileFormatInfo.getLoadFormat());
        return ALLOWED_FORMATS.contains(formatExtension);
    }

    private boolean hasSafeContent(ByteArrayInputStream stream) throws Exception {
        stream.reset();

        PdfReader pdfReader = new PdfReader(stream);
        if (pdfReader.getJavaScript() != null) {
            return false;
        }

        PdfDictionary names = pdfReader.getCatalog().getAsDict(PdfName.NAMES);
        if (names == null) {
            return true;
        }

        PdfDictionary embeddedFilesDict = names.getAsDict(PdfName.EMBEDDEDFILES);
        if (embeddedFilesDict != null) {
            PdfArray embeddedFileNames = embeddedFilesDict.getAsArray(PdfName.NAMES);
            if (embeddedFileNames != null && !embeddedFileNames.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
