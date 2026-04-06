package main.java.com.projet.poker.model.game;

import java.util.Date;

import main.java.com.projet.poker.engine.ActionType;

public class Action {
    
    private long handId;

    private long id;
    private long playerId;
    private ActionType actionType;
    private double amount;
    Date timestamp;
    
    public Action(ActionType actionType, long playerId, double amount) {
        this.id = 0;
        this.handId = 0;
        this.playerId = playerId;
        this.actionType = actionType;
        this.amount = amount;
        this.timestamp = new Date(System.currentTimeMillis());
    }

    public ActionType getActionType() {
        return actionType;
    }

    public long getPlayerId() {
        return playerId;
    }

    public double getAmount() {
        return amount;
    }

    public long getId() {
        return id;
    }
}
