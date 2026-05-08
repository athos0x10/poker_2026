package main.java.com.projet.poker.engine.network;

import com.projet.poker.engine.PokerEngine;
import com.projet.poker.engine.ActionType;
import com.projet.poker.model.game.Action;
import com.projet.poker.model.game.Table;
import com.projet.poker.network.dto.DTOManager.ActionRequestDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    private final PokerEngine engine;
    private final Table currentTable; // À remplacer par un TableManager plus tard

    public GameController(PokerEngine engine) {
        this.engine = engine;
        // On initialise une table de test
        this.currentTable = new Table(1, "Main Table", 10, 6);
    }

    @MessageMapping("/action") // Le front envoie sur /app/action
    public void receiveAction(ActionRequestDTO request) {
        // Gestion des commandes spéciales
        if (request.action().equalsIgnoreCase("infos_req")) {
            engine.getNotifier().sendFullGameInfos(request.playerId(), currentTable);
            return;
        }

        if (request.action().equalsIgnoreCase("quit")) {
            engine.getNotifier().broadcastPlayerQuit(table.getActivePlayers(), request.playerId());
            engine.handlePlayerQuit(currentTable, request.playerId());
            return;
        }

        // Conversion et envoi au moteur
        ActionType type = ActionType.valueOf(request.action().toUpperCase());
        Action action = new Action(type, request.playerId(), request.amount());
        engine.processAction(currentTable, action);
    }
}
