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
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/file")
public class FileController {
    private static final String FOLDER = "./src/main/resources/static/my";

    @GetMapping("/security")
    public ResponseEntity<FileSystemResource> security(@RequestParam String fileName) throws IOException {
        if (isSecurityAccess(FOLDER, fileName)) {
            File file = Paths.get(FOLDER).resolve(fileName).toFile();
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            System.out.println("******" + mimeType);
            return ResponseEntity.ok().contentType(MediaType.valueOf(mimeType)).body(new FileSystemResource(file));
        }
        throw new RuntimeException("Bad request");
    }

    @GetMapping("/security/**")
    public ResponseEntity<FileSystemResource> securityByPath(HttpServletRequest request) throws IOException {
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

    public boolean isSecurityAccess(String folder, String fileName) throws IOException {
        Path folderPath = Paths.get(folder).normalize();
        System.out.println("Folder: " + folderPath.toAbsolutePath());
        Path filePath = folderPath.resolve(fileName).normalize();
        System.out.println("File: " + filePath.toAbsolutePath());
        //toAbsolutePath并不解析Path中的符号链接(解析符号链接需要执行文件系统操作)
        //return filePath.toAbsolutePath().startsWith(folderPath.toAbsolutePath());
        return filePath.toRealPath().startsWith(folderPath.toRealPath());
    }

    public static void main(String[] args) {
        // /usr/bin/vim -> /etc/alternatives/vim -> /usr/bin/vim.basic

        String baseFolder = "/var/www/";
        Path myPath = Paths.get(baseFolder).resolve("../../usr/bin/vim");
        File myFile = myPath.toFile();

        System.out.println("Path::toString, " + myPath);
        System.out.println("Path::startsWith, " + myPath.startsWith(baseFolder));
        System.out.println("Path::normalize, " + myPath.normalize());
        System.out.println("Path::toAbsolutePath, " + myPath.toAbsolutePath());
        try {
            // This method will resolve symbol link
            System.out.println("Path::toRealPath, " + myPath.toRealPath());
        } catch (NoSuchFileException noSuchFileException) {
            noSuchFileException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        System.out.println("File::exists, " + myFile.exists());
        System.out.println("File::getAbsolutePath, " + myFile.getAbsolutePath());
        try {
            // This method will resolve symbol link
            System.out.println("File::getCanonicalPath, " + myFile.getCanonicalPath());
        } catch (NoSuchFileException noSuchFileException) {
            noSuchFileException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
