package com.zxf.example.controllers;

import com.zxf.example.document.DocumentType;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

            if (!documentType.isSafe(byteArrayInputStream, file.getOriginalFilename())) {
                if (!documentType.canSanitize()) {
                    throw new RuntimeException("Unsafe file!");
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
    public ModelAndView uploadMultiFile(@RequestParam("files") MultipartFile[] files) throws IOException {
        for (int i = 0; i < files.length; i++) {
            // 在Windows系统中，打开InputStream而不关闭，会导致底层打开的文件被占用，也会导致MultipartResolver.cleanupMultipart()方法不能删除该文件。
            InputStream inputStream = files[i].getInputStream();
            inputStream.readNBytes(102400);
        }

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

    @PostMapping("/uploadFilesWithAdditionalData")
    public ModelAndView uploadFilesWithAdditionalData(@RequestParam MultipartFile[] files, @RequestParam String name, @RequestParam String email) throws IOException {
        for (int i = 0; i < files.length; i++) {
            // 在Windows系统中，打开InputStream而不关闭，会导致底层打开的文件被占用，也会导致MultipartResolver.cleanupMultipart()方法不能删除该文件。
            InputStream inputStream = files[i].getInputStream();
            inputStream.readNBytes(102400);
        }
        ModelAndView modelAndView = new ModelAndView("file_uploaded_multiple_additional_view");
        modelAndView.addObject("files", files);
        modelAndView.addObject("name", name);
        modelAndView.addObject("email", email);
        return modelAndView;
    }
}
