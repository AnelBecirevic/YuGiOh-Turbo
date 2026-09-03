package com.yugiohTurbo.model;

import java.io.Serializable;

public interface DuelCard extends Serializable {

    Integer cardId();

    String name();

    String cardType();

    String description();

    String imagePath();
}