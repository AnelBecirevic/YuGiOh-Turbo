package com.yugiohTurbo.model;

import java.io.Serializable;

public class DuelMonster implements Serializable {

    public enum Position {
        ATTACK,
        DEFENSE
    }

    private final DuelMonsterCard card;
    private final Position position;
    private boolean attacked;

    public DuelMonster(
            DuelMonsterCard card,
            Position position
    ) {
        this.card = card;
        this.position = position;
        this.attacked = false;
    }

    public DuelMonsterCard getCard() {
        return card;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isAttacked() {
        return attacked;
    }

    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }
}