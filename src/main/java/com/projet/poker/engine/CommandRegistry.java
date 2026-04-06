package main.java.com.projet.poker.engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import main.java.com.projet.poker.model.game.Table;

public class CommandRegistry {
    private final Map<String, BaseCommand> commands = new HashMap<>();

    public void registerCommand(BaseCommand cmd) {
        commands.put(cmd.getName().toLowerCase(), cmd);
    }

    public void dispatch(String input, Table table, PokerEngine engine) {
        String[] parts = input.trim().split("\\s+");
        String cmdName = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        BaseCommand cmd = commands.get(cmdName);

        if (cmd != null) {
            if (args.length >= cmd.getMinArgs()) {
                cmd.execute(table, engine, args);
            } else {
                engine.getLogger().logError("Argument(s) manquant(s). Usage: " + cmd.getName());
            }
        } else {
            engine.getLogger().logError("Commande système inconnue : " + cmdName);
        }
    }

    public Map<String, BaseCommand> getCommands() {
        return commands;
    }
}
