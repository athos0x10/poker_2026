package com.projet.poker.controller;

import com.projet.poker.dto.ProfilResponse;
import com.projet.poker.model.persist.Utilisateur;
import com.projet.poker.security.SessionManager;
import com.projet.poker.service.UtilisateurService;
import com.projet.poker.service.ProfilService; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profils")
@CrossOrigin("*")
public class ProfilController {

    private final SessionManager sessionManager;
    private final UtilisateurService utilisateurService;
    private final ProfilService profilService;

    public ProfilController(SessionManager sessionManager, UtilisateurService utilisateurService, ProfilService profilService) {
        this.sessionManager = sessionManager;
        this.utilisateurService = utilisateurService;
        this.profilService = profilService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMonProfil(@RequestHeader("Authorization") String token) {
        Long userId = sessionManager.getUserId(token);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Session invalide"));

        Utilisateur u = utilisateurService.trouverParId(userId);
        ProfilResponse resp = new ProfilResponse();
        resp.setLogin(u.getLogin());
        resp.setEmail(u.getEmail());
        resp.setBiographie(u.getProfil().getBiographie());
        resp.setImageUrl(u.getProfil().getImageUrl());
        resp.setNiveau(u.getProfil().getNiveau());
        resp.setExperiencePoints(u.getProfil().getExperiencePoints());

        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/me/bio")
    public ResponseEntity<?> modifierBio(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request) {
        Long userId = sessionManager.getUserId(token);
        if (userId == null) return ResponseEntity.status(401).build();

        Utilisateur u = utilisateurService.trouverParId(userId);
        profilService.modifierBiographie(u.getProfil().getId(), request.get("biographie"));
        return ResponseEntity.ok(Map.of("message", "Biographie mise à jour"));
    }
}