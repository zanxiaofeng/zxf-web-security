package com.zxf.example.document.checker;

import com.aspose.slides.IOleObjectFrame;
import com.aspose.slides.IShape;
import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import com.aspose.words.FileFormatInfo;
import com.aspose.words.FileFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class PowerpointDocumentChecker implements DocumentChecker {

    private static final List<String> ALLOWED_FORMATS = Arrays.asList(".ppt", ".pptx");

    public PowerpointDocumentChecker() {
        DocumentChecker.register(PowerpointDocumentChecker.class, this);
    }

    @Override
    public boolean isSafe(ByteArrayInputStream inputStream, FileFormatInfo fileFormatInfo) {
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

        Presentation presentation = new Presentation(stream);
        if (presentation.getVbaProject() != null) {
            return false;
        }

        for (ISlide slide : presentation.getSlides()) {
            for (IShape shape : slide.getShapes()) {
                if (shape instanceof IOleObjectFrame) {
                    return false;
                }
            }
        }

        return true;
    }
}
