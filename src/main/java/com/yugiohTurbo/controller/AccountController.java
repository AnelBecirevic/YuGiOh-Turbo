package com.yugiohTurbo.controller;

import com.yugiohTurbo.model.Account;
import com.yugiohTurbo.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/account")
    public String accountPage(
            HttpSession session,
            Model model
    ) {

        Integer accountId = (Integer) session.getAttribute("accountId");

        if (accountId == null) {
            return "redirect:/";
        }

        Account account = accountService.getAccount(accountId);

        if (account == null) {
            session.invalidate();
            return "redirect:/";
        }

        model.addAttribute("username", account.username());

        return "account";
    }

    @GetMapping("/change-username")
    public String changeUsernamePage(HttpSession session) {

        if (session.getAttribute("accountId") == null) {
            return "redirect:/";
        }

        return "change-username";
    }

    @PostMapping("/change-username")
    public String changeUsername(
            @RequestParam String newUsername,
            HttpSession session,
            Model model
    ) {

        Integer accountId = (Integer) session.getAttribute("accountId");

        if (accountId == null) {
            return "redirect:/";
        }

        boolean changed = accountService.changeUsername(
                accountId,
                newUsername
        );

        if (!changed) {
            model.addAttribute(
                    "error",
                    "Username is invalid or already taken."
            );

            return "change-username";
        }

        session.setAttribute("username", newUsername.trim());

        return "redirect:/account";
    }

    @GetMapping("/delete-account")
    public String deleteAccountPage(HttpSession session) {

        if (session.getAttribute("accountId") == null) {
            return "redirect:/";
        }

        return "delete-account";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(HttpSession session) {

        Integer accountId = (Integer) session.getAttribute("accountId");

        if (accountId == null) {
            return "redirect:/";
        }

        accountService.deleteAccount(accountId);

        session.invalidate();

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/";
    }
}