package main.java.com.projet.poker.model.game;

import java.util.Date;

import main.java.com.projet.poker.engine.ActionType;

public class Action {

    private Long id;
    private Long handId;
    private Long playerId;
    private ActionType actionType;
    private double amount;
    Date timestamp;
    
    public Action(Long id, Long handId, Long playerId, ActionType actionType, double amount) {
        this.id = id;
        this.handId = handId;
        this.playerId = playerId;
        this.actionType = actionType;
        this.amount = amount;
        this.timestamp = new Date(System.currentTimeMillis());
    }

    public ActionType getActionType() {
        return actionType;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public double getAmount() {
        return amount;
    }

    public Long getId() {
        return id;
    }
}
