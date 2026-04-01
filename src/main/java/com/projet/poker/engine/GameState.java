package main.java.com.projet.poker.engine;

public enum GameState {
    /* L'état du jeu */
    WAITING_FOR_PLAYERS,
    PRE_FLOP,
    FLOP,   
    TURN,
    RIVER,
    SHOWDOWN;
}
