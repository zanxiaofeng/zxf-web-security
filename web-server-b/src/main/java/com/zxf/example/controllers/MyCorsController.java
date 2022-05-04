package com.zxf.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cors")
@CrossOrigin(origins = "http://localhost:8081", methods = {RequestMethod.GET, RequestMethod.POST}, allowCredentials = "true", maxAge = 3600)
public class MyCorsController {
    @GetMapping("/get")
    public ModelAndView get(@RequestParam String account) {
        ModelAndView modelAndView = new ModelAndView("cors/get");
        modelAndView.addObject("account", account);
        return modelAndView;
    }

    @PostMapping("/post")
    public ModelAndView post(@RequestParam String account) {
        ModelAndView modelAndView = new ModelAndView("cors/post");
        modelAndView.addObject("account", account);
        return modelAndView;
    }
}
