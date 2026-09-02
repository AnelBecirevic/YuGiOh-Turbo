package com.yugiohTurbo.model;

public record TrunkCard(
        Integer cardId,
        String name,
        Integer quantity,
        String attribute,
        String monsterType,
        Integer level,
        Integer attack,
        Integer defense,
        String description,
        String imagePath
) {
}