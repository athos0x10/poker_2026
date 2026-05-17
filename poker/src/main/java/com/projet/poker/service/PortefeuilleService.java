package com.projet.poker.service;

import com.projet.poker.model.persist.Portefeuille;
import com.projet.poker.repository.PortefeuilleRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortefeuilleService {

  private final PortefeuilleRepository portefeuilleRepository;

  public PortefeuilleService(PortefeuilleRepository portefeuilleRepository) {
    this.portefeuilleRepository = portefeuilleRepository;
  }

  /**
   * Récupère un portefeuille par son ID
   */
  public Portefeuille recupererPortefeuille(Long id) {
    return portefeuilleRepository.findById(id).orElseThrow(
        ()
            -> new RuntimeException("Portefeuille introuvable avec l'id : " +
                                    id));
  }

  /**
   * Récupère un portefeuille par l'identifiant de l'utilisateur associé
   */
  public Portefeuille recupererParUtilisateurId(Long utilisateurId) {
    Optional<Portefeuille> maybe =
        portefeuilleRepository.findByUtilisateurId(utilisateurId);
    return maybe.orElseThrow(
        ()
            -> new RuntimeException(
                "Portefeuille introuvable pour l'utilisateur : " +
                utilisateurId));
  }

  /**
   * Consulte le solde actuel
   */
  public BigDecimal consulterSolde(Long id) {
    return recupererPortefeuille(id).getGlobalBalance();
  }

  /**
   * Ajoute de l'argent au portefeuille (ex: dépôt ou gain de partie)
   */
  @Transactional
  public void ajouterFonds(Long id, BigDecimal montant) {
    if (montant.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(
          "Le montant à ajouter doit être positif.");
    }

    Portefeuille portefeuille = recupererPortefeuille(id);
    portefeuille.addFunds(montant);
    portefeuilleRepository.save(portefeuille);
  }

  @Transactional
  public void ajouterFondsByUtilisateurId(Long utilisateurId,
                                          BigDecimal montant) {
    if (montant.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(
          "Le montant à ajouter doit être positif.");
    }
    Portefeuille portefeuille = recupererParUtilisateurId(utilisateurId);
    portefeuille.addFunds(montant);
    portefeuilleRepository.save(portefeuille);
  }

  /**
   * Retire de l'argent du portefeuille (ex: mise ou retrait) La validation du
   * solde suffisant est déjà gérée dans l'entité Portefeuille
   */
  @Transactional
  public void retirerFonds(Long id, BigDecimal montant) {
    if (montant.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(
          "Le montant à retirer doit être positif.");
    }

    Portefeuille portefeuille = recupererPortefeuille(id);

    // Cette méthode lèvera une IllegalArgumentException si le solde est
    // insuffisant
    portefeuille.withdrawFunds(montant);

    portefeuilleRepository.save(portefeuille);
  }

  @Transactional
  public void retirerFondsByUtilisateurId(Long utilisateurId,
                                          BigDecimal montant) {
    if (montant.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(
          "Le montant à retirer doit être positif.");
    }

    Portefeuille portefeuille = recupererParUtilisateurId(utilisateurId);

    // Cette méthode lèvera une IllegalArgumentException si le solde est
    // insuffisant
    portefeuille.withdrawFunds(montant);

    portefeuilleRepository.save(portefeuille);
  }
}
