package com.zxf.example.document.sanitizer;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.interactive.action.PDPageAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PdfDocumentSanitizer implements DocumentSanitizer {
    public PdfDocumentSanitizer() {
        DocumentSanitizer.register(PdfDocumentSanitizer.class, this);
    }

    @Override
    public byte[] sanitize(ByteArrayInputStream inputStream) throws Exception {
        inputStream.reset();
        try (RandomAccessReadBuffer buffer = new RandomAccessReadBuffer(inputStream);
             PDDocument document = Loader.loadPDF(buffer)) {

            if (document.isEncrypted()) {
                throw new SecurityException("Encrypted PDFs are not allowed.");
            }

            sanitizeJavaScriptAndActions(document);
            flattenFormFields(document);
            sanitizeMetadata(document);
            removeEmbeddedFiles(document);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rebuildPDF(document, baos);
            return baos.toByteArray();
        }
    }

    private static void sanitizeJavaScriptAndActions(PDDocument document) throws IOException {
        // Remove document-level open action
        document.getDocumentCatalog().setOpenAction(null);

        // Remove JavaScript from Names dictionary
        var names = document.getDocumentCatalog().getNames();
        if (names != null) {
            names.setJavascript(null);
        }

        // Remove JavaScript actions from form fields
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        if (acroForm != null) {
            for (PDField field : acroForm.getFields()) {
                field.getCOSObject().removeItem(COSName.AA);
            }
        }

        // Clean up page-level actions and link annotations
        for (PDPage page : document.getPages()) {
            PDPageAdditionalActions pageActions = page.getActions();
            if (pageActions != null) {
                pageActions.setC(null);
                pageActions.setO(null);
            }

            List<PDAnnotation> linksToRemove = page.getAnnotations().stream()
                    .filter(ann -> ann instanceof PDAnnotationLink)
                    .collect(Collectors.toList());
            page.getAnnotations().removeAll(linksToRemove);
        }
    }

    public static void flattenFormFields(PDDocument document) throws IOException {
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        if (acroForm != null) {
            acroForm.setNeedAppearances(true);
            acroForm.flatten();
        }
    }

    public static void sanitizeMetadata(PDDocument document) {
        PDDocumentInformation newInfo = new PDDocumentInformation();
        newInfo.setProducer("SafePDFProcessor");
        document.setDocumentInformation(newInfo);

        PDMetadata metadata = document.getDocumentCatalog().getMetadata();
        if (metadata != null) {
            document.getDocumentCatalog().setMetadata(null);
        }
    }

    public static void removeEmbeddedFiles(PDDocument document) {
        var docNames = document.getDocumentCatalog().getNames();
        if (docNames != null && docNames.getEmbeddedFiles() != null) {
            docNames.setEmbeddedFiles(null);
        }
    }

    public static void rebuildPDF(PDDocument document, OutputStream out) throws IOException {
        document.save(out);
    }
}
