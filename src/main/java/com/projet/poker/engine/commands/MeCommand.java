package main.java.com.projet.poker.engine.commands;

import main.java.com.projet.poker.engine.BaseCommand;
import main.java.com.projet.poker.engine.PokerEngine;
import main.java.com.projet.poker.model.game.Table;

public class MeCommand extends BaseCommand {

    public MeCommand() { super("/me", "/me : Affiche les informations sur l'utilisateur", 0); }

    @Override
    public void execute(Table table, PokerEngine engine, String[] args) {
        engine.getLogger().logCurrentPlayerInfos(table);
    }
}
