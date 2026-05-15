package com.projet.poker.engine.network;

import com.projet.poker.engine.network.JsonNotifier;
import com.projet.poker.model.game.PlayerSession;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketGameNotifier extends JsonNotifier {

  /* @TODO potentiellement à modifier */

  private final SimpMessagingTemplate messagingTemplate;

  public WebSocketGameNotifier(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @Override
  protected void send(long targetPlayerId, Object message) {
    // Envoi privé : Spring utilise l'ID pour router vers la bonne session
    messagingTemplate.convertAndSendToUser(String.valueOf(targetPlayerId),
                                           "/queue/private", message);
  }

  @Override
  protected void broadcast(List<PlayerSession> players, Object message) {
    messagingTemplate.convertAndSend("/topic/table", message);
  }
}
