package com.zxf.example.controllers;

import com.zxf.example.document.checker.WordDocumentChecker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class FileUploadController {
    WordDocumentChecker wordDocumentChecker;

    @GetMapping("/fileUploader")
    public ModelAndView fileUploader() {
        ModelAndView modelAndView = new ModelAndView("file_uploader_view");
        return modelAndView;
    }

    @PostMapping("/uploadFile")
    public ModelAndView uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        try (InputStream stream = file.getInputStream()){
            if (!wordDocumentChecker.isSafe(new BufferedInputStream(stream), file.getName())){
                throw  new RuntimeException("Unsafe word file!");
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
