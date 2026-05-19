package com.projet.poker.controller;

import com.projet.poker.dto.AmiDetailDTO;
import com.projet.poker.model.persist.Amitie;
import com.projet.poker.model.persist.Profil;
import com.projet.poker.model.persist.Utilisateur;
import com.projet.poker.security.SessionManager;
import com.projet.poker.service.AmitieService;
import com.projet.poker.service.UtilisateurService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/amities")
@CrossOrigin("*")
public class AmitieController {

  private final SessionManager sessionManager;
  private final AmitieService amitieService;
  private final UtilisateurService utilisateurService;

  public AmitieController(SessionManager sessionManager,
                          AmitieService amitieService,
                          UtilisateurService utilisateurService) {
    this.sessionManager = sessionManager;
    this.amitieService = amitieService;
    this.utilisateurService = utilisateurService;
  }

  // Lister les amis (Actifs et en attente)
  @GetMapping("/me")
  public ResponseEntity<?>
  mesAmis(@RequestHeader("Authorization") String token) {
    Long myId = sessionManager.getUserId(token);
    if (myId == null) {
      return ResponseEntity.status(401).build();
    }

    // On récupère la liste brute de tes amitiés depuis le service
    // (Adapte le type selon ce que renvoie ta vraie méthode)
    List<Amitie> amitiesBrutes = amitieService.listerAmis(myId);

    // 2. On les transforme pour le Front
    List<AmiDetailDTO> listeFront = new ArrayList<>();

    for (Amitie a : amitiesBrutes) {
      AmiDetailDTO dto = new AmiDetailDTO();
      dto.setAmitieId(a.getId());
      dto.setStatus(a.getStatus().name()); // "PENDING" ou "ACCEPTED"

      // Si c'est moi le demandeur, l'ami est le récepteur
      if (a.getReceveur().getId().equals(myId)) {
        dto.setAmiLogin(a.getDemandeur().getLogin());
        dto.setIsReceiver(
            true); // C'est moi qui ai reçu la demande -> BOUTONS OK
      } else {
        // Sinon, je suis le demandeur
        dto.setAmiLogin(a.getReceveur().getLogin());
        dto.setIsReceiver(false); // J'ai envoyé la demande -> "EN ATTENTE"
      }

      listeFront.add(dto);
    }

    return ResponseEntity.ok(listeFront);
  }

  // Envoyer une demande d'ami via un pseudo (login)
  @PostMapping("/demande")
  public ResponseEntity<?>
  demanderAmi(@RequestHeader("Authorization") String token,
              @RequestBody Map<String, String> request) {
    Long myId = sessionManager.getUserId(token);
    if (myId == null) {
      return ResponseEntity.status(401).build();
    }

    try {
      Utilisateur target =
          utilisateurService.trouverParLogin(request.get("targetLogin"));
      amitieService.envoyerDemande(myId, target.getId());
      return ResponseEntity.ok(
          Map.of("message", "Demande envoyée à " + target.getLogin()));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(
          Map.of("error", "Joueur introuvable"));
    }
  }

  // Accepter ou Refuser
  @PostMapping("/reponse")
  public ResponseEntity<?>
  repondreDemande(@RequestHeader("Authorization") String token,
                  @RequestBody Map<String, Object> request) {
    Long myId = sessionManager.getUserId(token);
    if (myId == null) {
      return ResponseEntity.status(401).build();
    }

    Long amitieId = Long.valueOf(request.get("amitieId").toString());
    boolean accepter = (boolean)request.get("accepter");

    if (accepter) {
      amitieService.accepterDemande(amitieId);
    } else {
      amitieService.supprimerOuRefuser(amitieId);
    }
    return ResponseEntity.ok(Map.of("message", "Action enregistrée"));
  }

  // Récupérer le profil d'un ami à partir de l'amitieId
  @GetMapping("/profil/{amitieId}")
  public ResponseEntity<?> getProfilAmi(@RequestHeader("Authorization") String token,
                                        @PathVariable Amitie amitie) {
    Long myId = sessionManager.getUserId(token);
    if (myId == null) {
      return ResponseEntity.status(401).build();
    }



      // Sécurité : On vérifie que l'utilisateur connecté fait bien partie de cette relation
      if (!amitie.getDemandeur().getId().equals(myId) && !amitie.getReceveur().getId().equals(myId)) {
        return ResponseEntity.status(403).body(Map.of("error", "Vous ne faites pas partie de cette amitié."));
      }

      // 2. On détermine qui est l'ami
      Utilisateur ami;
      if (amitie.getDemandeur().getId().equals(myId)) {
        ami = amitie.getReceveur();
      } else {
        ami = amitie.getDemandeur();
      }

      // 3. On construit le DTO ProfilResponse avec les données de l'ami
      com.projet.poker.dto.ProfilResponse response = new com.projet.poker.dto.ProfilResponse();
      response.setLogin(ami.getLogin());
      response.setEmail(ami.getEmail());
      
    
      Profil profil = ami.getProfil();
      // Ajuste les getters ci-dessous selon les variables réelles de ton entité Utilisateur (ou son Profil lié)
      response.setBiographie(profil.getBiographie()); 
      response.setNiveau(profil.getNiveau());
      response.setLogin(ami.getLogin());

      return ResponseEntity.ok(response);

  }
}
