package com.zxf.example.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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
        //如果Path中有符号链接，并不能解析后比较（解析符号链接需要真实执行文件系统操作）
        return filePath.toAbsolutePath().startsWith(folderPath.toAbsolutePath());
    }

    public static void main(String[] args) throws IOException {
        String baseFolder = "var/www/images/";
        Path myPath = Paths.get(baseFolder).resolve("../../../etc/hosts");
        //toFile并不执行文件系统操作
        File myFile = myPath.toFile();

        System.out.println("Path::toString, " + myPath);
        System.out.println("Path::startsWith, " + myPath.startsWith(baseFolder));
        System.out.println("Path::toString, " + myPath.normalize());
        System.out.println("Path::toString, " + myPath.toAbsolutePath());
        System.out.println("File::exists, " + myFile.exists());
        System.out.println("File::getAbsolutePath, " + myFile.getAbsolutePath());
        System.out.println("File::getCanonicalPath, " + myFile.getCanonicalPath());
    }
}
