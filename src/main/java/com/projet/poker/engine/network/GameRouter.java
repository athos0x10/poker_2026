package com.projet.poker.engine.network;

import com.google.gson.Gson;
import com.projet.poker.engine.ActionType;
import com.projet.poker.engine.PokerEngine;
import com.projet.poker.model.game.Action;
import com.projet.poker.model.game.Table;
import com.projet.poker.engine.network.dto.DTOManager.ActionRequestDTO;

public class GameRouter {

    private PokerEngine engine;
    private Gson gson;

    public GameRouter(PokerEngine engine) {
        this.engine = engine;
        this.gson = new Gson();
    }

    /**
     * Simule la réception d'un message JSON du front-end via WebSocket.
     */
    public void handleIncomingWebSocketMessage(Table table, String jsonPayload) {
        System.out.println("\n[FRONT-END ENVOIE] : " + jsonPayload);

        try {
            // Désérialisation automatique du JSON vers notre DTO
            ActionRequestDTO request = gson.fromJson(jsonPayload, ActionRequestDTO.class);

            // Interception des commandes spéciales (voir tableau de specs)
            if (request.action().equalsIgnoreCase("infos_req")) {
                if (engine.getNotifier() != null) {
                    engine.getNotifier().sendFullGameInfos(request.playerId(), table);
                }
                return;
            }

            if (request.action().equalsIgnoreCase("quit")) {
                if (engine.getNotifier() != null) {
                    engine.getNotifier().broadcastPlayerQuit(table.getActivePlayers(), request.playerId());
                }
                engine.handlePlayerQuit(table, request.playerId());
                return;
            }

            // 3. C'est une action de jeu classique, on la traduit pour le moteur
            ActionType type = ActionType.valueOf(request.action().toUpperCase());
            Action action = new Action(type, request.playerId(), request.amount());

            // 4. On envoie au moteur
            engine.processAction(table, action);

        } catch (IllegalArgumentException e) {
            System.err.println("Erreur : Action inconnue envoyée par le front => " + jsonPayload);
        } catch (Exception e) {
            System.err.println("Erreur de parsing du JSON client => " + jsonPayload);
        }
    }
}
