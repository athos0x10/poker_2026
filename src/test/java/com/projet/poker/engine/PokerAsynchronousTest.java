package com.projet.poker.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projet.poker.engine.network.mock.MockWebSocketNotifier;
import com.projet.poker.engine.network.GameNotifier;
import com.projet.poker.engine.network.GameRouter;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;


public class PokerAsynchronousTest {

    private PokerEngine engine;
    private GameRouter router;
    private Table table;
    private PlayerSession p0, p1, p2;

    @BeforeEach
    public void setUp() {
        engine = new PokerEngine();
        engine.setNotifier(new MockWebSocketNotifier()); // Pour logger les JSON sans bloquer
        router = new GameRouter(engine);

        // Création de la table avec 3 joueurs à 200 jetons. Min bet = 10.
        table = new Table(1, "Table VIP", 10, 3);
        p0 = new PlayerSession(0, 200, 0); // Dealer
        p1 = new PlayerSession(1, 200, 1); // Small Blind (paie 5)
        p2 = new PlayerSession(2, 200, 2); // Big Blind (paie 10)

        table.addPlayer(p0);
        table.addPlayer(p1);
        table.addPlayer(p2);
    }

    @Test
    public void testRealisticGameFlowWithQuittingPlayer() {
        // 1. DÉMARRAGE
        engine.startNewHand(table);
        
        // Vérifications initiales : prélèvement des blindes
        assertEquals(200, p0.getCurrentStack(), "Le dealer ne paie pas de blinde.");
        assertEquals(195, p1.getCurrentStack(), "P1 paie la petite blinde (5).");
        assertEquals(190, p2.getCurrentStack(), "P2 paie la grosse blinde (10).");
        assertEquals(15, table.getGameHand().getPotAmount(), "Le pot initial doit être de 15.");
        assertEquals(0, table.getGameHand().getCurrentTurnIndex(), "C'est au joueur 0 (UTG) de jouer.");

        // 2. ACTION INVALIDE : P1 essaie de jouer hors de son tour
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 1, \"action\": \"CALL\", \"amount\": 0}");
        
        // Vérification : le jeu est bloqué, la requête est refusée, le tour n'a pas changé
        assertEquals(0, table.getGameHand().getCurrentTurnIndex(), "Le tour doit rester à 0 après une action hors-tour.");
        assertEquals(195, p1.getCurrentStack(), "Le stack de P1 n'a pas dû changer.");

