package com.projet.poker.engine.network;

import java.util.List;

import com.projet.poker.engine.Card;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;

/* 
 * Interface pour gérer l'envoi de messages de notification.
 */
public interface GameNotifier {
    // --- MESSAGES PRIVÉS (Un seul joueur) ---
    void sendHandCards(long playerId, List<Card> cards);
    void notifyActionAck(long playerId, boolean isValid, String message);
    void notifyPlayerTurn(long playerId);
    void sendFullGameInfos(long playerId, Table table);
    void sendBestCurrentCombination(long playerId, List<Card> cards, String handType);

    // --- MESSAGES BROADCAST (Toute la table) ---
    void broadcastBoardUpdate(List<PlayerSession> players, List<Card> communityCards);
    void broadcastBetAndStackUpdate(List<PlayerSession> players, long playerId, double bet, double stack);
    void broadcastPotUpdate(List<PlayerSession> players, double potAmount);
    void broadcastPlayerQuit(List<PlayerSession> players, long playerId);
    void broadcastShowdown(List<PlayerSession> players, List<Card> board, List<PlayerSession> winners);
}
