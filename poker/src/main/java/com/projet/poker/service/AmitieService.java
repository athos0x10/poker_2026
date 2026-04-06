package com.projet.poker.service;

import com.projet.poker.model.persist.Amitie;
import com.projet.poker.model.persist.FriendStatus;
import com.projet.poker.model.persist.Utilisateur;
import com.projet.poker.repository.AmitieRepository;
import com.projet.poker.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AmitieService {

  private final AmitieRepository amitieRepository;
  private final UtilisateurRepository utilisateurRepository;

  public AmitieService(AmitieRepository amitieRepository,
                       UtilisateurRepository utilisateurRepository) {
    this.amitieRepository = amitieRepository;
    this.utilisateurRepository = utilisateurRepository;
  }

  /**
   * Envoie une demande d'ami (Statut: PENDING)
   */
  @Transactional
  public Amitie envoyerDemande(Long demandeurId, Long receveurId) {
    Utilisateur demandeur =
        utilisateurRepository.findById(demandeurId)
            .orElseThrow(() -> new RuntimeException("Demandeur introuvable"));
    Utilisateur receveur =
        utilisateurRepository.findById(receveurId)
            .orElseThrow(() -> new RuntimeException("Receveur introuvable"));

    // Vérifier si une relation n'existe pas déjà dans un sens ou dans l'autre
    if (amitieRepository.existsByDemandeurAndReceveur(demandeur, receveur) ||
        amitieRepository.existsByDemandeurAndReceveur(receveur, demandeur)) {
      throw new IllegalStateException(
          "Une relation existe déjà entre ces deux joueurs.");
    }

    Amitie nouvelleAmitie = new Amitie();
    nouvelleAmitie.setDemandeur(demandeur);
    nouvelleAmitie.setReceveur(receveur);
    nouvelleAmitie.setStatus(FriendStatus.PENDING);

    // La date et la vérification d'auto-amitié sont gérées par @PrePersist dans
    // ton entité
    return amitieRepository.save(nouvelleAmitie);
  }

  /**
   * Accepte une demande d'ami
   */
  @Transactional
  public void accepterDemande(Long amitieId) {
    Amitie amitie = amitieRepository.findById(amitieId).orElseThrow(
        () -> new RuntimeException("Demande introuvable"));

    if (amitie.getStatus() != FriendStatus.PENDING) {
      throw new IllegalStateException(
          "Seule une demande en attente peut être acceptée.");
    }

    amitie.setStatus(FriendStatus.ACCEPTED);
    amitieRepository.save(amitie);
  }

  /**
   * Refuse ou supprime une amitié (suppression en base)
   */
  @Transactional
  public void supprimerOuRefuser(Long amitieId) {
    if (!amitieRepository.existsById(amitieId)) {
      throw new RuntimeException("Amitié introuvable");
    }
    amitieRepository.deleteById(amitieId);
  }

  /**
   * Bloque un utilisateur
   */
  @Transactional
  public void bloquer(Long amitieId) {
    Amitie amitie = amitieRepository.findById(amitieId).orElseThrow(
        () -> new RuntimeException("Relation introuvable"));

    amitie.setStatus(FriendStatus.BLOCKED);
    amitieRepository.save(amitie);
  }
}
