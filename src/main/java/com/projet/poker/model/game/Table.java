package main.java.com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.List;

import main.java.com.projet.poker.engine.GameState;

public class Table {

    private Long id;
    private String name;
    private double minBet;
    private int maxPlayers;
    private GameState gameState;
    private List<SessionJoueur> activePlayers;
    private Hand currentHand;

    public Table() {
        this.activePlayers = new ArrayList<>();
        this.currentHand = new Hand();
    }
    
    public Table(Long id, String name, double minBet, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.minBet = minBet;
        this.maxPlayers = maxPlayers;
        this.gameState = GameState.WAITING_FOR_PLAYERS;
        this.activePlayers = new ArrayList<>();
        this.currentHand = new Hand();
    }

    public void addPlayer(SessionJoueur p) {
        activePlayers.add(p);
    }

    public Hand getHand() {
        return currentHand;
    }
}
