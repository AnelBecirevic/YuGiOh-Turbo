package com.yugiohTurbo.repository;

import com.yugiohTurbo.model.DuelCard;
import com.yugiohTurbo.model.DuelMonsterCard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DuelRepository {

    private final JdbcTemplate jdbcTemplate;

    public DuelRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DuelCard> loadPlayerDeck(Integer accountId) {

        return jdbcTemplate.query(
                """
                SELECT
                    c.card_id,
                    c.name,
                    c.description,
                    c.image_path,
                    m.attribute,
                    m.monster_type,
                    m.level,
                    m.attack,
                    m.defense,
                    dc.quantity
                FROM deckcard dc
                JOIN deck d
                    ON dc.deck_id = d.deck_id
                JOIN card c
                    ON dc.card_id = c.card_id
                JOIN monstercard m
                    ON c.card_id = m.card_id
                WHERE d.deck_id = (
                    SELECT deck_id
                    FROM deck
                    WHERE account_id = ?
                    ORDER BY deck_id
                    LIMIT 1
                )
                ORDER BY c.card_id
                """,
                rs -> {

                    List<DuelCard> deck = new ArrayList<>();

                    while (rs.next()) {

                        DuelMonsterCard card =
                                new DuelMonsterCard(
                                        rs.getInt("card_id"),
                                        rs.getString("name"),
                                        rs.getString("attribute"),
                                        rs.getString("monster_type"),
                                        rs.getInt("level"),
                                        rs.getInt("attack"),
                                        rs.getInt("defense"),
                                        rs.getString("description"),
                                        rs.getString("image_path")
                                );

                        int quantity =
                                rs.getInt("quantity");

                        for (int i = 0; i < quantity; i++) {
                            deck.add(card);
                        }
                    }

                    return deck;
                },
                accountId
        );
    }

    public List<DuelCard> loadKaibaDeck() {

        return jdbcTemplate.query(
                """
                SELECT
                    c.card_id,
                    c.name,
                    c.description,
                    c.image_path,
                    m.attribute,
                    m.monster_type,
                    m.level,
                    m.attack,
                    m.defense
                FROM card c
                JOIN monstercard m
                    ON c.card_id = m.card_id
                WHERE c.card_id BETWEEN 15 AND 28
                ORDER BY c.card_id
                """,
                rs -> {

                    List<DuelCard> deck = new ArrayList<>();

                    while (rs.next()) {

                        DuelMonsterCard card =
                                new DuelMonsterCard(
                                        rs.getInt("card_id"),
                                        rs.getString("name"),
                                        rs.getString("attribute"),
                                        rs.getString("monster_type"),
                                        rs.getInt("level"),
                                        rs.getInt("attack"),
                                        rs.getInt("defense"),
                                        rs.getString("description"),
                                        rs.getString("image_path")
                                );

                        int cardId =
                                rs.getInt("card_id");

                        int quantity =
                                cardId <= 26 ? 3 : 2;

                        for (int i = 0; i < quantity; i++) {
                            deck.add(card);
                        }
                    }

                    return deck;
                }
        );
    }
}