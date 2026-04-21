package com.projet.poker.engine.commands;

import com.projet.poker.engine.BaseCommand;
import com.projet.poker.engine.PokerEngine;
import com.projet.poker.model.game.Table;

public class TableCommand extends BaseCommand {

    public TableCommand() { super("/table", "/table : Affiche les informations sur la table", 0); }

    @Override    
    public void execute(Table table, PokerEngine engine, String[] args) {
        engine.getLogger().logTableInfos(table);
    }
}
