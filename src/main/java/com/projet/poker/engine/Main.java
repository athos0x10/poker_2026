package com.projet.poker.engine;

import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;

public class Main {

    /* Sans jouer une partie, permet de tester en utilisant toutes les fonctionnalités 
     * que l'algorithme d'évaluation fonctionne (à appeler dans le main)
    */
    public void launchRandomEvaluation() {
        Table table = new Table();
        PokerEngine engine = new PokerEngine();

        for (int i = 0; i < 5; i++) table.addPlayer(new PlayerSession(i));

        engine.startNewHand(table);
        engine.dealFlop(table);
        engine.dealTurn(table);
        engine.dealRiver(table);

        engine.getLogger().logCommunityCards(table);
        engine.getLogger().logPlayerHands(table);

        engine.evaluateShowdown(table);

        engine.getLogger().logWinners(table);
    }
    

    public static void main(String[] args) {
        PokerEngine engine = new PokerEngine();
        engine.start();
    }
    
}
