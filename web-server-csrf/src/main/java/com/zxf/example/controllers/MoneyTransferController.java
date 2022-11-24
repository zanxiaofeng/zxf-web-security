package com.zxf.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/money")
public class MoneyTransferController {

    @GetMapping("/transfer")
    public ModelAndView transfer_form() {
        ModelAndView modelAndView = new ModelAndView("money/transfer_form");
        return modelAndView;
    }

    @PostMapping("/transfer")
    public ModelAndView transfer_to(@RequestParam String sourceAccount, @RequestParam String targetAccount, @RequestParam String amount, HttpServletRequest request) {
        System.out.println(amount + " has been transfer from " + sourceAccount + " to " + targetAccount);
        ModelAndView modelAndView = new ModelAndView("money/transfer_result");
        modelAndView.addObject("sourceAccount", sourceAccount);
        modelAndView.addObject("targetAccount", targetAccount);
        modelAndView.addObject("amount", amount);
        modelAndView.addObject("csfrTokenFromCookie", WebUtils.getCookie(request, ""));
        modelAndView.addObject("csfrTokenFromRequest", request.getParameterValues(""));
        return modelAndView;
    }
}
