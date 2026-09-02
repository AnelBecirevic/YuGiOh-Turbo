package com.yugiohTurbo.repository;

import com.yugiohTurbo.model.TrunkCard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public Integer findDeckIdByAccountId(Integer accountId) {

        return jdbcTemplate.query(
                """
                SELECT deck_id
                FROM deck
                WHERE account_id = ?
                ORDER BY deck_id
                LIMIT 1
                """,
                rs -> rs.next()
                        ? rs.getInt("deck_id")
                        : null,
                accountId
        );
    }

    public List<TrunkCard> findCardsByDeckId(Integer deckId) {

        return jdbcTemplate.query(
                """
                SELECT
                    c.card_id,
                    c.name,
                    dc.quantity,
                    m.attribute,
                    m.monster_type,
                    m.level,
                    m.attack,
                    m.defense,
                    c.description,
                    c.image_path
                FROM deckcard dc
                JOIN card c
                    ON dc.card_id = c.card_id
                JOIN monstercard m
                    ON c.card_id = m.card_id
                WHERE dc.deck_id = ?
                ORDER BY c.card_id
                """,
                (rs, rowNum) -> new TrunkCard(
                        rs.getInt("card_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getString("attribute"),
                        rs.getString("monster_type"),
                        rs.getInt("level"),
                        rs.getInt("attack"),
                        rs.getInt("defense"),
                        rs.getString("description"),
                        rs.getString("image_path")
                ),
                deckId
        );
    }

    public Integer getCardQuantity(
            Integer deckId,
            Integer cardId
    ) {

        return jdbcTemplate.query(
                """
                SELECT quantity
                FROM deckcard
                WHERE deck_id = ?
                AND card_id = ?
                """,
                rs -> rs.next()
                        ? rs.getInt("quantity")
                        : 0,
                deckId,
                cardId
        );
    }

    public int getDeckSize(Integer deckId) {

        Integer size = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(quantity), 0)
                FROM deckcard
                WHERE deck_id = ?
                """,
                Integer.class,
                deckId
        );

        return size == null ? 0 : size;
    }

    public void incrementCard(
            Integer deckId,
            Integer cardId
    ) {

        jdbcTemplate.update(
                """
                INSERT INTO deckcard (deck_id, card_id, quantity)
                VALUES (?, ?, 1)
                ON CONFLICT (deck_id, card_id)
                DO UPDATE SET quantity = deckcard.quantity + 1
                """,
                deckId,
                cardId
        );
    }

    public void decrementCard(
            Integer deckId,
            Integer cardId
    ) {

        jdbcTemplate.update(
                """
                UPDATE deckcard
                SET quantity = quantity - 1
                WHERE deck_id = ?
                AND card_id = ?
                """,
                deckId,
                cardId
        );

        jdbcTemplate.update(
                """
                DELETE FROM deckcard
                WHERE deck_id = ?
                AND card_id = ?
                AND quantity <= 0
                """,
                deckId,
                cardId
        );
    }
}