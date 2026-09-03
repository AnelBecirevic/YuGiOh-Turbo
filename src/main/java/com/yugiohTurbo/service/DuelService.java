package com.yugiohTurbo.service;

import com.yugiohTurbo.model.DuelCard;
import com.yugiohTurbo.model.DuelMonster;
import com.yugiohTurbo.model.DuelMonsterCard;
import com.yugiohTurbo.model.DuelState;
import com.yugiohTurbo.repository.DuelRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DuelService {

    private final DuelRepository duelRepository;

    public DuelService(DuelRepository duelRepository) {
        this.duelRepository = duelRepository;
    }

    public int getPlayerDeckSize(Integer accountId) {
        return duelRepository.loadPlayerDeck(accountId).size();
    }

    public DuelState startDuel(Integer accountId) {

        List<DuelCard> playerDeck =
                new ArrayList<>(
                        duelRepository.loadPlayerDeck(accountId)
                );

        if (playerDeck.size() != 40) {
            return null;
        }

        List<DuelCard> opponentDeck =
                new ArrayList<>(
                        duelRepository.loadKaibaDeck()
                );

        Collections.shuffle(playerDeck);
        Collections.shuffle(opponentDeck);

        DuelState state =
                new DuelState(
                        playerDeck,
                        opponentDeck
                );

        for (int i = 0; i < 5; i++) {
            drawPlayerCard(state);
            drawOpponentCard(state);
        }

        state.setCurrentPhase("MAIN PHASE 1");

        state.getBattleLog().add(
                "The duel begins!"
        );

        state.getBattleLog().add(
                "Turn 1 - Main Phase 1."
        );

        state.getBattleLog().add(
                "The first player cannot enter the Battle Phase."
        );

        return state;
    }

    public int requiredTributes(DuelMonsterCard card) {

        if (card.level() <= 4) {
            return 0;
        }

        if (card.level() <= 6) {
            return 1;
        }

        return 2;
    }

    public void enterBattlePhase(DuelState state) {

        if (state == null
                || state.isFinished()
                || !state.isPlayerTurn()
                || !"MAIN PHASE 1".equals(state.getCurrentPhase())) {
            return;
        }

        if (state.getTurnNumber() == 1) {

            state.getBattleLog().add(
                    "You cannot enter the Battle Phase on the first turn."
            );

            return;
        }

        state.setCurrentPhase("BATTLE PHASE");

        state.getBattleLog().add(
                "You entered the Battle Phase."
        );
    }

    public void enterMainPhase2(DuelState state) {

        if (state == null
                || state.isFinished()
                || !state.isPlayerTurn()
                || !"BATTLE PHASE".equals(state.getCurrentPhase())) {
            return;
        }

        state.setCurrentPhase("MAIN PHASE 2");

        state.getBattleLog().add(
                "You entered Main Phase 2."
        );
    }

    public void summonPlayerMonster(
            DuelState state,
            Integer cardId,
            DuelMonster.Position position,
            List<Integer> tributeZones
    ) {

        if (state == null
                || state.isFinished()
                || !state.isPlayerTurn()
                || state.isPlayerSummoned()) {
            return;
        }

        boolean validMainPhase =
                "MAIN PHASE 1".equals(state.getCurrentPhase())
                        || "MAIN PHASE 2".equals(state.getCurrentPhase());

        if (!validMainPhase) {

            state.getBattleLog().add(
                    "Monsters can only be summoned during a Main Phase."
            );

            return;
        }

        DuelCard selectedCard =
                state.getPlayerHand()
                        .stream()
                        .filter(card ->
                                card.cardId().equals(cardId))
                        .findFirst()
                        .orElse(null);

        if (selectedCard == null) {
            return;
        }

        if (!(selectedCard instanceof DuelMonsterCard monsterCard)) {

            state.getBattleLog().add(
                    selectedCard.name()
                            + " is not a Monster Card."
            );

            return;
        }

        int requiredTributes =
                requiredTributes(monsterCard);

        if (!validPlayerTributes(
                state,
                tributeZones,
                requiredTributes
        )) {

            state.getBattleLog().add(
                    monsterCard.name()
                            + " requires exactly "
                            + requiredTributes
                            + " tribute(s)."
            );

            return;
        }

        if (requiredTributes == 0
                && findEmptyZone(
                state.getPlayerMonsterZones()
        ) == -1) {

            state.getBattleLog().add(
                    "All five Monster Zones are occupied."
            );

            return;
        }

        tributeSelectedPlayerMonsters(
                state,
                tributeZones
        );

        int emptyZone =
                findEmptyZone(
                        state.getPlayerMonsterZones()
                );

        if (emptyZone == -1) {
            return;
        }

        state.getPlayerHand().remove(selectedCard);

        state.getPlayerMonsterZones()[emptyZone] =
                new DuelMonster(
                        monsterCard,
                        position
                );

        state.setPlayerSummoned(true);

        state.getBattleLog().add(
                "You summoned "
                        + monsterCard.name()
                        + " in "
                        + position.name().toLowerCase()
                        + " position."
        );
    }

    public void attack(
            DuelState state,
            int attackerZone,
            int targetZone
    ) {

        if (state == null
                || state.isFinished()
                || !state.isPlayerTurn()) {
            return;
        }

        if (!"BATTLE PHASE".equals(
                state.getCurrentPhase()
        )) {

            state.getBattleLog().add(
                    "You can only attack during the Battle Phase."
            );

            return;
        }

        if (attackerZone < 0
                || attackerZone >= 5) {
            return;
        }

        DuelMonster attacker =
                state.getPlayerMonsterZones()[attackerZone];

        if (attacker == null
                || attacker.isAttacked()) {
            return;
        }

        if (attacker.getPosition()
                == DuelMonster.Position.DEFENSE) {
            return;
        }

        if (state.hasOpponentMonsters()) {

            if (targetZone < 0
                    || targetZone >= 5
                    || state.getOpponentMonsterZones()[targetZone] == null) {
                return;
            }

            attacker.setAttacked(true);

            resolveBattle(
                    state,
                    attacker,
                    state.getOpponentMonsterZones()[targetZone],
                    attackerZone,
                    targetZone,
                    true
            );

        } else {

            attacker.setAttacked(true);

            int damage =
                    attacker.getCard().attack();

            state.setOpponentLifePoints(
                    state.getOpponentLifePoints()
                            - damage
            );

            state.getBattleLog().add(
                    attacker.getCard().name()
                            + " attacked Kaiba directly for "
                            + damage
                            + " damage."
            );
        }

        checkWinner(state);
    }

    public void endPlayerTurn(DuelState state) {

        if (state == null
                || state.isFinished()
                || !state.isPlayerTurn()) {
            return;
        }

        state.setCurrentPhase("END PHASE");

        state.getBattleLog().add(
                "You entered the End Phase."
        );

        state.setPlayerTurn(false);

        state.getBattleLog().add(
                "Kaiba's turn."
        );

        if (!drawOpponentCard(state)) {

            state.setFinished(true);
            state.setWinner("PLAYER");
            state.setCurrentPhase("DUEL FINISHED");

            state.getBattleLog().add(
                    "Kaiba cannot draw a card. You win!"
            );

            return;
        }

        performKaibaSummon(state);

        if (!state.isFinished()) {
            performKaibaBattlePhase(state);
        }

        if (!state.isFinished()) {
            startPlayerTurn(state);
        }
    }

    private void startPlayerTurn(DuelState state) {

        state.setPlayerTurn(true);
        state.setPlayerSummoned(false);

        state.setTurnNumber(
                state.getTurnNumber() + 1
        );

        resetAttacks(
                state.getPlayerMonsterZones()
        );

        state.setCurrentPhase("DRAW PHASE");

        if (!drawPlayerCard(state)) {

            state.setFinished(true);
            state.setWinner("KAIBA");
            state.setCurrentPhase("DUEL FINISHED");

            state.getBattleLog().add(
                    "You cannot draw a card. Kaiba wins!"
            );

            return;
        }

        state.setCurrentPhase("MAIN PHASE 1");

        state.getBattleLog().add(
                "Turn "
                        + state.getTurnNumber()
                        + " - Main Phase 1."
        );
    }

    /*
     * KAIBA AI
     *
     * 1. Find all monsters that Kaiba can legally summon.
     * 2. If the player's field is empty, use strongest ATK.
     * 3. If Kaiba has a monster capable of beating the
     *    player's strongest monster, summon the strongest
     *    attacker in Attack Position.
     * 4. Otherwise summon the legal monster with the highest
     *    DEF in Defense Position.
     */
    private void performKaibaSummon(DuelState state) {

        int monstersOnField =
                countMonsters(
                        state.getOpponentMonsterZones()
                );

        List<DuelMonsterCard> legalCards =
                state.getOpponentHand()
                        .stream()
                        .filter(DuelMonsterCard.class::isInstance)
                        .map(DuelMonsterCard.class::cast)
                        .filter(card ->
                                requiredTributes(card)
                                        <= monstersOnField)
                        .toList();

        if (legalCards.isEmpty()) {

            state.getBattleLog().add(
                    "Kaiba has no monster he can legally summon."
            );

            return;
        }

        DuelMonsterCard chosenCard;
        DuelMonster.Position chosenPosition;

        if (!state.hasPlayerMonsters()) {

            chosenCard =
                    legalCards.stream()
                            .max(
                                    Comparator.comparingInt(
                                            DuelMonsterCard::attack
                                    )
                            )
                            .orElse(null);

            chosenPosition =
                    DuelMonster.Position.ATTACK;

        } else {

            int strongestPlayerPower =
                    strongestPlayerMonsterPower(state);

            DuelMonsterCard strongestAttacker =
                    legalCards.stream()
                            .max(
                                    Comparator.comparingInt(
                                            DuelMonsterCard::attack
                                    )
                            )
                            .orElse(null);

            if (strongestAttacker != null
                    && strongestAttacker.attack()
                    > strongestPlayerPower) {

                chosenCard =
                        strongestAttacker;

                chosenPosition =
                        DuelMonster.Position.ATTACK;

            } else {

                chosenCard =
                        legalCards.stream()
                                .max(
                                        Comparator.comparingInt(
                                                DuelMonsterCard::defense
                                        )
                                )
                                .orElse(null);

                chosenPosition =
                        DuelMonster.Position.DEFENSE;
            }
        }

        if (chosenCard == null) {
            return;
        }

        int requiredTributes =
                requiredTributes(chosenCard);

        if (requiredTributes == 0
                && findEmptyZone(
                state.getOpponentMonsterZones()
        ) == -1) {
            return;
        }

        tributeFirstOpponentMonsters(
                state,
                requiredTributes
        );

        int emptyZone =
                findEmptyZone(
                        state.getOpponentMonsterZones()
                );

        if (emptyZone == -1) {
            return;
        }

        state.getOpponentHand().remove(chosenCard);

        state.getOpponentMonsterZones()[emptyZone] =
                new DuelMonster(
                        chosenCard,
                        chosenPosition
                );

        state.getBattleLog().add(
                "Kaiba summoned "
                        + chosenCard.name()
                        + " in "
                        + chosenPosition.name().toLowerCase()
                        + " position."
        );
    }

    private int strongestPlayerMonsterPower(
            DuelState state
    ) {

        int strongestPower = 0;

        for (DuelMonster monster :
                state.getPlayerMonsterZones()) {

            if (monster == null) {
                continue;
            }

            int power;

            if (monster.getPosition()
                    == DuelMonster.Position.ATTACK) {

                power =
                        monster.getCard().attack();

            } else {

                power =
                        monster.getCard().defense();
            }

            if (power > strongestPower) {
                strongestPower = power;
            }
        }

        return strongestPower;
    }

    private void performKaibaBattlePhase(
            DuelState state
    ) {

        for (int attackerZone = 0;
             attackerZone < 5;
             attackerZone++) {

            if (state.isFinished()) {
                return;
            }

            DuelMonster attacker =
                    state.getOpponentMonsterZones()[attackerZone];

            if (attacker == null
                    || attacker.getPosition()
                    == DuelMonster.Position.DEFENSE) {
                continue;
            }

            if (!state.hasPlayerMonsters()) {

                int damage =
                        attacker.getCard().attack();

                state.setPlayerLifePoints(
                        state.getPlayerLifePoints()
                                - damage
                );

                state.getBattleLog().add(
                        "Kaiba's "
                                + attacker.getCard().name()
                                + " attacked directly for "
                                + damage
                                + " damage."
                );

                checkWinner(state);

                continue;
            }

            int targetZone =
                    chooseKaibaTarget(state);

            if (targetZone != -1) {

                resolveBattle(
                        state,
                        attacker,
                        state.getPlayerMonsterZones()[targetZone],
                        attackerZone,
                        targetZone,
                        false
                );

                checkWinner(state);
            }
        }
    }

    private int chooseKaibaTarget(
            DuelState state
    ) {

        int chosenZone = -1;
        int lowestPower = Integer.MAX_VALUE;

        for (int i = 0; i < 5; i++) {

            DuelMonster monster =
                    state.getPlayerMonsterZones()[i];

            if (monster == null) {
                continue;
            }

            int power;

            if (monster.getPosition()
                    == DuelMonster.Position.ATTACK) {

                power =
                        monster.getCard().attack();

            } else {

                power =
                        monster.getCard().defense();
            }

            if (power < lowestPower) {

                lowestPower = power;
                chosenZone = i;
            }
        }

        return chosenZone;
    }

    private void resolveBattle(
            DuelState state,
            DuelMonster attacker,
            DuelMonster defender,
            int attackerZone,
            int defenderZone,
            boolean playerAttacking
    ) {

        int attackValue =
                attacker.getCard().attack();

        state.getBattleLog().add(
                (playerAttacking
                        ? "Your "
                        : "Kaiba's ")
                        + attacker.getCard().name()
                        + " attacks "
                        + defender.getCard().name()
                        + "."
        );

        if (defender.getPosition()
                == DuelMonster.Position.ATTACK) {

            int defenderAttack =
                    defender.getCard().attack();

            if (attackValue > defenderAttack) {

                destroyDefender(
                        state,
                        defenderZone,
                        playerAttacking
                );

                damageDefendingPlayer(
                        state,
                        attackValue - defenderAttack,
                        playerAttacking
                );

            } else if (attackValue < defenderAttack) {

                destroyAttacker(
                        state,
                        attackerZone,
                        playerAttacking
                );

                damageAttackingPlayer(
                        state,
                        defenderAttack - attackValue,
                        playerAttacking
                );

            } else {

                destroyAttacker(
                        state,
                        attackerZone,
                        playerAttacking
                );

                destroyDefender(
                        state,
                        defenderZone,
                        playerAttacking
                );

                state.getBattleLog().add(
                        "Both monsters were destroyed."
                );
            }

        } else {

            int defenderDefense =
                    defender.getCard().defense();

            if (attackValue > defenderDefense) {

                destroyDefender(
                        state,
                        defenderZone,
                        playerAttacking
                );

                state.getBattleLog().add(
                        defender.getCard().name()
                                + " was destroyed."
                );

            } else if (attackValue < defenderDefense) {

                int damage =
                        defenderDefense - attackValue;

                damageAttackingPlayer(
                        state,
                        damage,
                        playerAttacking
                );

                state.getBattleLog().add(
                        "The defending monster's DEF was higher."
                );

            } else {

                state.getBattleLog().add(
                        "ATK and DEF were equal."
                );
            }
        }
    }

    private boolean validPlayerTributes(
            DuelState state,
            List<Integer> tributeZones,
            int requiredTributes
    ) {

        if (requiredTributes == 0) {

            return tributeZones == null
                    || tributeZones.isEmpty();
        }

        if (tributeZones == null
                || tributeZones.size()
                != requiredTributes) {
            return false;
        }

        Set<Integer> uniqueZones =
                new HashSet<>(tributeZones);

        if (uniqueZones.size()
                != requiredTributes) {
            return false;
        }

        for (Integer zone : uniqueZones) {

            if (zone == null
                    || zone < 0
                    || zone >= 5
                    || state.getPlayerMonsterZones()[zone]
                    == null) {

                return false;
            }
        }

        return true;
    }

    private void tributeSelectedPlayerMonsters(
            DuelState state,
            List<Integer> tributeZones
    ) {

        if (tributeZones == null) {
            return;
        }

        for (Integer zone : tributeZones) {

            DuelMonster monster =
                    state.getPlayerMonsterZones()[zone];

            if (monster == null) {
                continue;
            }

            state.getPlayerGraveyard()
                    .add(monster.getCard());

            state.getPlayerMonsterZones()[zone] =
                    null;

            state.getBattleLog().add(
                    "You tributed "
                            + monster.getCard().name()
                            + "."
            );
        }
    }

    private void tributeFirstOpponentMonsters(
            DuelState state,
            int amount
    ) {

        int tributed = 0;

        for (int i = 0;
             i < state.getOpponentMonsterZones().length
                     && tributed < amount;
             i++) {

            DuelMonster monster =
                    state.getOpponentMonsterZones()[i];

            if (monster == null) {
                continue;
            }

            state.getOpponentGraveyard()
                    .add(monster.getCard());

            state.getOpponentMonsterZones()[i] =
                    null;

            tributed++;

            state.getBattleLog().add(
                    "Kaiba tributed "
                            + monster.getCard().name()
                            + "."
            );
        }
    }

    private void destroyAttacker(
            DuelState state,
            int zone,
            boolean playerAttacking
    ) {

        if (playerAttacking) {

            DuelMonster monster =
                    state.getPlayerMonsterZones()[zone];

            if (monster != null) {

                state.getPlayerGraveyard()
                        .add(monster.getCard());

                state.getPlayerMonsterZones()[zone] =
                        null;
            }

        } else {

            DuelMonster monster =
                    state.getOpponentMonsterZones()[zone];

            if (monster != null) {

                state.getOpponentGraveyard()
                        .add(monster.getCard());

                state.getOpponentMonsterZones()[zone] =
                        null;
            }
        }
    }

    private void destroyDefender(
            DuelState state,
            int zone,
            boolean playerAttacking
    ) {

        if (playerAttacking) {

            DuelMonster monster =
                    state.getOpponentMonsterZones()[zone];

            if (monster != null) {

                state.getOpponentGraveyard()
                        .add(monster.getCard());

                state.getOpponentMonsterZones()[zone] =
                        null;
            }

        } else {

            DuelMonster monster =
                    state.getPlayerMonsterZones()[zone];

            if (monster != null) {

                state.getPlayerGraveyard()
                        .add(monster.getCard());

                state.getPlayerMonsterZones()[zone] =
                        null;
            }
        }
    }

    private void damageAttackingPlayer(
            DuelState state,
            int damage,
            boolean playerAttacking
    ) {

        if (playerAttacking) {

            state.setPlayerLifePoints(
                    state.getPlayerLifePoints()
                            - damage
            );

            state.getBattleLog().add(
                    "You took "
                            + damage
                            + " battle damage."
            );

        } else {

            state.setOpponentLifePoints(
                    state.getOpponentLifePoints()
                            - damage
            );

            state.getBattleLog().add(
                    "Kaiba took "
                            + damage
                            + " battle damage."
            );
        }
    }

    private void damageDefendingPlayer(
            DuelState state,
            int damage,
            boolean playerAttacking
    ) {

        if (playerAttacking) {

            state.setOpponentLifePoints(
                    state.getOpponentLifePoints()
                            - damage
            );

            state.getBattleLog().add(
                    "Kaiba took "
                            + damage
                            + " battle damage."
            );

        } else {

            state.setPlayerLifePoints(
                    state.getPlayerLifePoints()
                            - damage
            );

            state.getBattleLog().add(
                    "You took "
                            + damage
                            + " battle damage."
            );
        }
    }

    private boolean drawPlayerCard(
            DuelState state
    ) {

        if (state.getPlayerDeck().isEmpty()) {
            return false;
        }

        state.getPlayerHand().add(
                state.getPlayerDeck().remove(0)
        );

        return true;
    }

    private boolean drawOpponentCard(
            DuelState state
    ) {

        if (state.getOpponentDeck().isEmpty()) {
            return false;
        }

        state.getOpponentHand().add(
                state.getOpponentDeck().remove(0)
        );

        return true;
    }

    private int countMonsters(
            DuelMonster[] zones
    ) {

        int count = 0;

        for (DuelMonster monster : zones) {

            if (monster != null) {
                count++;
            }
        }

        return count;
    }

    private int findEmptyZone(
            DuelMonster[] zones
    ) {

        for (int i = 0;
             i < zones.length;
             i++) {

            if (zones[i] == null) {
                return i;
            }
        }

        return -1;
    }

    private void resetAttacks(
            DuelMonster[] zones
    ) {

        for (DuelMonster monster : zones) {

            if (monster != null) {
                monster.setAttacked(false);
            }
        }
    }

    private void checkWinner(
            DuelState state
    ) {

        if (state.getOpponentLifePoints() <= 0) {

            state.setFinished(true);
            state.setWinner("PLAYER");
            state.setCurrentPhase(
                    "DUEL FINISHED"
            );

            state.getBattleLog().add(
                    "Kaiba's Life Points reached 0. You win!"
            );

        } else if (state.getPlayerLifePoints() <= 0) {

            state.setFinished(true);
            state.setWinner("KAIBA");
            state.setCurrentPhase(
                    "DUEL FINISHED"
            );

            state.getBattleLog().add(
                    "Your Life Points reached 0. Kaiba wins!"
            );
        }
    }
}