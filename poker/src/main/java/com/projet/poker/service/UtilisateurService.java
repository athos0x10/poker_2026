package com.projet.poker.service;

import com.projet.poker.model.persist.Portefeuille;
import com.projet.poker.model.persist.Profil;
import com.projet.poker.model.persist.Utilisateur;
import com.projet.poker.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Enregistre un nouvel utilisateur avec son profil et son portefeuille par
     * défaut.
     */
    @Transactional
    public Utilisateur enregistrerUtilisateur(Utilisateur utilisateur) {
        // 1. Vérifications de sécurité (doublons)
        if (utilisateurRepository.existsByLogin(utilisateur.getLogin())) {
            throw new RuntimeException("Ce login est déjà utilisé.");
        }
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        // 2. Initialisation du Profil par défaut
        Profil profil = new Profil();
        profil.setUtilisateur(utilisateur);
        profil.setBiographie("Nouveau joueur de Poker");
        utilisateur.setProfil(profil);

        // 3. Initialisation du Portefeuille avec un bonus de bienvenue (ex: 500.00)
        Portefeuille portefeuille = new Portefeuille();
        portefeuille.setUtilisateur(utilisateur);
        portefeuille.setGlobalBalance(new BigDecimal("500.00"));
        utilisateur.setPortefeuille(portefeuille);

        // 4. Sauvegarde (le CascadeType.ALL dans l'entité sauvera Profil et Portefeuille automatiquement)
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public Utilisateur trouverParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }


    /**
     * Récupère un utilisateur par son login
     */
    public Utilisateur trouverParLogin(String login) {
        return utilisateurRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    /**
     * Récupère la liste des amis confirmés d'un utilisateur
     */
    public List<Utilisateur> recupererAmisConfirmes(Long userId) {
        Utilisateur user = trouverParId(userId);
        return user.getAmis(); // Utilise la méthode logique de ton entité
    }

    /**
     * Supprimer un compte
     */
    @Transactional
    public void supprimerCompte(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer : utilisateur introuvable");
        }
        utilisateurRepository.deleteById(id);
    }
}
