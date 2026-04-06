package main.java.com.projet.poker.engine.commands;

import main.java.com.projet.poker.engine.BaseCommand;
import main.java.com.projet.poker.engine.PokerEngine;
import main.java.com.projet.poker.model.game.Table;

public class WinnersCommand extends BaseCommand {

    public WinnersCommand() { super("/winners", "/winners : Affiche les gagnants de la main en cours", 0); }

    @Override
    public void execute(Table table, PokerEngine engine, String[] args) {
        if (table.getWinners() == null || table.getWinners().isEmpty()) {
            engine.getLogger().logInfo("Il n'y a pas encore de vainqueur !");
        } else {
            engine.getLogger().logWinners(table);
        }
    }
}
