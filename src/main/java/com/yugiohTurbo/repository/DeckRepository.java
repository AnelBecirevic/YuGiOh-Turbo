package com.yugiohTurbo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DeckRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeckRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer createDeck(
            Integer accountId,
            String deckName
    ) {

        return jdbcTemplate.queryForObject(
                """
                INSERT INTO deck (account_id, deck_name)
                VALUES (?, ?)
                RETURNING deck_id
                """,
                Integer.class,
                accountId,
                deckName
        );
    }

    public void addCard(
            Integer deckId,
            Integer cardId,
            Integer quantity
    ) {

        jdbcTemplate.update(
                """
                INSERT INTO deckcard (deck_id, card_id, quantity)
                VALUES (?, ?, ?)
                """,
                deckId,
                cardId,
                quantity
        );
    }
}