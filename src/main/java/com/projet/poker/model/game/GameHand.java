package main.java.com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.java.com.projet.poker.engine.Card;
import main.java.com.projet.poker.engine.Deck;

public class GameHand {
    /* Classe représentant une manche / partie de poker 
     * (au début de laquelle on redistribue les cartes, et à la fin de laquelle on détermine le gagnant)
     */
    
    private Long id;
    private Long tableId;
    private double potAmount;
    private Deck deck;
    private List<Card> communityCards;
    private Date startTime;
    private int currentTurnIndex;
    private PlayerSession dealerButton;
    private double highestBet;

    public GameHand() {
        this.deck = new Deck();
        this.communityCards = new ArrayList<>();
    }

    public GameHand(Long id, Long tableId, double potAmount, PlayerSession dealerButton) {
        this.id = id;
        this.tableId = tableId;
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

    public void setPotAmount(double potAmount) {
        this.potAmount = potAmount;
    }

    public void setCurrentTurnIndex(int currentTurnIndex) {
        this.currentTurnIndex = currentTurnIndex;
    }

    public void setHighestBet(double highestBet) {
        this.highestBet = highestBet;
    }

    public void addToPot(double amount) {
        potAmount += amount;
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
