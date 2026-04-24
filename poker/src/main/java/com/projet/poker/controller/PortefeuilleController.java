package com.projet.poker.controller;

import com.projet.poker.security.SessionManager;
import com.projet.poker.service.UtilisateurService;
import com.projet.poker.model.persist.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/portefeuille")
@CrossOrigin("*")
public class PortefeuilleController {

    private final SessionManager sessionManager;
    private final UtilisateurService utilisateurService;

    public PortefeuilleController(SessionManager sessionManager, UtilisateurService utilisateurService) {
        this.sessionManager = sessionManager;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/mon-solde")
    public ResponseEntity<?> getMonSolde(@RequestHeader("Authorization") String token) {
        Long userId = sessionManager.getUserId(token);
        if (userId == null) return ResponseEntity.status(401).body("Session invalide");

        Utilisateur u = utilisateurService.trouverParId(userId);
        return ResponseEntity.ok(Map.of("solde", u.getPortefeuille().getGlobalBalance()));
    }
}