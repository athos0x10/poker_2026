package com.projet.poker.engine.network;

import java.security.Principal;
import java.util.Map;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  /* @TODO potentiellement à modifier */

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // Le serveur envoie des messages sur ces préfixes
    config.enableSimpleBroker("/topic", "/queue");
    // Le front envoie ses messages vers ce préfixe
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/poker-game")
        .setHandshakeHandler(new UserHandshakeHandler())
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  private static class UserHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
      String playerId = null;
      if (request instanceof ServletServerHttpRequest servletRequest) {
        playerId = servletRequest.getServletRequest().getParameter("playerId");
      }
      if (playerId == null || playerId.isBlank()) {
        playerId = "anonymous-" + java.util.UUID.randomUUID();
      }
      return new StompPrincipal(playerId);
    }
  }

  private static class StompPrincipal implements Principal {
    private final String name;

    public StompPrincipal(String name) { this.name = name; }

    @Override
    public String getName() {
      return name;
    }
  }
}
