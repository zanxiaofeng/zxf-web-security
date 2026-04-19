package com.zxf.example.document.sanitizer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

@Slf4j
@Component
public class CsvDocumentSanitizer implements DocumentSanitizer {
    private static final List<Character> DANGEROUS_BEGIN_CHARACTERS = List.of('=', '+', '-', '@', '\t', '\r');

    public CsvDocumentSanitizer() {
        DocumentSanitizer.register(CsvDocumentSanitizer.class, this);
    }

    @Override
    public byte[] sanitize(ByteArrayInputStream inputStream) throws Exception {
        inputStream.reset();
        try (Reader reader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReader(reader)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (Writer writer = new OutputStreamWriter(baos);
                 CSVWriter csvWriter = new CSVWriter(writer)) {
                for (String[] record : csvReader.readAll()) {
                    for (int i = 0; i < record.length; i++) {
                        if (needsSanitize(record[i])) {
                            log.debug("Sanitizing CSV cell: [{}] -> [{}]", record[i], "'" + record[i]);
                            record[i] = "'" + record[i];
                        }
                    }
                    csvWriter.writeNext(record);
                }
            }
            return baos.toByteArray();
        }
    }

    private boolean needsSanitize(String cellValue) {
        return !cellValue.isEmpty() && DANGEROUS_BEGIN_CHARACTERS.contains(cellValue.charAt(0));
    }
}
