package com.yugiohTurbo.repository;

import com.yugiohTurbo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByUsername(String username) {

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM account WHERE username = ?",
                Integer.class,
                username
        );

        return count != null && count > 0;
    }

    public Integer create(String username, String passwordHash) {

        return jdbcTemplate.queryForObject(
                """
                INSERT INTO account (username, password_hash)
                VALUES (?, ?)
                RETURNING account_id
                """,
                Integer.class,
                username,
                passwordHash
        );
    }

    public Account findByUsername(String username) {

        return jdbcTemplate.query(
                """
                SELECT account_id, username, password_hash
                FROM account
                WHERE username = ?
                """,
                rs -> {

                    if (rs.next()) {
                        return new Account(
                                rs.getInt("account_id"),
                                rs.getString("username"),
                                rs.getString("password_hash")
                        );
                    }

                    return null;
                },
                username
        );
    }

    public Account findById(Integer accountId) {

        return jdbcTemplate.query(
                """
                SELECT account_id, username, password_hash
                FROM account
                WHERE account_id = ?
                """,
                rs -> {

                    if (rs.next()) {
                        return new Account(
                                rs.getInt("account_id"),
                                rs.getString("username"),
                                rs.getString("password_hash")
                        );
                    }

                    return null;
                },
                accountId
        );
    }

    public void updateUsername(Integer accountId, String newUsername) {

        jdbcTemplate.update(
                """
                UPDATE account
                SET username = ?
                WHERE account_id = ?
                """,
                newUsername,
                accountId
        );
    }

    public void deleteById(Integer accountId) {

        jdbcTemplate.update(
                "DELETE FROM account WHERE account_id = ?",
                accountId
        );
    }
}