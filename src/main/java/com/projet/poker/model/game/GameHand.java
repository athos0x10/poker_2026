package main.java.com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.java.com.projet.poker.engine.Card;
import main.java.com.projet.poker.engine.Deck;

public class GameHand {
    
    private Long id;
    private Long tableId;
    private double potAmount;
    private Deck deck;
    private List<Card> communityCards;
    Date startTime;
    int currentTurnIndex;
    PlayerSession dealerButton;

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
