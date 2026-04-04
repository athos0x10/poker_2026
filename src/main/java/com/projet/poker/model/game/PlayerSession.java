package main.java.com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.List;

import javax.swing.border.StrokeBorder;

import main.java.com.projet.poker.engine.Card;

public class PlayerSession {
    
    private long id;
    private long tableId;
    private long userId;
    private double currentStack;
    private int seatNumber;
    private List<Card> holeCards;
    private boolean hasFolded;
    private boolean isAllIn;

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

    public List<Card> getHoleCards() {
        return holeCards;
    }
    
    public String displayHoleCards() {
        StringBuilder s = new StringBuilder();

        for (Card c : holeCards) {
            s.append(c.toString() + " ");
        }

        return s.toString();
    }

    public long getId() {
        return userId;
    }
}
