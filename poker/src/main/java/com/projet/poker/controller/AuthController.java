package com.projet.poker.contoller;

import com.projet.poker.dto.InscriptionRequest;
import com.projet.poker.dto.LoginRequest;
import com.projet.poker.model.persist.Utilisateur;
import com.projet.poker.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin("*") // Pour permettre à ton Front JS de communiquer sans blocage
public class AuthController {

    private final UtilisateurService utilisateurService;

    public AuthController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> inscrire(@Valid @RequestBody InscriptionRequest request) {
        try {
            Utilisateur u = new Utilisateur();
            u.setLogin(request.getLogin());
            u.setEmail(request.getEmail());
            u.setPasswordHash(request.getPasswordHash());
            Utilisateur cree = utilisateurService.enregistrerUtilisateur(u);
            return ResponseEntity.ok(cree.getId()); // On renvoie l'ID pour le Front
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> seConnecter(@Valid @RequestBody LoginRequest request) {
        // Logique simplifiée sans Spring Security pour tes tests
        try {
            Utilisateur u = utilisateurService.trouverParLogin(request.getLogin());
            if (u.getPasswordHash().equals(request.getPassword())) { // À remplacer par .matches() plus tard
                return ResponseEntity.ok(u.getId());
            }
            return ResponseEntity.status(401).body("Identifiants incorrects");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé");
        }
    }
}