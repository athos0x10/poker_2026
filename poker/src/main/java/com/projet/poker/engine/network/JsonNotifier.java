package com.projet.poker.engine.network;

import com.projet.poker.engine.Card;
import com.projet.poker.engine.network.dto.DTOManager.*;
import com.projet.poker.model.game.GameHand;
import com.projet.poker.model.game.PlayerSession;
import com.projet.poker.model.game.Table;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Notifier JSON abstrait pour gérer l'envoi de messages au format JSON.
 */
public abstract class JsonNotifier implements GameNotifier {

  // Méthode abstraite à implémenter pour envoyer un message JSON à un joueur
  // spécifique
  protected abstract void send(long targetPlayerId, Object message);

  protected abstract void broadcast(List<PlayerSession> players,
                                    Object message);

  private List<String> mapCards(List<Card> cards) {
    return cards.stream().map(Card::toString).collect(Collectors.toList());
  }

  // ================ MESSAGES PRIVÉS ================

  @Override
  public void sendHandCards(long playerId, List<Card> cards) {
    send(playerId, new HandCardsDTO("send_hand", mapCards(cards)));
  }

  @Override
  public void notifyActionAck(long playerId, boolean isValid, String message) {
    send(playerId, new ActionAckDTO("ack", isValid ? 1 : 0, message));
  }

  @Override
  public void notifyPlayerTurn(long playerId) {
    send(playerId, new PlayerTurnDTO("your_turn"));
  }

  @Override
  public void sendFullGameInfos(long playerId, Table table) {
    GameHand hand = table.getGameHand();

    List<PlayerInfoDTO> players =
        table.getActivePlayers()
            .stream()
            .map(p
                 -> new PlayerInfoDTO(p.getId(), p.getCurrentStack(),
                                      p.getBetInCurrentRound(), p.hasFolded(),
                                      p.isAllIn(), p.getSeatNumber()))
            .toList();

    send(playerId,
         new FullGameInfosDTO(
             "infos_res", table.getName(), table.getGameState().toString(),
             hand.getPotAmount(), mapCards(hand.getCommunityCards()),
             hand.getHighestBet(), hand.getCurrentTurnIndex(), players));
  }

  @Override
  public void sendBestCurrentCombination(long playerId, List<Card> cards,
                                         String handType) {
    send(playerId, new BestComboDTO("best_cards", handType, mapCards(cards)));
  }

  @Override
  public void broadcastFullGameInfos(List<PlayerSession> players, Table table) {
    GameHand hand = table.getGameHand();
    List<PlayerInfoDTO> playerInfos =
        players.stream()
            .map(p
                 -> new PlayerInfoDTO(p.getId(), p.getCurrentStack(),
                                      p.getBetInCurrentRound(), p.hasFolded(),
                                      p.isAllIn(), p.getSeatNumber()))
            .toList();

    broadcast(players,
              new FullGameInfosDTO(
                  "infos_res", table.getName(), table.getGameState().toString(),
                  hand.getPotAmount(), mapCards(hand.getCommunityCards()),
                  hand.getHighestBet(), hand.getCurrentTurnIndex(),
                  playerInfos));
  }

  // ================ MESSAGES BROADCAST ================

  @Override
  public void broadcastBoardUpdate(List<PlayerSession> players,
                                   List<Card> communityCards) {
    broadcast(players,
              new BoardUpdateDTO("update_board", mapCards(communityCards)));
  }

  @Override
  public void broadcastBetAndStackUpdate(List<PlayerSession> players,
                                         long playerId, double bet,
                                         double stack) {
    broadcast(players,
              new BetStackUpdateDTO("update_bet_stack", playerId, bet, stack));
  }

  @Override
  public void broadcastPotUpdate(List<PlayerSession> players,
                                 double potAmount) {
    broadcast(players, new PotUpdateDTO("update_pot", potAmount));
  }

  @Override
  public void broadcastPlayerQuit(List<PlayerSession> players, long playerId) {
    broadcast(players, new UserQuitDTO("user_quit", playerId));
  }

  @Override
  public void broadcastGamePaused(List<PlayerSession> players) {
    broadcast(players, new ActionAckDTO("game_paused", 1, "Game is paused"));
  }

  @Override
  public void broadcastShowdown(List<PlayerSession> players, List<Card> board,
                                List<PlayerSession> winners) {
    List<WinnerInfoDTO> winDtos =
        winners.stream()
            .map(w
                 -> new WinnerInfoDTO(w.getId(), w.getFinalHand().toString(),
                                      mapCards(w.getHoleCards())))
            .toList();

    broadcast(players,
              new ShowdownDTO("reveal_cards", mapCards(board), winDtos));
  }
}
