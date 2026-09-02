package com.yugiohTurbo.service;

import com.yugiohTurbo.model.Account;
import com.yugiohTurbo.repository.AccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean createAccount(
            String username,
            String password,
            String confirmPassword
    ) {

        if (username == null || username.isBlank()) {
            return false;
        }

        if (password == null || password.isBlank()) {
            return false;
        }

        if (!password.equals(confirmPassword)) {
            return false;
        }

        if (accountRepository.existsByUsername(username)) {
            return false;
        }

        String passwordHash = passwordEncoder.encode(password);

        accountRepository.create(username, passwordHash);

        return true;
    }

    public Account login(String username, String password) {

        Account account = accountRepository.findByUsername(username);

        if (account == null) {
            return null;
        }

        if (!passwordEncoder.matches(password, account.passwordHash())) {
            return null;
        }

        return account;
    }
}