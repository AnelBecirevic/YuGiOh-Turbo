package com.yugiohTurbo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/create-account")
    public String createAccountPage() {
        return "create-account";
    }

    @GetMapping("/menu")
    public String menuPage(HttpSession session) {

        if (session.getAttribute("accountId") == null) {
            return "redirect:/";
        }

        return "menu";
    }
}