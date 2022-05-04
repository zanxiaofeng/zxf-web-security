package com.zxf.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/my")
public class MyController {
    @GetMapping("/get")
    public ModelAndView get(@RequestParam String account) {
        ModelAndView modelAndView = new ModelAndView("my/get");
        modelAndView.addObject("account", account);
        return modelAndView;
    }

    @PostMapping("/post")
    public ModelAndView post(@RequestParam String account) {
        ModelAndView modelAndView = new ModelAndView("my/post");
        modelAndView.addObject("account", account);
        return modelAndView;
    }
}
