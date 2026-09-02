package com.yugiohTurbo.controller;

import com.yugiohTurbo.model.Account;
import com.yugiohTurbo.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create-account")
    public String createAccount(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model
    ) {

        boolean created = accountService.createAccount(
                username,
                password,
                confirmPassword
        );

        if (!created) {
            model.addAttribute(
                    "error",
                    "Account could not be created. Check your details or choose another username."
            );

            return "create-account";
        }

        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {

        Account account = accountService.login(username, password);

        if (account == null) {
            model.addAttribute(
                    "error",
                    "Incorrect username or password."
            );

            return "login";
        }

        session.setAttribute("accountId", account.accountId());
        session.setAttribute("username", account.username());

        return "redirect:/menu";
    }
}
