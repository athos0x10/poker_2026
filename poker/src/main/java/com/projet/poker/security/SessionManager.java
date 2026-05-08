package com.projet.poker.security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {
    // Map qui stocke : Token -> UserId

    private final Map<String, Long> activeSessions = new ConcurrentHashMap<>();

    public String createSession(Long userId) {
        String token = UUID.randomUUID().toString();
        activeSessions.put(token, userId);
        return token;
    }

    public Long getUserId(String token) {
        // On retire "Bearer " si le front l'envoie dans le header Authorization
        String cleanToken = token.replace("Bearer ", "");
        return activeSessions.get(cleanToken);
    }

    public void removeSession(String token) {
        activeSessions.remove(token.replace("Bearer ", ""));
    }
}
