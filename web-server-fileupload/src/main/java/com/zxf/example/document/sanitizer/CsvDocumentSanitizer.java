package com.zxf.example.document.sanitizer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CsvDocumentSanitizer implements DocumentSanitizer {
    private List<Character> BEGIN_CHARACTERS = Arrays.asList('=', '+', '-', '@', Character.valueOf((char) 0x09), Character.valueOf((char) 0x0d));

    public CsvDocumentSanitizer() {
        DocumentSanitizer.register(CsvDocumentSanitizer.class, this);
    }

    @Override
    public byte[] sanitize(ByteArrayInputStream inputStream) throws IOException, CsvException {
        inputStream.reset();
        try (Reader inputStreamReader = new InputStreamReader(inputStream); CSVReader csvReader = new CSVReader(inputStreamReader)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (Writer outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream); CSVWriter csvWriter = new CSVWriter(outputStreamWriter)) {
                for (String[] record : csvReader.readAll()) {
                    for (int i = 0; i < record.length; i++) {
                        if (needSanitize(record[i])) {
                            System.out.println("BEFORE: " + record[i]);
                            record[i] = "'" + record[i];
                            System.out.println("AFTER : " + record[i]);
                        }
                    }
                    csvWriter.writeNext(record);
                }
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    private boolean needSanitize(String cellValue) {
        return cellValue.length() > 0 && BEGIN_CHARACTERS.contains(cellValue.charAt(0));
    }
}
