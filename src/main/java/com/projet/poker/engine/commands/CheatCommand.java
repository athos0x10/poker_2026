package main.java.com.projet.poker.engine.commands;

import main.java.com.projet.poker.engine.BaseCommand;
import main.java.com.projet.poker.engine.PokerEngine;
import main.java.com.projet.poker.model.game.Table;

public class CheatCommand extends BaseCommand {

    public CheatCommand() { super("/cheat", "/playerhands : Affiche les mains des joueurs", 0); }

    @Override    
    public void execute(Table table, PokerEngine engine, String[] args) {
        engine.getLogger().logPlayerHands(table);
    }
}
