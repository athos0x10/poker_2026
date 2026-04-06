package main.java.com.projet.poker.engine.commands;

import main.java.com.projet.poker.engine.BaseCommand;
import main.java.com.projet.poker.engine.PokerEngine;
import main.java.com.projet.poker.model.game.Table;

public class PotCommand extends BaseCommand {

    public PotCommand() { super("/pot", "/pot : Affiche le pot", 0); }

    @Override    
    public void execute(Table table, PokerEngine engine, String[] args) {
        engine.getLogger().logPotAmount(table);
    }
}
