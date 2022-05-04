package com.zxf.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/my")
public class MyController {
    @GetMapping("/redirect")
    public ModelAndView redirect(@RequestParam String redirectUrl) {
        return new ModelAndView("redirect:" + redirectUrl);
    }
}
