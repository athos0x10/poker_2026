package main.java.com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.List;

import main.java.com.projet.poker.engine.Card;

public class SessionJoueur {
    
    private long id;
    private long talbleId;
    private long userId;
    private double currentStack;
    private int seatNumber;
    private List<Card> handCards;
    private boolean hasFolded;
    private boolean isAllIn;

    public SessionJoueur(long userId) {
        this.userId = userId;
    }

    public SessionJoueur(long id, long talbleId, long userId, double currentStack, int seatNumber) {
        this.id = id;
        this.talbleId = talbleId;
        this.userId = userId;
        this.currentStack = currentStack;
        this.seatNumber = seatNumber;
        this.hasFolded = false;
        this.isAllIn = false;
        this.handCards = new ArrayList<>();
    }
}
