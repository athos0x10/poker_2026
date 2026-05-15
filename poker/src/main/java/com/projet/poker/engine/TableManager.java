package com.projet.poker.engine;

import com.projet.poker.engine.GameState;
import com.projet.poker.engine.network.WebSocketGameNotifier;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;
// tests
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TableManager {

    private final WebSocketGameNotifier gameNotifier;

    private final Map<Integer, Table> tables = new ConcurrentHashMap<>();
    private final Map<Integer, PokerEngine> engines = new ConcurrentHashMap<>();

    @Autowired
    public TableManager(WebSocketGameNotifier gameNotifier) {
        this.gameNotifier = gameNotifier;
    }

    // pour les tests (creation de tables)
    @PostConstruct
    public void init() {
        createTable(1, "Table des Pros", 10.0, 2);
        createTable(2, "Table Débutant", 1.0, 2);
    }

    /**
     * Crée une nouvelle table et son moteur associé
     */
    public void createTable(int tableId, String name, double minBet,
            int maxPlayers) {
        Table table = new Table(tableId, name, minBet, maxPlayers);
        PokerEngine engine = new PokerEngine();

        engine.setNotifier(gameNotifier);

        tables.put(tableId, table);
        engines.put(tableId, engine);
    }

    public void joinTable(int tableId, PlayerSession player) {
        Table table = tables.get(tableId);
        PokerEngine engine = engines.get(tableId);

        table.addPlayer(player);
        int playerCount = table.getActivePlayers().size();
        System.out.println("TableManager.joinTable: tableId=" + tableId
                + ", playerId=" + player.getId() + ", state="
                + table.getGameState() + ", activePlayers=" + playerCount);

        if (table.getGameState() == GameState.WAITING_FOR_PLAYERS
                && playerCount >= 2) {
            System.out.println(
                    "TableManager.joinTable: starting new hand for tableId=" + tableId
                    + " with " + playerCount + " players.");
            engine.startNewHand(table);
        }
    }

    public Optional<Table> getTable(int tableId) {
        return Optional.ofNullable(tables.get(tableId));
    }

    public Optional<Table> findTableByPlayerId(long playerId) {
        return tables.values().stream()
            .filter(table -> table.getActivePlayers().stream()
                .anyMatch(player -> player.getId() == playerId))
            .findFirst();
    }

    public Optional<PokerEngine> getEngine(int tableId) {
        return Optional.ofNullable(engines.get(tableId));
    }

    public Map<Integer, Table> getAllTables() {
        return tables;
    }

    /**
     * Supprime une table quand elle est vide ou terminée
     */
    public void removeTable(int tableId) {
        tables.remove(tableId);
        engines.remove(tableId);
    }
}
