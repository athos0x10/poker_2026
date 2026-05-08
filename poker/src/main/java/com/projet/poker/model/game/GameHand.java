package com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.projet.poker.engine.Card;
import com.projet.poker.engine.Deck;

public class GameHand {
    /* Classe représentant une manche / partie de poker 
     * (au début de laquelle on redistribue les cartes, et à la fin de laquelle on détermine le gagnant)
     */
    
    private Date startTime;
    private double smallBlindAmount;
    private double bigBlindAmount;
    private double potAmount;
    private int currentTurnIndex;
    private double highestBet;
    private Deck deck;
    private List<Card> communityCards;
    private PlayerSession dealerButton;
    private PlayerSession smallBlindPlayer;
    private PlayerSession bigBlindPlayer;

    public GameHand() {
        this.deck = new Deck();
        this.communityCards = new ArrayList<>();
    }

    public GameHand(double potAmount, PlayerSession dealerButton) {
        this.potAmount = potAmount;
        this.communityCards = new ArrayList<>();
        this.deck = new Deck();
        this.startTime = new Date(System.currentTimeMillis());
        this.currentTurnIndex = 0;
        this.dealerButton = dealerButton;
    }

    public void shuffleDeck() {
        deck.shuffle();
    }

    public List<Card> drawCards(int n) {
        return deck.drawCards(n);
    }

    public Card drawCard() {
        return deck.drawCard();
    }

    public void burnCard() {
        deck.burnCard();
    }

    public void resetDeck() {
        this.deck = new Deck();
    }

    public void setPotAmount(double potAmount) {
        this.potAmount = potAmount;
    }

    public void setCurrentTurnIndex(int currentTurnIndex) {
        this.currentTurnIndex = currentTurnIndex;
    }

    public void setHighestBet(double highestBet) {
        this.highestBet = highestBet;
    }

    public void setDealerButton(PlayerSession dealerButton) {
        this.dealerButton = dealerButton;
    }

    public void setBigBlindPlayer(PlayerSession bigBlindPlayer) {
        this.bigBlindPlayer = bigBlindPlayer;
    }

    public void setSmallBlindPlayer(PlayerSession smallBlindPlayer) {
        this.smallBlindPlayer = smallBlindPlayer;
    }

    public void setSmallBlindAmount(double smallBlindAmount) {
        this.smallBlindAmount = smallBlindAmount;
    }

    public void setBigBlindAmount(double bigBlindAmount) {
        this.bigBlindAmount = bigBlindAmount;
    }

    public void addToPot(double amount) {
        potAmount += amount;
    }

    public PlayerSession getBigBlindPlayer() {
        return bigBlindPlayer;
    }

    public PlayerSession getSmallBlindPlayer() {
        return smallBlindPlayer;
    }

    public double getBigBlindAmount() {
        return bigBlindAmount;
    }

    public double getSmallBlindAmount() {
        return smallBlindAmount;
    }

    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    public PlayerSession getDealerButton() {
        return dealerButton;
    }

    public double getHighestBet() {
        return highestBet;
    }

    public double getPotAmount() {
        return potAmount;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public void setCommunityCards(List<Card> communityCards) {
        this.communityCards = communityCards;
    }
}
