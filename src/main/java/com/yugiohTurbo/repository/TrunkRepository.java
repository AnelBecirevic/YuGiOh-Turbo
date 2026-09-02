package com.yugiohTurbo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TrunkRepository {

    private final JdbcTemplate jdbcTemplate;

    public TrunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addCard(
            Integer accountId,
            Integer cardId,
            Integer quantity
    ) {

        jdbcTemplate.update(
                """
                INSERT INTO trunk (account_id, card_id, quantity)
                VALUES (?, ?, ?)
                """,
                accountId,
                cardId,
                quantity
        );
    }
}