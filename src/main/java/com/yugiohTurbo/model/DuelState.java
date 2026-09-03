package com.yugiohTurbo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DuelState implements Serializable {

    private final List<DuelCard> playerDeck;
    private final List<DuelCard> opponentDeck;

    private final List<DuelCard> playerHand;
    private final List<DuelCard> opponentHand;

    private final DuelMonster[] playerMonsterZones;
    private final DuelMonster[] opponentMonsterZones;

    private final List<DuelCard> playerGraveyard;
    private final List<DuelCard> opponentGraveyard;

    private final List<String> battleLog;

    private int playerLifePoints;
    private int opponentLifePoints;

    private boolean playerTurn;
    private boolean playerSummoned;

    private boolean finished;
    private String winner;

    private int turnNumber;

    private String currentPhase;

    public DuelState(
            List<DuelCard> playerDeck,
            List<DuelCard> opponentDeck
    ) {

        this.playerDeck = playerDeck;
        this.opponentDeck = opponentDeck;

        this.playerHand = new ArrayList<>();
        this.opponentHand = new ArrayList<>();

        this.playerMonsterZones = new DuelMonster[5];
        this.opponentMonsterZones = new DuelMonster[5];

        this.playerGraveyard = new ArrayList<>();
        this.opponentGraveyard = new ArrayList<>();

        this.battleLog = new ArrayList<>();

        this.playerLifePoints = 8000;
        this.opponentLifePoints = 8000;

        this.playerTurn = true;
        this.playerSummoned = false;

        this.finished = false;
        this.winner = null;

        this.turnNumber = 1;

        this.currentPhase = "MAIN PHASE 1";
    }

    public List<DuelCard> getPlayerDeck() {
        return playerDeck;
    }

    public List<DuelCard> getOpponentDeck() {
        return opponentDeck;
    }

    public List<DuelCard> getPlayerHand() {
        return playerHand;
    }

    public List<DuelCard> getOpponentHand() {
        return opponentHand;
    }

    public DuelMonster[] getPlayerMonsterZones() {
        return playerMonsterZones;
    }

    public DuelMonster[] getOpponentMonsterZones() {
        return opponentMonsterZones;
    }

    public List<DuelCard> getPlayerGraveyard() {
        return playerGraveyard;
    }

    public List<DuelCard> getOpponentGraveyard() {
        return opponentGraveyard;
    }

    public List<String> getBattleLog() {
        return battleLog;
    }

    public int getPlayerLifePoints() {
        return playerLifePoints;
    }

    public void setPlayerLifePoints(int playerLifePoints) {
        this.playerLifePoints = Math.max(playerLifePoints, 0);
    }

    public int getOpponentLifePoints() {
        return opponentLifePoints;
    }

    public void setOpponentLifePoints(int opponentLifePoints) {
        this.opponentLifePoints = Math.max(opponentLifePoints, 0);
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    public boolean isPlayerSummoned() {
        return playerSummoned;
    }

    public void setPlayerSummoned(boolean playerSummoned) {
        this.playerSummoned = playerSummoned;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    public boolean hasPlayerMonsters() {

        for (DuelMonster monster : playerMonsterZones) {

            if (monster != null) {
                return true;
            }
        }

        return false;
    }

    public boolean hasOpponentMonsters() {

        for (DuelMonster monster : opponentMonsterZones) {

            if (monster != null) {
                return true;
            }
        }

        return false;
    }
}