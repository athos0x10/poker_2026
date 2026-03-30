package com.projet.poker.model.persist;

import java.util.List;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Utilisateur {
    // Attributs de l'entité Utilisateur

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // L'identifiant unique de l'utilisateur
    private Long id;
    // Le login de l'utilisateur
    private String login;
    // Le mot de passe haché de l'utilisateur
    private String passwordHash;
    // L'adresse e-mail de l'utilisateur
    private String email;
    // Date de création du compte utilisateur
    private LocalDateTime dateCreation;

    // Relations avec d'autres entités
    // Relation OneToOne avec l'entité Profil
    @OneToOne(mappedBy = "utilisateur")
    private Profil profil;
    // Relation OneToOne avec l'entité Portefeuille
    @OneToOne(mappedBy = "utilisateur")
    private Portefeuille portefeuille;
    // Relation OneToMany avec l'entité Amitie
    @OneToMany(mappedBy = "utilisateur")
    private List<Amitie> amities;
    // Relation OneToMany avec l'entité SessionJoueur
    @OneToMany(mappedBy = "utilisateur")
    private List<SessionJoueur> sessionsJoueur;

    // Constructeurs
    /**
     * Constructeur par défaut de l'entité Utilisateur Ce constructeur est
     * nécessaire pour que JPA puisse instancier l'entité lors de la
     * récupération des données depuis la base de données.
     */
    public Utilisateur() {
    }

    /**
     * Constructeur de l'entité Utilisateur avec tous les attributs
     *
     * @param login Le login de l'utilisateur
     * @param passwordHash Le mot de passe haché de l'utilisateur
     * @param email L'adresse e-mail de l'utilisateur
     * @param dateCreation La date de création du compte utilisateur
     */
    public Utilisateur(String login, String passwordHash, String email,
            LocalDateTime dateCreation) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.email = email;
        this.dateCreation = dateCreation;
    }

    // Getters et setters pour les attributs de l'entité Utilisateur
    /**
     * Getter pour l'identifiant unique de l'utilisateur
     *
     * @return L'identifiant unique de l'utilisateur
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter pour l'identifiant unique de l'utilisateur
     *
     * @param id L'identifiant unique de l'utilisateur à définir
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter pour le login de l'utilisateur
     *
     * @return Le login de l'utilisateur
     */
    public String getLogin() {
        return login;
    }

    /**
     * Setter pour le login de l'utilisateur
     *
     * @param login Le login de l'utilisateur à définir
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Getter pour le mot de passe haché de l'utilisateur
     *
     * @return Le mot de passe haché de l'utilisateur
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Setter pour le mot de passe haché de l'utilisateur
     *
     * @param passwordHash Le mot de passe haché de l'utilisateur à définir
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Getter pour l'adresse e-mail de l'utilisateur
     *
     * @return L'adresse e-mail de l'utilisateur
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter pour l'adresse e-mail de l'utilisateur
     *
     * @param email L'adresse e-mail de l'utilisateur à définir
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter pour la date de création du compte utilisateur
     *
     * @return La date de création du compte utilisateur
     */
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    /**
     * Setter pour la date de création du compte utilisateur
     *
     * @param dateCreation La date de création du compte utilisateur à définir
     */
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    // Getters et setters pour les relations avec d'autres entités
    /**
     * Getter pour le profil associé à l'utilisateur
     *
     * @return Le profil associé à l'utilisateur
     */
    public Profil getProfil() {
        return profil;
    }

    /**
     * Setter pour le profil associé à l'utilisateur
     *
     * @param profil Le profil associé à l'utilisateur à définir
     */
    public void setProfil(Profil profil) {
        this.profil = profil;
    }

    /**
     * Getter pour le portefeuille associé à l'utilisateur
     *
     * @return Le portefeuille associé à l'utilisateur
     */
    public Portefeuille getPortefeuille() {
        return portefeuille;
    }

    /**
     * Setter pour le portefeuille associé à l'utilisateur
     *
     * @param portefeuille Le portefeuille associé à l'utilisateur à définir
     */
    public void setPortefeuille(Portefeuille portefeuille) {
        this.portefeuille = portefeuille;
    }

    /**
     * Getter pour la liste des amitiés de l'utilisateur
     *
     * @return La liste des amitiés de l'utilisateur
     */
    public List<Amitie> getAmities() {
        return amities;
    }

    /**
     * Setter pour la liste des amitiés de l'utilisateur
     *
     * @param amities La liste des amitiés de l'utilisateur à définir
     */
    public void setAmities(List<Amitie> amities) {
        this.amities = amities;
    }

    /**
     * Getter pour la liste des sessions de jeu de l'utilisateur
     *
     * @return La liste des sessions de jeu de l'utilisateur
     */
    public List<SessionJoueur> getSessionsJoueur() {
        return sessionsJoueur;
    }

    /**
     * Setter pour la liste des sessions de jeu de l'utilisateur
     *
     * @param sessionsJoueur La liste des sessions de jeu de l'utilisateur à
     * définir
     */
    public void setSessionsJoueur(List<SessionJoueur> sessionsJoueur) {
        this.sessionsJoueur = sessionsJoueur;
    }

    // Autres méthodes de l'entité Utilisateur
    /**
     * Authentifie l'utilisateur avec le mot de passe fourni
     *
     * @param pswHash Le mot de passe haché à vérifier
     * @return true si l'utilisateur est authentifié, false sinon
     */
    boolean authentifier(String pswHash) {
        return this.passwordHash.equals(pswHash);
    }
}
