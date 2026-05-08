package com.projet.poker.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;

public class PokerEngineEvaluationTest {

    private PokerEngine engine;
    private Table table;
    private PlayerSession p1;
    private PlayerSession p2;

    @BeforeEach
    public void setUp() {
        engine = new PokerEngine();
        table = new Table(1, "Test Table", 10, 2);
        
        p1 = new PlayerSession(1, 200, 1);
        p2 = new PlayerSession(2, 200, 2);
        
        table.addPlayer(p1);
        table.addPlayer(p2);
        
        table.getGameHand().setPotAmount(100); 
        p1.setTotalInvestedInHand(50);
        p2.setTotalInvestedInHand(50);
    }

    @Test
    public void testFullHouseBeatsFlush() {
        p1.getHoleCards().addAll(Arrays.asList(
            new Card(CardValue.AS, CardColor.COEUR), new Card(CardValue.AS, CardColor.PIQUE)
        ));
        p2.getHoleCards().addAll(Arrays.asList(
            new Card(CardValue.DEUX, CardColor.TREFLE), new Card(CardValue.CINQ, CardColor.TREFLE)
        ));
        table.getGameHand().setCommunityCards(Arrays.asList(
            new Card(CardValue.AS, CardColor.TREFLE), new Card(CardValue.ROI, CardColor.COEUR),
            new Card(CardValue.ROI, CardColor.PIQUE), new Card(CardValue.NEUF, CardColor.TREFLE),
            new Card(CardValue.VALET, CardColor.TREFLE)
        ));

        List<PlayerSession> winners = engine.evaluateShowdown(table);

        assertEquals(1, winners.size());
        assertEquals(p1, winners.get(0));
        assertEquals(HandType.FULL, p1.getFinalHand());
        assertEquals(HandType.FLUSH, p2.getFinalHand());
    }

    @Test
    public void testQuinteFlushBeatsCarre() {
        p1.getHoleCards().addAll(Arrays.asList(
            new Card(CardValue.NEUF, CardColor.PIQUE), new Card(CardValue.DIX, CardColor.PIQUE)
        ));
        p2.getHoleCards().addAll(Arrays.asList(
            new Card(CardValue.AS, CardColor.COEUR), new Card(CardValue.AS, CardColor.CARREAU)
        ));
        table.getGameHand().setCommunityCards(Arrays.asList(
            new Card(CardValue.VALET, CardColor.PIQUE), new Card(CardValue.DAME, CardColor.PIQUE),
            new Card(CardValue.ROI, CardColor.PIQUE), new Card(CardValue.AS, CardColor.PIQUE),
            new Card(CardValue.AS, CardColor.TREFLE)
        ));

        List<PlayerSession> winners = engine.evaluateShowdown(table);

        assertEquals(1, winners.size());
        assertEquals(p1, winners.get(0));
        assertEquals(HandType.QUINTE_FLUSH, p1.getFinalHand());
    }

    @Test
    public void testWheelStraightVsHighStraight() {
        p1.getHoleCards().addAll(Arrays.asList(new Card(CardValue.AS, CardColor.COEUR), new Card(CardValue.DEUX, CardColor.PIQUE)));
        
        // CORRECTION : P2 a 8 et 9 (pour faire 5-6-7-8-9)
        p2.getHoleCards().addAll(Arrays.asList(new Card(CardValue.HUIT, CardColor.TREFLE), new Card(CardValue.NEUF, CardColor.CARREAU)));
        
        table.getGameHand().setCommunityCards(Arrays.asList(
            new Card(CardValue.TROIS, CardColor.CARREAU), new Card(CardValue.QUATRE, CardColor.COEUR),
            new Card(CardValue.CINQ, CardColor.PIQUE), new Card(CardValue.SIX, CardColor.TREFLE),
            new Card(CardValue.SEPT, CardColor.CARREAU)
        ));

        List<PlayerSession> winners = engine.evaluateShowdown(table);
        assertEquals(1, winners.size());
        assertEquals(p2, winners.get(0), "La suite à hauteur 9 bat la suite à hauteur 5");
    }

    @Test
    public void testKickerTieBreaker() {
        p1.getHoleCards().addAll(Arrays.asList(
            new Card(CardValue.AS, CardColor.COEUR), new Card(CardValue.ROI, CardColor.PIQUE)
        ));
        p2.getHoleCards().addAll(Arrays.asList(
            new Card(CardValue.AS, CardColor.TREFLE), new Card(CardValue.DAME, CardColor.CARREAU)
        ));
        table.getGameHand().setCommunityCards(Arrays.asList(
            new Card(CardValue.AS, CardColor.CARREAU), new Card(CardValue.DEUX, CardColor.COEUR),
            new Card(CardValue.CINQ, CardColor.PIQUE), new Card(CardValue.HUIT, CardColor.TREFLE),
            new Card(CardValue.DIX, CardColor.CARREAU)
        ));

        List<PlayerSession> winners = engine.evaluateShowdown(table);

        assertEquals(1, winners.size());
        assertEquals(p1, winners.get(0));
    }

    @Test
    public void testTheBoardPlays() {
        // Le board est imbattable (Quinte Flush Royale)
        p1.getHoleCards().addAll(Arrays.asList(new Card(CardValue.DEUX, CardColor.TREFLE), new Card(CardValue.TROIS, CardColor.PIQUE)));
        p2.getHoleCards().addAll(Arrays.asList(new Card(CardValue.QUATRE, CardColor.CARREAU), new Card(CardValue.CINQ, CardColor.COEUR)));

        table.getGameHand().setCommunityCards(Arrays.asList(
            new Card(CardValue.DIX, CardColor.COEUR), new Card(CardValue.VALET, CardColor.COEUR),
            new Card(CardValue.DAME, CardColor.COEUR), new Card(CardValue.ROI, CardColor.COEUR),
            new Card(CardValue.AS, CardColor.COEUR)
        ));

        List<PlayerSession> winners = engine.evaluateShowdown(table);

        assertEquals(2, winners.size(), "Égalité, tout le monde joue le board");
    }
}
