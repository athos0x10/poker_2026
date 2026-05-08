package com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.List;

import com.projet.poker.engine.Card;
import com.projet.poker.engine.HandType;

public class PlayerSession {

    /* Un joueur (identifié dans le jeu, côté serveur) */
    
    private long id;
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

    private HandType finalHand;

    // Mise sur le tour courant
    private double betInCurrentRound;
    // Mise total permet de faire le calcul du partage du pot
    private double totalInvestedInHand;

    private boolean hasDisconnected = false;

    public PlayerSession(long id) {
        this.id = id;
        this.holeCards = new ArrayList<>();

    }

    public PlayerSession(long id, double currentStack, int seatNumber) {
        this.id = id;
        this.currentStack = currentStack;
        this.seatNumber = seatNumber;
        this.hasFolded = false;
        this.isAllIn = false;
        this.holeCards = new ArrayList<>();
    }
    
    public void addBet(double amount) {
        betInCurrentRound += amount;
        totalInvestedInHand += amount;
    }

    public void resetBet() {
        betInCurrentRound = 0;
    }
    
    public void withdraw(double amount) {
        currentStack = Math.max(0, currentStack - amount);
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

    public boolean hasDisconnected() {
        return hasDisconnected;
    }

    public double getTotalInvestedInHand() {
        return totalInvestedInHand;
    }

    public HandType getFinalHand() {
        return finalHand;
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

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setTotalInvestedInHand(double totalInvestedInHand) {
        this.totalInvestedInHand = totalInvestedInHand;
    }

    public void setFinalHand(HandType finalHand) {
        this.finalHand = finalHand;
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

    public void setHasDisconnected(boolean hasDisconnected) {
        this.hasDisconnected = hasDisconnected;
    }

    @Override
    public String toString() {
        return "Player " + id + " (Seat " + seatNumber + ", Stack: " + currentStack + ")";
    }
}
