package com.yugiohTurbo.repository;

import com.yugiohTurbo.model.TrunkCard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<TrunkCard> findAllByAccountId(Integer accountId) {

        return jdbcTemplate.query(
                """
                SELECT
                    c.card_id,
                    c.name,
                    t.quantity,
                    m.attribute,
                    m.monster_type,
                    m.level,
                    m.attack,
                    m.defense,
                    c.description,
                    c.image_path
                FROM trunk t
                JOIN card c
                    ON t.card_id = c.card_id
                JOIN monstercard m
                    ON c.card_id = m.card_id
                WHERE t.account_id = ?
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
                accountId
        );
    }

    public Integer getCardQuantity(
            Integer accountId,
            Integer cardId
    ) {

        return jdbcTemplate.query(
                """
                SELECT quantity
                FROM trunk
                WHERE account_id = ?
                AND card_id = ?
                """,
                rs -> rs.next()
                        ? rs.getInt("quantity")
                        : 0,
                accountId,
                cardId
        );
    }
}