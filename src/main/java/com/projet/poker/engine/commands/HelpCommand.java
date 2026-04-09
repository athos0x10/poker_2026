package main.java.com.projet.poker.engine.commands;

import main.java.com.projet.poker.engine.BaseCommand;
import main.java.com.projet.poker.engine.PokerEngine;
import main.java.com.projet.poker.model.game.Table;

public class HelpCommand extends BaseCommand {
    public HelpCommand() { super("/help", "/help : Affiche la liste des commandes", 0); }

    @Override
    public void execute(Table table, PokerEngine engine, String[] args) {
        engine.getLogger().logInfo("=== Liste des commandes système ===");

        for (BaseCommand cmd : engine.getRegistry().getCommands().values()) {
            engine.getLogger().logInfo(cmd.getDescription());
        }
        engine.getLogger().logInfo("=== Liste des commandes de jeu ===");

        engine.getLogger().logInfo("FOLD : Se coucher");
        engine.getLogger().logInfo("CHECK : Checker");
        engine.getLogger().logInfo("CALL : Suivre");
        engine.getLogger().logInfo("RAISE <amount> : Relancer AU montant spécifié");
        engine.getLogger().logInfo("ALLIN : Miser tous ses jetons");
    }
}
