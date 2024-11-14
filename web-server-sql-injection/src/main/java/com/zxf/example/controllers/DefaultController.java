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
@RequestMapping("/sql")
public class DefaultController {
    @GetMapping("/security")
    public Map<String, String> get(@RequestParam String account) {
        return Collections.singletonMap("default-account-get", account);
    }

    @PostMapping("/unSecurity")
    public Map<String, String> post(@RequestParam String account) {
        return Collections.singletonMap("default-account-post", account);
    }
}
