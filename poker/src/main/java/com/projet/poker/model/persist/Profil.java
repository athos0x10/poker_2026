package com.projet.poker.model.persist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name = "profils")
public class Profil {
    // Attributs de l'entité Profil

    // L'identifiant unique du profil
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Biographie de l'utilisateur
    @Column(length = 500)
    private String biographie;

    // URL de l'image de profil de l'utilisateur
    @Column(name = "image_url")
    private String imageUrl;

    // Point de réputation de l'utilisateur
    @Column(name = "experience_points", nullable = false)
    private int experiencePoints = 0;

    // Niveau de l'utilisateur basé sur les points de réputation
    @Column(nullable = false)
    private int niveau = 1;

    // Relations avec d'autres entités
    // Relation OneToOne avec l'entité Utilisateur
    @OneToOne
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    @ToString.Exclude
    private Utilisateur utilisateur;

    // Constructeurs
    /**
     * Constructeur par défaut de l'entité Profil Ce constructeur est nécessaire
     * pour que JPA puisse instancier l'entité lors de la récupération des
     * données depuis la base de données.
     */
    public Profil() {
    }

    /**
     * Constructeur de l'entité Profil avec tous les attributs
     *
     * @param biographie La biographie de l'utilisateur
     * @param imageUrl L'URL de l'image de profil de l'utilisateur
     * @param experiencePoints Les points de réputation de l'utilisateur
     * @param niveau Le niveau de l'utilisateur basé sur les points de
     * réputation
     * @param utilisateur L'utilisateur associé à ce profil
     */
    public Profil(String biographie, String imageUrl, int experiencePoints,
            int niveau, Utilisateur utilisateur) {
        this.biographie = biographie;
        this.imageUrl = imageUrl;
        this.experiencePoints = experiencePoints;
        this.niveau = niveau;
        this.utilisateur = utilisateur;
    }

    // Getters et setters pour les attributs de l'entité Profil
    /**
     * Obtient la biographie de l'utilisateur
     */
    public String getBiographie() {
        return biographie;
    }

    /**
     * Définit la biographie de l'utilisateur
     *
     * @param biographie La biographie à définir pour l'utilisateur
     */
    public void setBiographie(String biographie) {
        this.biographie = biographie;
    }

    /**
     * Obtient l'URL de l'image de profil de l'utilisateur
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Définit l'URL de l'image de profil de l'utilisateur
     *
     * @param imageUrl L'URL de l'image de profil à définir pour l'utilisateur
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Obtient les points de réputation de l'utilisateur
     */
    public int getExperiencePoints() {
        return experiencePoints;
    }

    /**
     * Définit les points de réputation de l'utilisateur
     *
     * @param experiencePoints Les points de réputation à définir pour
     * l'utilisateur
     */
    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    /**
     * Obtient le niveau de l'utilisateur basé sur les points de réputation
     */
    public int getNiveau() {
        return niveau;
    }

    /**
     * Définit le niveau de l'utilisateur basé sur les points de réputation
     *
     * @param niveau Le niveau à définir pour l'utilisateur
     */
    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    /**
     * Getter pour l'identifiant unique du profil
     *
     * @return L'identifiant unique du profil
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter pour l'identifiant unique du profil
     *
     * @param id L'identifiant unique du profil à définir
     */
    public void setId(Long id) {
        this.id = id;
    }

    // Getters et setters pour les relations avec d'autres entités
    /**
     * Obtient l'utilisateur associé à ce profil
     */
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    /**
     * Définit l'utilisateur associé à ce profil
     *
     * @param utilisateur L'utilisateur à associer à ce profil
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    // Autres méthodes de l'entité Profil
    /**
     * Ajoute des points de réputation à l'utilisateur
     *
     * @param points Les points de réputation à ajouter à l'utilisateur
     */
    public void addExperiencePoints(int points) {
        this.experiencePoints += points;
        calculerNiveau();
    }

    /**
     * Calcule le niveau de l'utilisateur basé sur les points de réputation
     * selon une formule simple (par exemple, 100 points = 1 niveau)
     */
    public void calculerNiveau() {
        this.niveau = this.experiencePoints / 100;
    }
}
