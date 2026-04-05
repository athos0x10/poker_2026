package main.java.com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.List;

import main.java.com.projet.poker.engine.Card;

public class PlayerSession {
    
    private long id;
    private long tableId;
    private long userId;
    private int seatNumber;

    // Cartes en main
    private List<Card> holeCards;
    // Jetons (ou monnaie)
    private double currentStack;

    // Le joueur s'est couché
    private boolean hasFolded;
    // Le joueur a all-in
    private boolean isAllIn;
    // Le joueur à joué dans le tour (qu'il ait suivi, relancé ou checké)
    private boolean hasActed;

    // Mise sur le tour courant
    private double betInCurrentRound;

    public PlayerSession(long userId) {
        this.userId = userId;
        this.holeCards = new ArrayList<>();

    }

    public PlayerSession(long id, long talbleId, long userId, double currentStack, int seatNumber) {
        this.id = id;
        this.tableId = talbleId;
        this.userId = userId;
        this.currentStack = currentStack;
        this.seatNumber = seatNumber;
        this.hasFolded = false;
        this.isAllIn = false;
        this.holeCards = new ArrayList<>();
    }
    
    public void addBet(double amount) {
        betInCurrentRound += amount;
    }

    public void resetBet() {
        betInCurrentRound = 0;
    }
    
    public void withdraw(double amount) {
        currentStack = Math.min(0, currentStack - amount);
    }

    public void deposit(double amount) {
        currentStack += amount;
    }

    public void bet(double amount) {
        addBet(amount);
        withdraw(amount);
    }

    public boolean hasFolded() {
        return hasFolded;
    }

    public boolean isAllIn() {
        return isAllIn;
    }

    public boolean hasActed() {
        return hasActed;
    }

    public double getBetInCurrentRound() {
        return betInCurrentRound;
    }

    public double getCurrentStack() {
        return currentStack;
    }

    public List<Card> getHoleCards() {
        return holeCards;
    }
    
    public long getId() {
        return id;
    }

    public void setAllIn(boolean isAllIn) {
        this.isAllIn = isAllIn;
    }

    public void setHasActed(boolean hasActed) {
        this.hasActed = hasActed;
    }

    public void setHasFolded(boolean hasFolded) {
        this.hasFolded = hasFolded;
    }

    
    public String displayHoleCards() {
        StringBuilder s = new StringBuilder();

        for (Card c : holeCards) {
            s.append(c.toString() + " ");
        }

        return s.toString();
    }

    @Override
    public String toString() {
        return "PlayerSession{" +
                "id=" + id +
                ", tableId=" + tableId +
                ", userId=" + userId +
                ", currentStack=" + currentStack +
                ", seatNumber=" + seatNumber +
                ", holeCards=" + displayHoleCards() +
                ", hasFolded=" + hasFolded +
                ", isAllIn=" + isAllIn +
                ", betInCurrentRound=" + betInCurrentRound +
                '}';
    }
}
