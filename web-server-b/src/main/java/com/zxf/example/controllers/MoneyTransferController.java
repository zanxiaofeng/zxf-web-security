package com.zxf.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/money")
@CrossOrigin(origins = "http://localhost:8081", methods = {RequestMethod.GET, RequestMethod.POST}, allowCredentials = "true", maxAge = 3600)
public class MoneyTransferController {

    @GetMapping("/transfer")
    public ModelAndView transfer_form() {
        ModelAndView modelAndView = new ModelAndView("money/transfer_form");
        return modelAndView;
    }

    @PostMapping("/transfer")
    public ModelAndView transfer_to(@RequestParam String sourceAccount, String targetAccount, String amount) {
        ModelAndView modelAndView = new ModelAndView("money/transfer_result");
        modelAndView.addObject("sourceAccount", sourceAccount);
        modelAndView.addObject("targetAccount", targetAccount);
        modelAndView.addObject("amount", amount);
        return modelAndView;
    }
}
