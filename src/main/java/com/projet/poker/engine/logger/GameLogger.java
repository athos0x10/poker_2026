package com.projet.poker.engine.logger;

import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;

public interface GameLogger {
    void logInfo(String message);
    void logError(String error);
    void logAllInfos(Table table);
    void logTableInfos(Table table);
    void logGameHandInfos(Table table);
    void logCurrentPlayerInfos(Table table);
    void logPlayerHands(Table table);
    void logWinners(Table table);
    void logCommunityCards(Table table);
    void logPlayerInfo(PlayerSession player);
    void logPlayerHoleCards(PlayerSession player);
    void logPotAmount(Table table);
    void logHighestBet(Table table);
    void logPlayerStack(PlayerSession player);
    void logPlayerBet(PlayerSession player);
    void logPlayerAction(PlayerSession player, String actionType, double amount);
}
