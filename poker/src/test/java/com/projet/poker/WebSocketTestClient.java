package com.projet.poker;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Scanner;

public class WebSocketTestClient {

    private static final String WEBSOCKET_URL = "ws://localhost:8080/ws"; // Ajustez l'URL si nécessaire
    private static final String TOPIC_TABLE = "/topic/table";
    private static final String USER_QUEUE = "/user/queue/private";

    public static void main(String[] args) {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            StompSession session = stompClient.connect(WEBSOCKET_URL, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    System.out.println("Connecté au WebSocket");

                    // S'abonner au topic public
                    session.subscribe(TOPIC_TABLE, new DefaultStompFrameHandler());

                    // S'abonner à la queue privée (nécessite un ID utilisateur simulé)
                    // Pour les tests, on peut simuler un ID, par exemple 1
                    session.subscribe(USER_QUEUE, new DefaultStompFrameHandler());

                    // Boucle pour envoyer des messages
                    Scanner scanner = new Scanner(System.in);
                    while (true) {
                        System.out.print("Entrez un message à envoyer (ou 'quit' pour quitter) : ");
                        String input = scanner.nextLine();
                        if ("quit".equalsIgnoreCase(input)) {
                            break;
                        }
                        // Envoyer un message de test, par exemple vers /app/table/join ou autre endpoint
                        // Ajustez selon vos contrôleurs
                        session.send("/app/table/join", input); // Exemple, remplacez par un endpoint réel
                    }
                    session.disconnect();
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    System.err.println("Erreur STOMP : " + exception.getMessage());
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    System.err.println("Erreur de transport : " + exception.getMessage());
                }
            }).get(); // Bloquant jusqu'à connexion

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Object.class; // Accepte tout type de payload
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object payload) {
            System.out.println("Message reçu : " + payload);
        }
    }
}