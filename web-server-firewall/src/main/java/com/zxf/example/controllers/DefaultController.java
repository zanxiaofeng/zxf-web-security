package com.zxf.example.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/*

 */
@RestController
@RequestMapping("/default")
public class DefaultController {
    @GetMapping("/get")
    public Map<String, String> get(@RequestParam String account) {
        return Collections.singletonMap("default-account-get", account);
    }

    @PostMapping("/post")
    public Map<String, String> post(@RequestParam String account) {
        return Collections.singletonMap("default-account-post", account);
    }

    @PutMapping("/put")
    public Map<String, String> put(@RequestBody Map<String, String> request) {
        return Collections.singletonMap("default-account-put", request.get("account"));
    }
}
