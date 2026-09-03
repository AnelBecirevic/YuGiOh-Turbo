package com.yugiohTurbo.model;

public record DuelMonsterCard(
        Integer cardId,
        String name,
        String attribute,
        String monsterType,
        Integer level,
        Integer attack,
        Integer defense,
        String description,
        String imagePath
) implements DuelCard {

    @Override
    public String cardType() {
        return "MONSTER";
    }
}