        // 3. ACTION VALIDE : P0 suit (CALL)
        // Le Highest Bet est à 10 (Grosse blinde). P0 doit payer 10.
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 0, \"action\": \"CALL\", \"amount\": 0}");
        
        assertEquals(190, p0.getCurrentStack(), "P0 a payé 10 pour suivre.");
        assertEquals(25, table.getGameHand().getPotAmount(), "Le pot passe à 25.");
        assertEquals(1, table.getGameHand().getCurrentTurnIndex(), "C'est maintenant au tour du joueur 1.");

        // 4. ACTION VALIDE : P1 relance (RAISE) à 30
        // P1 a déjà mis 5. La mise monte à 30. Il doit rajouter 25.
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 1, \"action\": \"RAISE\", \"amount\": 30}");
        
        assertEquals(170, p1.getCurrentStack(), "P1 a rajouté 25 (195 - 25 = 170).");
        assertEquals(50, table.getGameHand().getPotAmount(), "Le pot passe à 50 (25 précédents + 25 de relance).");
        assertEquals(2, table.getGameHand().getCurrentTurnIndex(), "C'est au tour du joueur 2.");

        // 5. COUP DE THÉÂTRE : P2 quitte la partie !
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 2, \"action\": \"QUIT\", \"amount\": 0}");
        
        assertTrue(p2.hasFolded(), "Le joueur qui quitte doit être forcé de FOLD.");
        assertEquals(0, table.getGameHand().getCurrentTurnIndex(), "Le tour doit revenir au joueur 0 car il doit répondre à la relance de P1.");

        // 6. FIN DE MANCHE : P0 décide de se coucher (FOLD) face à la relance
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 0, \"action\": \"FOLD\", \"amount\": 0}");
        
        assertTrue(p0.hasFolded(), "P0 s'est couché.");
        
        // 7. VÉRIFICATIONS FINALES : Victoire automatique de P1
        // P1 est le seul joueur n'ayant pas Fold. Il gagne automatiquement le pot.
        // Stack final attendu pour P1 = Ses 170 jetons restants + le pot de 50 = 220.
        assertEquals(GameState.SHOWDOWN, table.getGameState(), "La main doit être terminée (Showdown).");
        assertEquals(0, table.getGameHand().getPotAmount(), "Le pot doit avoir été vidé et distribué.");
        assertEquals(220, p1.getCurrentStack(), "P1 doit avoir remporté l'intégralité du pot.");
        assertEquals(190, p0.getCurrentStack(), "P0 a perdu ses 10 jetons investis.");
        assertEquals(190, p2.getCurrentStack(), "P2 a perdu ses 10 jetons de grosse blinde avant de quitter.");
        
        // Vérification de la conservation de l'argent (masse totale = 600)[cite: 9]
        double totalMoney = p0.getCurrentStack() + p1.getCurrentStack() + p2.getCurrentStack();
        assertEquals(600.0, totalMoney, 0.001, "La somme des stacks finaux doit correspondre aux 600 jetons de départ.");
    }

    private class GameNotifierSpy implements GameNotifier {
        @SuppressWarnings("unused")
        public int boardUpdateCount = 0;
        public int handCardsCount = 0;
        public int ackCount = 0;
        public int turnCount = 0;
        public int potUpdateCount = 0;
        public int quitCount = 0;
        public int showdownCount = 0;
        public int fullInfosCount = 0;
        public boolean lastAckStatus = false;

        @Override public void sendHandCards(long pId, List<Card> c) { handCardsCount++; }
        @Override public void notifyActionAck(long pId, boolean ok, String msg) { ackCount++; lastAckStatus = ok; }
        @Override public void notifyPlayerTurn(long pId) { turnCount++; }
        @Override public void sendFullGameInfos(long pId, Table t) { fullInfosCount++; }
        @Override public void sendBestCurrentCombination(long pId, List<Card> c, String t) {}
        @Override public void broadcastBoardUpdate(List<PlayerSession> p, List<Card> c) { boardUpdateCount++; }
        @Override public void broadcastBetAndStackUpdate(List<PlayerSession> p, long id, double b, double s) {}
        @Override public void broadcastPotUpdate(List<PlayerSession> p, double a) { potUpdateCount++; }
        @Override public void broadcastPlayerQuit(List<PlayerSession> p, long id) { quitCount++; }
        @Override public void broadcastShowdown(List<PlayerSession> p, List<Card> b, List<PlayerSession> w) { showdownCount++; }
    }

    @Test
    public void testAllNotifierFunctions() {
        GameNotifierSpy spy = new GameNotifierSpy();
        engine.setNotifier(spy);

        // --- TEST : sendHandCards, notifyPlayerTurn, sendFullGameInfos ---
        engine.startNewHand(table);
        
        // startNewHand distribue à 3 joueurs
        assertEquals(3, spy.handCardsCount, "Chaque joueur doit recevoir ses cartes.");
        assertEquals(3, spy.fullInfosCount, "Chaque joueur doit recevoir l'état complet au début.");
        assertEquals(1, spy.turnCount, "Le premier joueur doit être notifié.");

        // --- TEST : notifyActionAck (ECHEC) ---
        // P1 essaie de jouer alors que c'est à P0
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 1, \"action\": \"CALL\", \"amount\": 0}");
        assertEquals(1, spy.ackCount);
        assertFalse(spy.lastAckStatus, "L'ACK doit être négatif pour un mauvais tour.");

        // --- TEST : notifyActionAck (SUCCES) & broadcastPotUpdate ---
        // P0 joue correctement
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 0, \"action\": \"CALL\", \"amount\": 0}");
        assertTrue(spy.lastAckStatus, "L'ACK doit être positif.");
        assertTrue(spy.potUpdateCount > 0, "Le pot doit être mis à jour après un CALL.");

        // --- TEST : broadcastPlayerQuit ---
        // On simule un départ via le router
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 1, \"action\": \"QUIT\", \"amount\": 0}");
        assertEquals(1, spy.quitCount, "Le notifier doit diffuser le départ du joueur.");

        // --- TEST : broadcastShowdown ---
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 2, \"action\": \"FOLD\", \"amount\": 0}");
        assertEquals(1, spy.showdownCount, "Le showdown doit être diffusé à la fin de la main.");
    }
}
