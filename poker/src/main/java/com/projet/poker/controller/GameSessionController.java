package com.projet.poker.controller;

import com.projet.poker.engine.TableManager;
import com.projet.poker.model.game.Table;
import com.projet.poker.security.SessionManager;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
@CrossOrigin("*")
public class GameSessionController {

    private final SessionManager sessionManager;
    private final TableManager tableManager;

    public GameSessionController(SessionManager sessionManager, TableManager tableManager) {
        this.sessionManager = sessionManager;
        this.tableManager = tableManager;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getSessionInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("error", "Token manquant"));
        }

        Long userId = sessionManager.getUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Session invalide"));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("playerId", userId);

        tableManager.findTableByPlayerId(userId).ifPresentOrElse(table -> {
            result.put("tableId", table.getId());
            result.put("tableName", table.getName());
            result.put("gameState", table.getGameState());
            result.put("players", table.getActivePlayers());
        }, () -> {
            result.put("tableId", 1);
            result.put("status", "not-in-table");
        });

        return ResponseEntity.ok(result);
    }
}
