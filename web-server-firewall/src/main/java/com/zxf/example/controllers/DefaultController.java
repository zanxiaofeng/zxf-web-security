package com.zxf.example.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/default")
public class DefaultController {
    @GetMapping("/accounts/{id}")
    public Map<String, String> accountById(@PathVariable String id, @RequestParam String owner) {
        return new HashMap<>() {{
            put("id", id);
            put("owner", owner);
        }};
    }
}
