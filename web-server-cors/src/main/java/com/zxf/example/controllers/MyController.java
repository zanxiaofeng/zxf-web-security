package com.zxf.example.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/my")
public class MyController {
    @GetMapping("/get")
    public Map<String, String> get(@RequestParam String account) {
        return Collections.singletonMap("my-account-get", account);
    }

    @PostMapping("/post")
    public Map<String, String> post(@RequestParam String account) {
        return Collections.singletonMap("my-account-post", account);
    }

    @PutMapping("/put")
    public Map<String, String> put(@RequestBody Map<String, String> request) {
        return Collections.singletonMap("my-account-put", request.get("account"));
    }

    @GetMapping("/img")
    public Resource img() {
        return new ClassPathResource("/images/my-img.png");
    }
}
