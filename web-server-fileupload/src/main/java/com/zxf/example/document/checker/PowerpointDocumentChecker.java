package com.zxf.example.document.checker;

import com.aspose.slides.IOleObjectFrame;
import com.aspose.slides.IShape;
import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class PowerpointDocumentChecker implements DocumentChecker {
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
