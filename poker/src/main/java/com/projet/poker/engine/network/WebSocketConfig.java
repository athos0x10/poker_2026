package com.projet.poker.engine.network;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Le serveur envoie des messages sur ces préfixes
        config.enableSimpleBroker("/topic", "/queue");
        // Le front envoie ses messages vers ce préfixe
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // L'URL de connexion WebSocket (ex: ws://localhost:8080/poker-game)
        registry.addEndpoint("/poker-game").setAllowedOriginPatterns("*").withSockJS();
    }
}
