package main.java.com.projet.poker.engine.commands;

import main.java.com.projet.poker.engine.BaseCommand;
import main.java.com.projet.poker.engine.PokerEngine;
import main.java.com.projet.poker.model.game.Table;

public class PlayerBetCommand extends BaseCommand {

    public PlayerBetCommand() { super("/playerbet", "/playerbet <id> : Affiche la mise du joueur", 1); }

    @Override
    public void execute(Table table, PokerEngine engine, String[] args) {
        int playerId = args.length > 0 ? Integer.parseInt(args[0]) : table.getGameHand().getCurrentTurnIndex();
        engine.getLogger().logPlayerBet(table.getActivePlayers().get(playerId));
    }
}
