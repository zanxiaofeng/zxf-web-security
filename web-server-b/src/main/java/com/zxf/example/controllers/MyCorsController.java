package com.zxf.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cors")
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600)
public class MyCorsController {
    @GetMapping("/get")
    public ModelAndView get(@RequestParam String account) {
        return new ModelAndView("cors/get");
    }

    @PostMapping("/post")
    public ModelAndView post(@RequestParam String account) {
        return new ModelAndView("cors/post");
    }
}
