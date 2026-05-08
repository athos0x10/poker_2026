package com.projet.poker.engine.network;

import com.projet.poker.engine.network.JsonNotifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketGameNotifier extends JsonNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketGameNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    protected void send(long targetPlayerId, Object message) {
        // Envoi privé : Spring utilise l'ID pour router vers la bonne session
        messagingTemplate.convertAndSendToUser(
            String.valueOf(targetPlayerId), 
            "/queue/private", 
            message
        );
        
        // Si c'est un broadcast (que tu gères dans la classe parente), 
        // on peut aussi envoyer sur un topic commun :
        messagingTemplate.convertAndSend("/topic/table", message);
    }
}
