package main.java.com.projet.poker.engine;

import java.util.List;

import main.java.com.projet.poker.model.game.SessionJoueur;
import main.java.com.projet.poker.model.game.Table;

public class PokerEngine {

    public PokerEngine() {}

    public static void main(String[] args) {
        Table t = new Table();
        t.addPlayer(new SessionJoueur(1));
        t.addPlayer(new SessionJoueur(2));

        t.getHand().getDeck().shuffle();
        System.out.println(t.getHand().getDeck());
    }
    
}
