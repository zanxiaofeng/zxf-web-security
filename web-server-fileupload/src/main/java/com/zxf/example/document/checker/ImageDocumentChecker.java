package com.zxf.example.document.checker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ImageDocumentChecker implements DocumentChecker {

    private static final List<String> ALLOWED_FORMATS = Arrays.asList("jpg", "png", "tiff");

    public ImageDocumentChecker() {
        DocumentChecker.register(ImageDocumentChecker.class, this);
    }

    @Override
    public boolean isSafe(ByteArrayInputStream inputStream, String fileExtension) {
        try {
            if (!isAllowedFormat(fileExtension)) {
                return false;
            }

            return hasSafeContent(inputStream);
        } catch (Exception ex) {
            log.error("Exception during image file analysis.", ex);
            return false;
        }
    }

    private Boolean isAllowedFormat(String fileExtension) throws Exception {
        return ALLOWED_FORMATS.contains(fileExtension);
    }

    private Boolean hasSafeContent(ByteArrayInputStream stream) throws Exception {
        stream.reset();
        return ImageIO.read(stream) != null;
    }
}
