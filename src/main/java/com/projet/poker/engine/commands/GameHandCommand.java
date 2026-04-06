package main.java.com.projet.poker.engine.commands;

import main.java.com.projet.poker.engine.BaseCommand;
import main.java.com.projet.poker.engine.PokerEngine;
import main.java.com.projet.poker.model.game.Table;

public class GameHandCommand extends BaseCommand {

    public GameHandCommand() { super("/gamehand", "/gamehand : Affiche les informations sur la main du jeu", 0); }

    @Override
    public void execute(Table table, PokerEngine engine, String[] args) {
        engine.getLogger().logGameHandInfos(table);
    }
}
