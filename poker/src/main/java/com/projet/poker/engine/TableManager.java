package com.projet.poker.engine;

import com.projet.poker.model.game.Table;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.engine.network.WebSocketGameNotifier;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

// tests
import jakarta.annotation.PostConstruct;


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
        createTable(1, "Table des Pros", 10.0, 6);
        createTable(2, "Table Débutant", 1.0, 8);
    }

    /**
     * Crée une nouvelle table et son moteur associé
     */
    public void createTable(int tableId, String name, double minBet, int maxPlayers) {
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

        if (table.getActivePlayers().size() == table.getMaxPlayers()) {
            engine.startNewHand(table);
        }
    }

    public Optional<Table> getTable(int tableId) {
        return Optional.ofNullable(tables.get(tableId));
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
