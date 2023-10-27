package com.zxf.example.controllers;

import com.zxf.example.document.DocumentType;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Controller
public class FileUploadController {

    @GetMapping("/fileUploader")
    public ModelAndView fileUploader() {
        ModelAndView modelAndView = new ModelAndView("file_uploader_view");
        return modelAndView;
    }

    @PostMapping("/uploadFile")
    public ModelAndView uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        try (InputStream stream = file.getInputStream(); ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(stream))) {
            DocumentType documentType = DocumentType.getDocumentType(file.getOriginalFilename());
            if (documentType == null) {
                throw new RuntimeException("This is not a supported file!");
            }

            if (!documentType.isSafe(byteArrayInputStream)) {
                if (!documentType.canSanitize()) {
                    throw new RuntimeException("Unsafe word file!");
                }

                byte[] result = documentType.sanitize(byteArrayInputStream);
                System.out.println(new String(result));
            }

            ModelAndView modelAndView = new ModelAndView("file_uploaded_view");
            modelAndView.addObject("file", file);
            return modelAndView;
        }
    }

    @PostMapping("/uploadMultiFile")
    public ModelAndView uploadMultiFile(@RequestParam("files") MultipartFile[] files) {
        ModelAndView modelAndView = new ModelAndView("file_uploaded_multiple_view");
        modelAndView.addObject("files", files);
        return modelAndView;
    }

    @PostMapping("/uploadFileWithAdditionalData")
    public ModelAndView uploadFileWithAdditionalData(@RequestParam MultipartFile file, @RequestParam String name, @RequestParam String email) {
        ModelAndView modelAndView = new ModelAndView("file_uploaded_additional_view");
        modelAndView.addObject("file", file);
        modelAndView.addObject("name", name);
        modelAndView.addObject("email", email);
        return modelAndView;
    }
}
