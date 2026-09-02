package com.yugiohTurbo.model;

public record Account(
        Integer accountId,
        String username,
        String passwordHash
) {
}