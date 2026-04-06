package com.projet.poker.service;

import com.projet.poker.model.persist.Profil;
import com.projet.poker.repository.ProfilRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfilService {

    private final ProfilRepository profilRepository;

    public ProfilService(ProfilRepository profilRepository) {
        this.profilRepository = profilRepository;
    }

    /**
     * Récupère un profil par son ID
     */
    public Profil recupererProfil(Long id) {
        return profilRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Profil introuvable avec l'id : " + id));
    }

    /**
     * Met à jour la biographie et l'image de profil
     */
    @Transactional
    public Profil mettreAJourInfos(Long id, String biographie, String imageUrl) {
        Profil profil = recupererProfil(id);
        profil.setBiographie(biographie);
        profil.setImageUrl(imageUrl);
        return profilRepository.save(profil);
    }

    /**
     * Ajoute de l'expérience à un joueur (ex: après une partie gagnée) La
     * logique de calcul du niveau est gérée automatiquement par l'entité
     */
    @Transactional
    public void ajouterExperience(Long id, int points) {
        Profil profil = recupererProfil(id);

        // On utilise la méthode de ton entité qui gère l'XP + le calcul du niveau
        profil.addExperiencePoints(points);

        profilRepository.save(profil);
    }

    /**
     * Réinitialise les statistiques (optionnel)
     */
    @Transactional
    public void reinitialiserExperience(Long id) {
        Profil profil = recupererProfil(id);
        profil.setExperiencePoints(0);
        profil.setNiveau(1);
        profilRepository.save(profil);
    }
}
