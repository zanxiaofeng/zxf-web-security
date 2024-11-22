package com.zxf.example.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

@Controller
public class MoneyTransferController {

    @GetMapping("/")
    public ModelAndView transfer_form() {
        ModelAndView modelAndView = new ModelAndView("money/transfer_form");
        return modelAndView;
    }

    @PostMapping("/money/transfer")
    public ModelAndView transfer_to(@RequestParam String sourceAccount, @RequestParam String targetAccount, @RequestParam String amount,
                                    @RequestParam(name = "_csrf", required = false) String csrf, HttpServletRequest request) {
        System.out.println(amount + " has been transfer from " + sourceAccount + " to " + targetAccount);
        ModelAndView modelAndView = new ModelAndView("money/transfer_result");
        modelAndView.addObject("sourceAccount", sourceAccount);
        modelAndView.addObject("targetAccount", targetAccount);
        modelAndView.addObject("amount", amount);
        modelAndView.addObject("csrfTokenFromCookie",
                Optional.ofNullable(WebUtils.getCookie(request, "XSRF-TOKEN")).map(Cookie::getValue).orElse(""));
        modelAndView.addObject("csrfTokenFromRequest", csrf);
        return modelAndView;
    }
}
