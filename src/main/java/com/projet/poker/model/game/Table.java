package main.java.com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.List;

import main.java.com.projet.poker.engine.Card;
import main.java.com.projet.poker.engine.GameState;

public class Table {

    private Long id;
    private String name;
    private double minBet;
    private int maxPlayers;
    private GameState gameState;
    private List<PlayerSession> activePlayers;
    private GameHand gameHand;

    public Table() {
        this.activePlayers = new ArrayList<>();
        this.gameHand = new GameHand();
    }
    
    public Table(Long id, String name, double minBet, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.minBet = minBet;
        this.maxPlayers = maxPlayers;
        this.gameState = GameState.WAITING_FOR_PLAYERS;
        this.activePlayers = new ArrayList<>();
        this.gameHand = new GameHand();
    }

    public void addPlayer(PlayerSession p) {
        activePlayers.add(p);
    }

    public List<PlayerSession> getActivePlayers() {
        return activePlayers;
    }

    public GameHand getHand() {
        return gameHand;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameHand getGameHand() {
        return gameHand;
    }

    public void showTableInfo() {
        System.out.println("Table: " + name);
        System.out.println("Min Bet: " + minBet);
        System.out.println("Max Players: " + maxPlayers);
        System.out.println("Current Players: " + activePlayers.size());
    }

    public void showPlayerHands() {
        for (PlayerSession player : activePlayers) {
            System.out.println("Player " + player.getId() + ": " + player.displayHoleCards());
        }
    }

    public void showPlayers(List<PlayerSession> players) {
        for (PlayerSession p : players) {
            System.out.println("Player " + p.getId());
        }
    }

    public void showGameHand() {
        StringBuilder s = new StringBuilder("GameHand: ");

        for (Card c : gameHand.getCommunityCards()) {
            s.append(c.toString() + " ");
        }
        
        System.out.println(s.toString());
    }
}
