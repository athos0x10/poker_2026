package com.projet.poker.controller;

import com.projet.poker.dto.InscriptionRequest;
import com.projet.poker.dto.LoginRequest;
import com.projet.poker.model.persist.Utilisateur;
import com.projet.poker.security.SessionManager;
import com.projet.poker.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin("*")
public class AuthController {

    private final UtilisateurService utilisateurService;
    private final SessionManager sessionManager;

    public AuthController(UtilisateurService utilisateurService, SessionManager sessionManager) {
        this.utilisateurService = utilisateurService;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> inscrire(@Valid @RequestBody InscriptionRequest request) {
        try {
            Utilisateur u = new Utilisateur();
            u.setLogin(request.getLogin());
            u.setEmail(request.getEmail());
            u.setPasswordHash(request.getPasswordHash());
            Utilisateur cree = utilisateurService.enregistrerUtilisateur(u);
            
            // Auto-login : on génère le token immédiatement
            String token = sessionManager.createSession(cree.getId());
            return ResponseEntity.ok(Map.of("token", token, "message", "Inscription réussie !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> seConnecter(@Valid @RequestBody LoginRequest request) {
        try {
            Utilisateur u = utilisateurService.trouverParLogin(request.getLogin());
            if (u.getPasswordHash().equals(request.getPassword())) {
                String token = sessionManager.createSession(u.getId());
                return ResponseEntity.ok(Map.of("token", token));
            }
            return ResponseEntity.status(401).body(Map.of("error", "Identifiants incorrects"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "Utilisateur non trouvé"));
        }
    }
}