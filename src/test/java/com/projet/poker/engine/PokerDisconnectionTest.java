package com.projet.poker.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projet.poker.engine.network.mock.MockWebSocketNotifier;
import com.projet.poker.engine.network.GameRouter;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;

public class PokerDisconnectionTest {

    private PokerEngine engine;
    private GameRouter router;
    private Table table;
    private PlayerSession p0, p1, p2;

    @BeforeEach
    public void setUp() {
        engine = new PokerEngine();
        engine.setNotifier(new MockWebSocketNotifier());
        router = new GameRouter(engine);

        table = new Table(1, "Table Test Quit", 10, 3);
        p0 = new PlayerSession(0, 200, 0); // Dealer
        p1 = new PlayerSession(1, 200, 1); // Small Blind
        p2 = new PlayerSession(2, 200, 2); // Big Blind

        table.addPlayer(p0);
        table.addPlayer(p1);
        table.addPlayer(p2);
    }

    @Test
    public void testPlayerDisconnectMidHandAndIsRemovedNextHand() {
        // --- MANCHE 1 ---
        engine.startNewHand(table);
        assertEquals(3, table.getActivePlayers().size());
        assertEquals(0, table.getGameHand().getCurrentTurnIndex(), "C'est au joueur 0 (UTG) de jouer.");

        // P0 (UTG) joue normalement
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 0, \"action\": \"CALL\", \"amount\": 0}");
        assertEquals(1, table.getGameHand().getCurrentTurnIndex(), "Le tour passe à P1.");

        // CATASTROPHE : C'est au tour de P1, mais son internet coupe (QUIT) !
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 1, \"action\": \"QUIT\", \"amount\": 0}");

        // VÉRIFICATIONS IMMÉDIATES (La manche ne doit pas planter)
        assertTrue(p1.hasDisconnected(), "P1 doit être marqué comme déconnecté.");
        assertTrue(p1.hasFolded(), "P1 doit avoir été forcé au Fold.");
        assertEquals(3, table.getActivePlayers().size(), "P1 est toujours dans la liste pour ne pas casser les index actuels.");
        assertEquals(2, table.getGameHand().getCurrentTurnIndex(), "Le moteur a intelligemment passé le tour de P1 pour le donner à P2 !");

        // P2 (BB) termine la manche en checkant
        router.handleIncomingWebSocketMessage(table, "{\"playerId\": 2, \"action\": \"CHECK\", \"amount\": 0}");
        // Le jeu avance au flop, passons directement à la fin...
        
        // --- MANCHE 2 ---
        // Une nouvelle main démarre (ce qui déclenche clearTable)
        engine.startNewHand(table);

        // VÉRIFICATION DÉFINITIVE DU GRAND MÉNAGE
        assertEquals(2, table.getActivePlayers().size(), "P1 a été physiquement retiré de la table par le clearTable !");
        assertEquals(p0, table.getActivePlayers().get(0), "P0 est toujours là.");
        assertEquals(p2, table.getActivePlayers().get(1), "P2 est toujours là.");
    }
}
