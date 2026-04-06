package main.java.com.projet.poker.engine.commands;

import main.java.com.projet.poker.engine.BaseCommand;
import main.java.com.projet.poker.engine.PokerEngine;
import main.java.com.projet.poker.model.game.Table;

public class PlayerInfoCommand extends BaseCommand {

    public PlayerInfoCommand() { super("/playerinfo", "/playerinfo : Affiche les informations sur le joueur", 1); }

    @Override
    public void execute(Table table, PokerEngine engine, String[] args) {
        int playerId = args.length > 0 ? Integer.parseInt(args[0]) : table.getGameHand().getCurrentTurnIndex();
        engine.getLogger().logPlayerInfo(table.getActivePlayers().get(playerId));
    }
}
