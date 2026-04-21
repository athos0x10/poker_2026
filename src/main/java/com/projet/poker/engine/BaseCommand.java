package com.projet.poker.engine;

import com.projet.poker.model.game.Table;

public abstract class BaseCommand {
    private String name;
    private String description;
    private int minArgs;

    public BaseCommand(String name, String description, int minArgs) {
        this.name = name;
        this.description = description;
        this.minArgs = minArgs;
    }

    public abstract void execute(Table table, PokerEngine engine, String[] args);

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getMinArgs() { return minArgs; }
}
