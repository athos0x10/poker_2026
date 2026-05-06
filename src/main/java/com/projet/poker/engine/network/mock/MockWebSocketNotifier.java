package com.projet.poker.engine.network.mock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.projet.poker.engine.network.JsonNotifier;

/*
 * Notifier de test qui affiche dans la console les messages JSON qui seraient envoyés au client.
 * Utile pour vérifier que les données envoyées sont correctes et bien formatées, sans avoir besoin d'une vraie connexion WebSocket.
*/
public class MockWebSocketNotifier extends JsonNotifier {

    // GsonBuilder pour avoir un JSON formaté lisiblement dans la console
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void send(long targetPlayerId, Object message) {
        String jsonOutput = gson.toJson(message);
        
        System.out.println("\n[RESEAU -> JOUEUR ID: " + targetPlayerId + "]");
        System.out.println(jsonOutput);
        System.out.println("-------------------------------------------------");
    }
}
