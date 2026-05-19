package com.projet.poker.controller;

import com.projet.poker.dto.ProfilResponse;
import com.projet.poker.model.persist.Utilisateur;
import com.projet.poker.security.SessionManager;
import com.projet.poker.service.AmitieService;
import com.projet.poker.service.UtilisateurService;
import com.projet.poker.service.ProfilService; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profils")
@CrossOrigin("*")
public class ProfilController {

    private final SessionManager sessionManager;
    private final UtilisateurService utilisateurService;
    private final ProfilService profilService;
    private final AmitieService amitieService;

    public ProfilController(SessionManager sessionManager, UtilisateurService utilisateurService, ProfilService profilService, AmitieService amitieService) {
        this.sessionManager = sessionManager;
        this.utilisateurService = utilisateurService;
        this.profilService = profilService;
        this.amitieService = amitieService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMonProfil(@RequestHeader("Authorization") String token) {
        Long userId = sessionManager.getUserId(token);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Session invalide"));

        Utilisateur u = utilisateurService.trouverParId(userId);
        ProfilResponse resp = mapToProfilResponse(u, true);

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/amis")
    public ResponseEntity<?> getProfilsAmis(@RequestHeader("Authorization") String token) {
        Long userId = sessionManager.getUserId(token);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Session invalide"));

        List<Utilisateur> amis = utilisateurService.recupererAmisConfirmes(userId);
        List<ProfilResponse> response = amis.stream()
            .map(u -> mapToProfilResponse(u, false))
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfil(@RequestHeader("Authorization") String token,
                                       @PathVariable Long id) {
        Long userId = sessionManager.getUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Session invalide"));
        }
        if (!amitieService.sontAmis(userId, id)) {
            return ResponseEntity.status(403).body(Map.of("error", "Accès interdit : ce profil n'est pas accessible"));
        }

        Utilisateur u = utilisateurService.trouverParId(id);
        ProfilResponse resp = mapToProfilResponse(u, false);
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

    private ProfilResponse mapToProfilResponse(Utilisateur u, boolean includeEmail) {
        ProfilResponse resp = new ProfilResponse();
        resp.setLogin(u.getLogin());
        if (includeEmail) {
            resp.setEmail(u.getEmail());
        }
        resp.setBiographie(u.getProfil().getBiographie());
        resp.setImageUrl(u.getProfil().getImageUrl());
        resp.setNiveau(u.getProfil().getNiveau());
        resp.setExperiencePoints(u.getProfil().getExperiencePoints());
        return resp;
    }
}