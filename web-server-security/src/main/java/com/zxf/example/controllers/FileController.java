package com.zxf.example.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/file")
public class FileController {
    private static final String FOLDER = "./src/main/resources/static/my";

    @GetMapping("/security")
    public ResponseEntity<FileSystemResource> security(@RequestParam String fileName) {
        if (isSecurityAccess(FOLDER, fileName)) {
            File file = Paths.get(FOLDER).resolve(fileName).toFile();
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            System.out.println("******" + mimeType);
            return ResponseEntity.ok().contentType(MediaType.valueOf(mimeType)).body(new FileSystemResource(file));
        }
        throw new RuntimeException("Bad request");
    }

    @GetMapping("/security/**")
    public ResponseEntity<FileSystemResource> securityByPath(HttpServletRequest request) {
        String fileName = request.getRequestURI().substring(15);
        if (isSecurityAccess(FOLDER, fileName)) {
            File file = Paths.get(FOLDER).resolve(fileName).toFile();
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            System.out.println("******" + mimeType);
            return ResponseEntity.ok().contentType(MediaType.valueOf(mimeType)).body(new FileSystemResource(file));
        }
        throw new RuntimeException("Bad request");
    }

    @GetMapping("/un-security")
    public ResponseEntity<FileSystemResource> unSecurity(@RequestParam String fileName) {
        File file = Paths.get(FOLDER).resolve(fileName).toFile();
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        System.out.println("******" + mimeType);
        return ResponseEntity.ok().contentType(MediaType.valueOf(mimeType)).body(new FileSystemResource(file));
    }

    @GetMapping("/un-security/{fileName}")
    public ResponseEntity<FileSystemResource> unSecurityByPath(@PathVariable String fileName) {
        File file = Paths.get(FOLDER).resolve(fileName).toFile();
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        System.out.println("******" + mimeType);
        return ResponseEntity.ok().contentType(MediaType.valueOf(mimeType)).body(new FileSystemResource(file));
    }

    public boolean isSecurityAccess(String folder, String fileName) {
        Path folderPath = Paths.get(folder).normalize();
        System.out.println("Folder: " + folderPath.toAbsolutePath());
        Path filePath = folderPath.resolve(fileName).normalize();
        System.out.println("File: " + filePath.toAbsolutePath());
        return filePath.toAbsolutePath().startsWith(folderPath.toAbsolutePath());
    }
}
