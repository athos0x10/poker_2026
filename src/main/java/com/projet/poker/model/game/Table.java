package main.java.com.projet.poker.model.game;

import java.util.ArrayList;
import java.util.List;

import main.java.com.projet.poker.engine.GameState;

public class Table {
    /* Classe principale de gestion d'une partie de poker
     * Equivalent de la classe Game
    */

    private long id;
    private String name;
    // Mise minimum pour la grosse blinde (par défaut, petite blinde = grosse blinde / 2
    private double minBet;
    // Nombre max de joueurs
    private int maxPlayers;

    // Etat du jeu
    private GameState gameState;
    private List<PlayerSession> activePlayers;
    // Gagnants de la partie, il peut y en avoir plusieurs si égalité, None en cours de partie
    private List<PlayerSession> winners;
    // Informations sur la main globale 
    private GameHand gameHand;

    public Table() {
        this.activePlayers = new ArrayList<>();
        this.gameHand = new GameHand();
    }
    
    public Table(long id, String name, double minBet, int maxPlayers) {
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

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setWinners(List<PlayerSession> winners) {
        this.winners = winners;
    }

    public double getMinBet() {
        return minBet;
    }

    public List<PlayerSession> getWinners() {
        return winners;
    }

    public List<PlayerSession> getActivePlayers() {
        return activePlayers;
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameHand getGameHand() {
        return gameHand;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
