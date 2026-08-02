package com.yugiohTurbo.controller;

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
    public String menuPage() {
        return "menu";
    }

    @GetMapping("/account")
    public String accountPage() {
        return "account";
    }

    @GetMapping("/change-username")
    public String changeUsernamePage() {
        return "change-username";
    }

    @GetMapping("/delete-account")
    public String deleteAccountPage() {
        return "delete-account";
    }

}