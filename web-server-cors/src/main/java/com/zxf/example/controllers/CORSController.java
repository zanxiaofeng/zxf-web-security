package com.zxf.example.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/cors")
@CrossOrigin(origins = "http://localhost:8082,http://localhost:8081", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT}, allowCredentials = "true", maxAge = 3600)
public class CORSController {
    @GetMapping("/get")
    public Map<String, String> get(@RequestParam String account) {
        return Collections.singletonMap("cors-account-get", account);

    }

    @PostMapping("/post")
    public Map<String, String> post(@RequestParam String account) {
        return Collections.singletonMap("cors-account-post", account);
    }

    @PostMapping("/put")
    public Map<String, String> put(@RequestBody Map<String, String> request) {
        return Collections.singletonMap("cors-account-put", request.get("account"));
    }

//    @GetMapping("/img")
//    public Resource img() {
//        return new ClassPathResource("/images/cors.jpg");
//    }
}
