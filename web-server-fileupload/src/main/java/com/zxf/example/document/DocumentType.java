package com.zxf.example.document;

import com.aspose.words.FileFormatInfo;
import com.aspose.words.FileFormatUtil;
import com.zxf.example.document.checker.*;
import com.zxf.example.document.sanitizer.CsvDocumentSanitizer;
import com.zxf.example.document.sanitizer.DocumentSanitizer;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

@AllArgsConstructor
public enum DocumentType {
    Word("doc,docx", WordDocumentChecker.class, null),
    Pdf("pdf", PdfDocumentChecker.class, null),
    Excel("xls,xlsx", ExcelDocumentChecker.class, null),
    Powerpoint("ppt", PowerpointDocumentChecker.class, null),
    Csv("csv", CsvDocumentChecker.class, CsvDocumentSanitizer.class);

    private String fileExtensions;
    private Class documentChecker;
    private Class documentSanitizer;

    public boolean isSafe(ByteArrayInputStream inputStream) throws Exception {
        FileFormatInfo fileFormatInfo = FileFormatUtil.detectFileFormat(inputStream);
        DocumentChecker myDocumentChecker = DocumentChecker.getDocumentChecker(this.documentChecker);
        if (myDocumentChecker.isSafe(inputStream, fileFormatInfo)) {
            return true;
        }
        return false;
    }

    public boolean canSanitize() {
        return this.documentSanitizer != null;
    }

    public byte[] sanitize(ByteArrayInputStream inputStream) throws Exception {
        DocumentSanitizer myDocumentSanitizer = DocumentSanitizer.getDocumentSanitizer(this.documentSanitizer);
        return myDocumentSanitizer.sanitize(inputStream);
    }

    public static DocumentType getDocumentType(String fileName) {
        String fileExtension = FilenameUtils.getExtension(fileName);
        for (DocumentType documentType : DocumentType.values()) {
            if (Arrays.stream(StringUtils.split(documentType.fileExtensions, ",")).anyMatch(item -> item.equalsIgnoreCase(fileExtension))) {
                return documentType;
            }
        }
        return null;
    }
}
