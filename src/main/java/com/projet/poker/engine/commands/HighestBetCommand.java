package main.java.com.projet.poker.engine.commands;

import main.java.com.projet.poker.engine.BaseCommand;
import main.java.com.projet.poker.engine.PokerEngine;
import main.java.com.projet.poker.model.game.Table;

public class HighestBetCommand extends BaseCommand {

    public HighestBetCommand() { super("/highestbet", "/highestbet : Affiche la mise la plus élevée de la main en cours", 0); }

    @Override
    public void execute(Table table, PokerEngine engine, String[] args) {
        engine.getLogger().logHighestBet(table);
    }
}
