package com.zxf.example.document.checker;

import com.aspose.cells.MsoDrawingType;
import com.aspose.cells.OleObject;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.words.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class ExcelDocumentChecker implements DocumentChecker {
    private static final List<String> ALLOWED_FORMAT = Arrays.asList("xls", "xlsx");

    @Override
    public boolean isSafe(BufferedInputStream inputStream, String fileName) {
        try {
            if (!isAllowedFormat(inputStream)) {
                return false;
            }

            return hasSafeContent(inputStream);
        } catch (Exception ex) {
            log.error("Exception during Excel file analysis.", ex);
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
