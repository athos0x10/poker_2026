package com.projet.poker.engine.network;

import com.projet.poker.engine.ActionType;
import com.projet.poker.engine.PokerEngine;
import com.projet.poker.engine.TableManager;
import com.projet.poker.engine.network.dto.DTOManager.ActionRequestDTO;
import com.projet.poker.engine.network.dto.DTOManager.JoinRequestDTO;
import com.projet.poker.model.game.Action;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GameController.class);
  private final TableManager tableManager;

  public GameController(TableManager tableManager) {
    this.tableManager = tableManager;
  }

  @MessageMapping("/join") // front envoie sur /app/join
  public void handleJoinRequest(JoinRequestDTO request) {
    LOGGER.info(
        "JOIN request received: tableId={}, playerId={}, seat={}, stack={}",
        request.tableId(), request.playerId(), request.seatNumber(),
        request.initialStack());

    PlayerSession newPlayer = new PlayerSession(
        request.playerId(), request.initialStack(), request.seatNumber());

    tableManager.joinTable(request.tableId(), newPlayer);

    Table table = tableManager.getTable(request.tableId()).orElse(null);
    PokerEngine engine = tableManager.getEngine(request.tableId()).orElse(null);
    if (table != null && engine != null && table.getGameState() != null) {
      engine.getNotifier().sendFullGameInfos(request.playerId(), table);
    }
  }

  @MessageMapping("/action") // front envoie sur /app/action
  public void receiveAction(ActionRequestDTO request) {
    int tableId = request.tableId();

    PokerEngine engine = tableManager.getEngine(tableId).orElse(null);
    Table table = tableManager.getTable(tableId).orElse(null);

    if (engine == null || table == null)
      return;

    if (request.action().equalsIgnoreCase("infos_req")) {
      engine.getNotifier().sendFullGameInfos(request.playerId(), table);
      return;
    } else if (request.action().equalsIgnoreCase("quit")) {
      engine.getNotifier().broadcastPlayerQuit(table.getActivePlayers(),
                                               request.playerId());
      engine.handlePlayerQuit(table, request.playerId());
      return;
    } else {
      ActionType type = ActionType.valueOf(request.action().toUpperCase());
      Action action = new Action(type, request.playerId(), request.amount());
      engine.processAction(table, action);
    }
  }
}
