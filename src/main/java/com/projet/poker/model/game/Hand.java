package main.java.com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.java.com.projet.poker.engine.Card;
import main.java.com.projet.poker.engine.Deck;

public class Hand {
    
    private Long id;
    private Long tableId;
    private double potAmount;
    private Deck communityCards;
    Date startTime;
    int currentTurnIndex;
    SessionJoueur dealerButton;

    public Hand() {
        this.communityCards = new Deck();
    }

    public Hand(Long id, Long tableId, double potAmount, SessionJoueur dealerButton) {
        this.id = id;
        this.tableId = tableId;
        this.potAmount = potAmount;
        this.communityCards = new Deck();
        this.startTime = new Date(System.currentTimeMillis());
        this.currentTurnIndex = 0;
        this.dealerButton = dealerButton;
    }

    public Deck getDeck() {
        return communityCards;
    }
}
