package com.yugiohTurbo.service;

import com.yugiohTurbo.model.DuelCard;
import com.yugiohTurbo.model.DuelMonster;
import com.yugiohTurbo.model.DuelMonsterCard;
import com.yugiohTurbo.model.DuelState;
import com.yugiohTurbo.repository.DuelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class DuelServiceTest {

    private DuelRepository duelRepository;

    private DuelService duelService;

    @BeforeEach
    void setUp() {

        duelRepository =
                Mockito.mock(DuelRepository.class);

        duelService =
                new DuelService(
                        duelRepository
                );
    }

    @Test
    void requiredTributesReturnsZeroForLevelFourMonster() {

        DuelMonsterCard card =
                createMonster(
                        1,
                        "Celtic Guardian",
                        4,
                        1400,
                        1200
                );

        int tributes =
                duelService.requiredTributes(
                        card
                );

        assertEquals(
                0,
                tributes
        );
    }

    @Test
    void requiredTributesReturnsOneForLevelSixMonster() {

        DuelMonsterCard card =
                createMonster(
                        2,
                        "Summoned Skull",
                        6,
                        2500,
                        1200
                );

        int tributes =
                duelService.requiredTributes(
                        card
                );

        assertEquals(
                1,
                tributes
        );
    }

    @Test
    void requiredTributesReturnsTwoForLevelSevenMonster() {

        DuelMonsterCard card =
                createMonster(
                        3,
                        "Dark Magician",
                        7,
                        2500,
                        2100
                );

        int tributes =
                duelService.requiredTributes(
                        card
                );

        assertEquals(
                2,
                tributes
        );
    }

    @Test
    void startDuelCreatesFiveCardStartingHands() {

        Integer accountId = 1;

        List<DuelCard> playerDeck =
                createFortyCardDeck(
                        "Player Monster"
                );

        List<DuelCard> opponentDeck =
                createFortyCardDeck(
                        "Kaiba Monster"
                );

        when(
                duelRepository.loadPlayerDeck(
                        accountId
                )
        ).thenReturn(playerDeck);

        when(
                duelRepository.loadKaibaDeck()
        ).thenReturn(opponentDeck);

        DuelState state =
                duelService.startDuel(
                        accountId
                );

        assertNotNull(
                state
        );

        assertEquals(
                5,
                state.getPlayerHand().size()
        );

        assertEquals(
                5,
                state.getOpponentHand().size()
        );

        assertEquals(
                35,
                state.getPlayerDeck().size()
        );

        assertEquals(
                35,
                state.getOpponentDeck().size()
        );

        assertEquals(
                8000,
                state.getPlayerLifePoints()
        );

        assertEquals(
                8000,
                state.getOpponentLifePoints()
        );

        assertEquals(
                "MAIN PHASE 1",
                state.getCurrentPhase()
        );
    }

    @Test
    void startDuelFailsWhenPlayerDeckDoesNotContainFortyCards() {

        Integer accountId = 1;

        List<DuelCard> invalidDeck =
                new ArrayList<>();

        for (int i = 0; i < 39; i++) {

            invalidDeck.add(
                    createMonster(
                            i + 1,
                            "Monster " + i,
                            4,
                            1000,
                            1000
                    )
            );
        }

        when(
                duelRepository.loadPlayerDeck(
                        accountId
                )
        ).thenReturn(
                invalidDeck
        );

        DuelState state =
                duelService.startDuel(
                        accountId
                );

        assertNull(
                state
        );
    }

    @Test
    void playerCannotEnterBattlePhaseOnFirstTurn() {

        DuelState state =
                createEmptyDuelState();

        state.setTurnNumber(1);
        state.setPlayerTurn(true);
        state.setCurrentPhase(
                "MAIN PHASE 1"
        );

        duelService.enterBattlePhase(
                state
        );

        assertEquals(
                "MAIN PHASE 1",
                state.getCurrentPhase()
        );
    }

    @Test
    void playerCanEnterBattlePhaseAfterFirstTurn() {

        DuelState state =
                createEmptyDuelState();

        state.setTurnNumber(2);
        state.setPlayerTurn(true);
        state.setCurrentPhase(
                "MAIN PHASE 1"
        );

        duelService.enterBattlePhase(
                state
        );

        assertEquals(
                "BATTLE PHASE",
                state.getCurrentPhase()
        );
    }

    @Test
    void playerCanMoveFromBattlePhaseToMainPhaseTwo() {

        DuelState state =
                createEmptyDuelState();

        state.setTurnNumber(2);
        state.setPlayerTurn(true);
        state.setCurrentPhase(
                "BATTLE PHASE"
        );

        duelService.enterMainPhase2(
                state
        );

        assertEquals(
                "MAIN PHASE 2",
                state.getCurrentPhase()
        );
    }

    @Test
    void levelFourMonsterCanBeSummonedWithoutTributes() {

        DuelState state =
                createEmptyDuelState();

        DuelMonsterCard celticGuardian =
                createMonster(
                        1,
                        "Celtic Guardian",
                        4,
                        1400,
                        1200
                );

        state.getPlayerHand().add(
                celticGuardian
        );

        state.setPlayerTurn(true);
        state.setPlayerSummoned(false);
        state.setCurrentPhase(
                "MAIN PHASE 1"
        );

        duelService.summonPlayerMonster(
                state,
                celticGuardian.cardId(),
                DuelMonster.Position.ATTACK,
                null
        );

        assertEquals(
                0,
                state.getPlayerHand().size()
        );

        assertNotNull(
                state.getPlayerMonsterZones()[0]
        );

        assertEquals(
                "Celtic Guardian",
                state.getPlayerMonsterZones()[0]
                        .getCard()
                        .name()
        );

        assertTrue(
                state.isPlayerSummoned()
        );
    }

    @Test
    void levelSixMonsterCannotBeSummonedWithoutTribute() {

        DuelState state =
                createEmptyDuelState();

        DuelMonsterCard summonedSkull =
                createMonster(
                        2,
                        "Summoned Skull",
                        6,
                        2500,
                        1200
                );

        state.getPlayerHand().add(
                summonedSkull
        );

        state.setPlayerTurn(true);
        state.setPlayerSummoned(false);
        state.setCurrentPhase(
                "MAIN PHASE 1"
        );

        duelService.summonPlayerMonster(
                state,
                summonedSkull.cardId(),
                DuelMonster.Position.ATTACK,
                null
        );

        assertEquals(
                1,
                state.getPlayerHand().size()
        );

        assertNull(
                state.getPlayerMonsterZones()[0]
        );

        assertFalse(
                state.isPlayerSummoned()
        );
    }

    @Test
    void levelSixMonsterCanBeTributeSummoned() {

        DuelState state =
                createEmptyDuelState();

        DuelMonsterCard tribute =
                createMonster(
                        1,
                        "Feral Imp",
                        4,
                        1300,
                        1400
                );

        DuelMonsterCard summonedSkull =
                createMonster(
                        2,
                        "Summoned Skull",
                        6,
                        2500,
                        1200
                );

        state.getPlayerMonsterZones()[0] =
                new DuelMonster(
                        tribute,
                        DuelMonster.Position.ATTACK
                );

        state.getPlayerHand().add(
                summonedSkull
        );

        state.setPlayerTurn(true);
        state.setPlayerSummoned(false);
        state.setCurrentPhase(
                "MAIN PHASE 1"
        );

        duelService.summonPlayerMonster(
                state,
                summonedSkull.cardId(),
                DuelMonster.Position.ATTACK,
                List.of(0)
        );

        assertEquals(
                1,
                state.getPlayerGraveyard().size()
        );

        assertEquals(
                "Feral Imp",
                state.getPlayerGraveyard()
                        .getFirst()
                        .name()
        );

        assertNotNull(
                state.getPlayerMonsterZones()[0]
        );

        assertEquals(
                "Summoned Skull",
                state.getPlayerMonsterZones()[0]
                        .getCard()
                        .name()
        );

        assertEquals(
                0,
                state.getPlayerHand().size()
        );
    }

    @Test
    void directAttackReducesOpponentLifePoints() {

        DuelState state =
                createEmptyDuelState();

        DuelMonsterCard monster =
                createMonster(
                        1,
                        "Celtic Guardian",
                        4,
                        1400,
                        1200
                );

        state.getPlayerMonsterZones()[0] =
                new DuelMonster(
                        monster,
                        DuelMonster.Position.ATTACK
                );

        state.setPlayerTurn(true);
        state.setTurnNumber(2);
        state.setCurrentPhase(
                "BATTLE PHASE"
        );

        duelService.attack(
                state,
                0,
                -1
        );

        assertEquals(
                6600,
                state.getOpponentLifePoints()
        );

        assertTrue(
                state.getPlayerMonsterZones()[0]
                        .isAttacked()
        );
    }

    @Test
    void attackPositionMonsterIsDestroyedByStrongerAttack() {

        DuelState state =
                createEmptyDuelState();

        DuelMonsterCard playerMonster =
                createMonster(
                        1,
                        "Celtic Guardian",
                        4,
                        1400,
                        1200
                );

        DuelMonsterCard kaibaMonster =
                createMonster(
                        2,
                        "La Jinn",
                        4,
                        1800,
                        1000
                );

        state.getPlayerMonsterZones()[0] =
                new DuelMonster(
                        playerMonster,
                        DuelMonster.Position.ATTACK
                );

        state.getOpponentMonsterZones()[0] =
                new DuelMonster(
                        kaibaMonster,
                        DuelMonster.Position.ATTACK
                );

        state.setPlayerTurn(true);
        state.setTurnNumber(2);
        state.setCurrentPhase(
                "BATTLE PHASE"
        );

        duelService.attack(
                state,
                0,
                0
        );

        assertNull(
                state.getPlayerMonsterZones()[0]
        );

        assertNotNull(
                state.getOpponentMonsterZones()[0]
        );

        assertEquals(
                7600,
                state.getPlayerLifePoints()
        );

        assertEquals(
                1,
                state.getPlayerGraveyard().size()
        );
    }

    @Test
    void attackingHigherDefenseDealsDamageButDoesNotDestroyDefender() {

        DuelState state =
                createEmptyDuelState();

        DuelMonsterCard attacker =
                createMonster(
                        1,
                        "Celtic Guardian",
                        4,
                        1400,
                        1200
                );

        DuelMonsterCard defender =
                createMonster(
                        2,
                        "Giant Soldier of Stone",
                        3,
                        1300,
                        2000
                );

        state.getPlayerMonsterZones()[0] =
                new DuelMonster(
                        attacker,
                        DuelMonster.Position.ATTACK
                );

        state.getOpponentMonsterZones()[0] =
                new DuelMonster(
                        defender,
                        DuelMonster.Position.DEFENSE
                );

        state.setPlayerTurn(true);
        state.setTurnNumber(2);
        state.setCurrentPhase(
                "BATTLE PHASE"
        );

        duelService.attack(
                state,
                0,
                0
        );

        assertNotNull(
                state.getPlayerMonsterZones()[0]
        );

        assertNotNull(
                state.getOpponentMonsterZones()[0]
        );

        assertEquals(
                7400,
                state.getPlayerLifePoints()
        );

        assertEquals(
                0,
                state.getOpponentGraveyard().size()
        );
    }

    private DuelState createEmptyDuelState() {

        return new DuelState(
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    private List<DuelCard> createFortyCardDeck(
            String prefix
    ) {

        List<DuelCard> deck =
                new ArrayList<>();

        for (int i = 1; i <= 40; i++) {

            deck.add(
                    createMonster(
                            i,
                            prefix + " " + i,
                            4,
                            1000,
                            1000
                    )
            );
        }

        return deck;
    }

    private DuelMonsterCard createMonster(
            Integer cardId,
            String name,
            Integer level,
            Integer attack,
            Integer defense
    ) {

        return new DuelMonsterCard(
                cardId,
                name,
                "DARK",
                "Warrior",
                level,
                attack,
                defense,
                "Test monster.",
                "images/test.png"
        );
    }
}