package com.projet.poker.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.projet.poker.model.game.Table;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Action;


public class PokerEngineStressTest {

    private PokerEngine engine;
    private Random random = new Random();

    @BeforeEach
    public void setUp() {
        engine = new PokerEngine();
    }

    @Test
    public void testMassConservationOverTenThousandHands() {
        int numberOfPlayers = 6;
        double initialStack = 1000.0;
        double totalMoneyInSystem = numberOfPlayers * initialStack;

        Table table = new Table(1, "Stress Test Table", 10.0, numberOfPlayers);
        for (int i = 0; i < numberOfPlayers; i++) {
            table.addPlayer(new PlayerSession(i, initialStack, i));
        }

        for (int hand = 0; hand < 10000; hand++) {
            engine.startNewHand(table);

            // On joue la main jusqu'au Showdown ou jusqu'à ce qu'un seul gagne par Fold
            while (table.getGameState() != GameState.SHOWDOWN) {
                int currentTurn = table.getGameHand().getCurrentTurnIndex();
                Action randomAction = generateRandomAction(table, currentTurn);
                
                // On tente de traiter l'action (si elle est illégale, on en génère une autre)
                engine.processAction(table, randomAction);
            }

            // Vérification CRITIQUE après chaque main
            double currentTotalMoney = table.getActivePlayers().stream()
                    .mapToDouble(p -> p.getCurrentStack())
                    .sum() + table.getGameHand().getPotAmount();

            assertEquals(totalMoneyInSystem, currentTotalMoney, 0.001, 
                "Alerte ! Fuite d'argent à la main n°" + hand);
        }
    }

    private Action generateRandomAction(Table table, int playerIdx) {
        ActionType[] types = ActionType.values();
        ActionType randomType = types[random.nextInt(types.length)];
        
        double amount = 0;
        if (randomType == ActionType.RAISE) {
            // Relance aléatoire entre la BB et 100
            amount = table.getGameHand().getHighestBet() + random.nextInt(50) + 10;
        }

        return new Action(randomType, playerIdx, amount);
    }
}
