package com.zxf.example.document.checker;

import com.aspose.cells.MsoDrawingType;
import com.aspose.cells.OleObject;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.words.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Component
public class ExcelDocumentChecker implements DocumentChecker {
    private static final List<String> ALLOWED_FORMATS = Arrays.asList(".xls", ".xlsx", ".csv");

    public ExcelDocumentChecker() {
        DocumentChecker.register("excel", this);
    }

    @Override
    public boolean isSafe(ByteArrayInputStream inputStream, String fileName, FileFormatInfo fileFormatInfo) {
        try {
            if (!isAllowedFormat(fileFormatInfo)) {
                return false;
            }

            return hasSafeContent(inputStream);
        } catch (Exception ex) {
            log.error("Exception during Excel file analysis.", ex);
            return false;
        }
    }

    private Boolean isAllowedFormat(FileFormatInfo fileFormatInfo) throws Exception {
        String formatExtension = FileFormatUtil.loadFormatToExtension(fileFormatInfo.getLoadFormat());
        return ALLOWED_FORMATS.contains(formatExtension);
    }

    private Boolean hasSafeContent(ByteArrayInputStream stream) throws Exception {
        stream.reset();

        Workbook workbook = new Workbook(stream);
        if (workbook.hasMacro()) {
            return false;
        }


        for (int i = 0; i < workbook.getWorksheets().getCount(); i++) {
            Worksheet worksheet = workbook.getWorksheets().get(i);
            for (int j = 0; j < worksheet.getOleObjects().getCount(); j++) {
                OleObject oleObject = worksheet.getOleObjects().get(j);
                if (oleObject.getMsoDrawingType() == MsoDrawingType.OLE_OBJECT) {
                    return false;
                }
            }
        }

        return true;
    }
}
