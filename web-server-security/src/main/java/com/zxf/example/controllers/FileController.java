package com.zxf.example.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/file")
public class FileController {
    private static final String FOLDER = "./src/main/resources/static/my";

    @GetMapping("/security")
    public FileSystemResource security(@RequestParam String fileName) {
        if (isSecurityAccess(FOLDER, fileName)) {
            return new FileSystemResource(Paths.get(FOLDER).resolve(fileName).toFile());
        }
        throw new RuntimeException("Bad request");
    }

    @GetMapping("/security/**")
    public FileSystemResource securityByPath(HttpServletRequest request) {
        String fileName = request.getRequestURI().substring(15);
        if (isSecurityAccess(FOLDER, fileName)) {
            return new FileSystemResource(Paths.get(FOLDER).resolve(fileName).toFile());
        }
        throw new RuntimeException("Bad request");
    }

    @GetMapping("/un-security")
    public FileSystemResource unSecurity(@RequestParam String fileName) {
        return new FileSystemResource(Paths.get(FOLDER).resolve(fileName).toFile());
    }

    @GetMapping("/un-security/{fileName}")
    public FileSystemResource unSecurityByPath(@PathVariable String fileName) {
        return new FileSystemResource(Paths.get(FOLDER).resolve(fileName).toFile());
    }

    public boolean isSecurityAccess(String folder, String fileName) {
        Path folderPath = Paths.get(folder).normalize();
        System.out.println("Folder: " + folderPath.toAbsolutePath());
        Path filePath = folderPath.resolve(fileName).normalize();
        System.out.println("File: " + filePath.toAbsolutePath());
        return filePath.toAbsolutePath().startsWith(folderPath.toAbsolutePath());
    }
}
