package com.projet.poker.engine;

import java.util.List;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;

public interface GameNotifier {
    // --- MESSAGES PRIVÉS (Un seul joueur) ---
    void sendHandCards(long playerId, List<Card> cards);
    void notifyActionAck(long playerId, boolean isValid, String message);
    void notifyPlayerTurn(long playerId);
    void sendFullGameInfos(long playerId, Table table);
    void sendBestCurrentCombination(long playerId, List<Card> cards, String handType);

    // --- MESSAGES BROADCAST (Toute la table) ---
    void broadcastBoardUpdate(List<Card> communityCards);
    void broadcastBetAndStackUpdate(long playerId, double bet, double stack);
    void broadcastPotUpdate(double potAmount);
    void broadcastPlayerQuit(long playerId);
    void broadcastShowdown(List<Card> board, List<PlayerSession> winners);
}
