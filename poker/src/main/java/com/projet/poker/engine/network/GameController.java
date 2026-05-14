package com.projet.poker.engine.network;

import com.projet.poker.engine.TableManager;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.engine.network.dto.DTOManager.JoinRequestDTO;
import com.projet.poker.engine.ActionType;
import com.projet.poker.engine.PokerEngine;
import com.projet.poker.engine.network.dto.DTOManager.ActionRequestDTO;
import com.projet.poker.engine.network.dto.DTOManager.CreateRequestDOT;
import com.projet.poker.model.game.Action;
import com.projet.poker.model.game.Table;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    private final TableManager tableManager;
    private int newTableId = 1;

    public GameController(TableManager tableManager) {
        this.tableManager = tableManager;
    }


    @MessageMapping("/join") // front envoie sur /app/join
    public void handleJoinRequest(JoinRequestDTO request) {
        
        PlayerSession newPlayer = new PlayerSession(
            request.playerId(), 
            request.initialStack(), 
            request.seatNumber()
        );

        tableManager.joinTable(request.tableId(), newPlayer);
    }

    @MessageMapping("/create") // front envoie sur /app/create
    @SendToUser("/queue/reply") // repond sur user/queue/reply
    public int handleCreateRequest(CreateRequestDOT request) {
        tableManager.createTable(newTableId, 
            request.tableName(), 
            request.blind(), 
            request.tableSize());
        
        PlayerSession newPlayer = new PlayerSession(
            request.playerId(), 
            request.initialStack(), 
            request.seatNumber()
        );

        tableManager.joinTable(newTableId, newPlayer);

        newTableId++;
        return newTableId - 1;
    }


    @MessageMapping("/action") // front envoie sur /app/action
    public void receiveAction(ActionRequestDTO request) {
        int tableId = request.tableId();
        
        PokerEngine engine = tableManager.getEngine(tableId).orElse(null);
        Table table = tableManager.getTable(tableId).orElse(null);

        if (engine == null || table == null) return;

        if (request.action().equalsIgnoreCase("infos_req")) {
        engine.getNotifier().sendFullGameInfos(request.playerId(), table);
        return;
        } else if (request.action().equalsIgnoreCase("quit")) {
        engine.getNotifier().broadcastPlayerQuit(table.getActivePlayers(), request.playerId());
        engine.handlePlayerQuit(table, request.playerId());
        return;
        } else {
        ActionType type = ActionType.valueOf(request.action().toUpperCase());
        Action action = new Action(type, request.playerId(), request.amount());
        engine.processAction(table, action);
        }
    }
}
