package com.yugiohTurbo.service;

import com.yugiohTurbo.model.Account;
import com.yugiohTurbo.repository.AccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PlayerSetupService playerSetupService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AccountService(
            AccountRepository accountRepository,
            PlayerSetupService playerSetupService
    ) {

        this.accountRepository = accountRepository;
        this.playerSetupService = playerSetupService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
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

        String passwordHash =
                passwordEncoder.encode(password);

        Integer accountId =
                accountRepository.create(
                        username.trim(),
                        passwordHash
                );

        playerSetupService.initializeNewPlayer(accountId);

        return true;
    }

    public Account login(
            String username,
            String password
    ) {

        Account account =
                accountRepository.findByUsername(username);

        if (account == null) {
            return null;
        }

        if (!passwordEncoder.matches(
                password,
                account.passwordHash()
        )) {
            return null;
        }

        return account;
    }

    public Account getAccount(Integer accountId) {
        return accountRepository.findById(accountId);
    }

    public boolean changeUsername(
            Integer accountId,
            String newUsername
    ) {

        if (newUsername == null || newUsername.isBlank()) {
            return false;
        }

        newUsername = newUsername.trim();

        if (accountRepository.existsByUsername(newUsername)) {
            return false;
        }

        accountRepository.updateUsername(
                accountId,
                newUsername
        );

        return true;
    }

    public void deleteAccount(Integer accountId) {
        accountRepository.deleteById(accountId);
    }
}