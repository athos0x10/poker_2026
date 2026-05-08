package com.projet.poker.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projet.poker.model.game.Action;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;

public class PokerEngineGameFlowTest {

    private PokerEngine engine;
    private Table table;
    private PlayerSession p1; // Siège 0 (Dealer/UTG dans les tests simplifiés)
    private PlayerSession p2; // Siège 1
    private PlayerSession p3; // Siège 2

    @BeforeEach
    public void setUp() {
        engine = new PokerEngine();
        table = new Table(1, "Test Table", 10, 3);
        
        p1 = new PlayerSession(0, 200, 0);
        p2 = new PlayerSession(1, 200, 1);
        p3 = new PlayerSession(2, 200, 2);
        
        table.addPlayer(p1);
        table.addPlayer(p2);
        table.addPlayer(p3);
    }

    /* * TEST 1 : Distribution en Cascade avec All-ins asymétriques (LE TEST ULTIME)
     */
    @Test
    public void testUnevenAllInCascadeDistribution() {
        // P1 a la MEILLEURE main (Brelan d'As) mais fait All-in avec seulement 10 jetons
        p1.getHoleCards().addAll(Arrays.asList(new Card(CardValue.AS, CardColor.COEUR), new Card(CardValue.AS, CardColor.PIQUE)));
        p1.setTotalInvestedInHand(10);
        p1.withdraw(10); // Retire les 10 de son stack

        // P2 a la DEUXIÈME main (Paire de Rois) et fait All-in à 100 jetons
        p2.getHoleCards().addAll(Arrays.asList(new Card(CardValue.ROI, CardColor.TREFLE), new Card(CardValue.ROI, CardColor.PIQUE)));
        p2.setTotalInvestedInHand(100);
        p2.withdraw(100);

        // P3 a la PIRE main (Paire de Dames) et a suivi jusqu'à 100 jetons
        p3.getHoleCards().addAll(Arrays.asList(new Card(CardValue.DAME, CardColor.CARREAU), new Card(CardValue.DAME, CardColor.PIQUE)));
        p3.setTotalInvestedInHand(100);
        p3.withdraw(100);

        // Pot total = 210
        table.getGameHand().setPotAmount(210);

        table.getGameHand().setCommunityCards(Arrays.asList(
            new Card(CardValue.AS, CardColor.TREFLE), new Card(CardValue.DEUX, CardColor.COEUR),
            new Card(CardValue.CINQ, CardColor.PIQUE), new Card(CardValue.SEPT, CardColor.TREFLE),
            new Card(CardValue.NEUF, CardColor.CARREAU)
        ));

        // On déclenche le Showdown manuellement
        engine.evaluateShowdown(table);

        // VÉRIFICATIONS (Stack initial 200)
        // P1 gagne la première tranche: il a mis 10, il prend 10 à P2 et 10 à P3 = gain de 30. Stack final: (200-10) + 30 = 220.
        assertEquals(220, p1.getCurrentStack(), "P1 doit récupérer son investissement de 10 x3 (30 au total)");
        
        // P2 gagne la 2eme tranche: il a mis 100. La tranche 1 a consommé 10. Reste 90. Il prend ses 90 + les 90 de P3 = gain de 180. Stack final: (200-100) + 180 = 280.
        assertEquals(280, p2.getCurrentStack(), "P2 gagne le side pot face à P3");
        
        // P3 perd tout ce qu'il a misé. Stack final : (200-100) = 100.
        assertEquals(100, p3.getCurrentStack(), "P3 perd ses 100 jetons investis");
    }

    /* * TEST 2 : Abandon d'un joueur en cours de manche (FOLD)
     */
    @Test
    public void testEarlyWinByFold() {
        // On initialise une main normalement via le moteur
        engine.startNewHand(table); 
        
        // Rappel: Stack initial = 200. Min bet = 10.
        // Après startNewHand :
        // P0 (Dealer) = 200
        // P1 (SB) = 195
        // P2 (BB) = 190
        // Le Pot vaut 15. Le Highest Bet vaut 10. C'est à P0 de jouer.

        // P0 se couche (FOLD)
        Action foldAction = new Action(ActionType.FOLD, p1.getId(), 0);
        engine.processAction(table, foldAction);
        assertTrue(p1.hasFolded(), "P1 doit être marqué comme couché");

        // P1 se couche (FOLD)
        Action foldAction2 = new Action(ActionType.FOLD, p2.getId(), 0);
        engine.processAction(table, foldAction2);
        assertTrue(p2.hasFolded(), "P2 doit être marqué comme couché");

        // Si tout le monde se couche sauf P3, la manche doit se terminer et P3 empoche le pot (15) !
        assertEquals(205, p3.getCurrentStack(), "P3 doit avoir récupéré la petite blinde (5) et gardé ses jetons (200)");
    }
}
