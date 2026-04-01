package com.projet.poker.model.persist;

import jakarta.persistence.*;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_demandeur", "user_recepteur"})
}, name = "amities") // evite une amitie AB et BA
public class Amitie {
    // Attributs de l'entité Profil

    // L'identifiant unique de l'amitie
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Status de l'amitie : PENDING, ACCEPTED, BLOCKED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;

    // Date de creation de l'amitie
    @Column(nullable = false, updatable = false)
    private LocalDateTime since;

    // Relations avec d'autres entités
    // Relation ManyToOne avec l'entité Utilisateur demandeuse
    @ManyToOne
    @JoinColumn(name = "user_demandeur")
    private Utilisateur user_demandeur;
    // Relation ManyToOne avec l'entité Utilisateur receptrice 
    @ManyToOne
    @JoinColumn(name = "user_recepteur")
    private Utilisateur user_recepteur;

    // Constructeurs
    /**
     * Constructeur par défaut de l'entité Amitie Ce constructeur est nécessaire
     * pour que JPA puisse instancier l'entité lors de la récupération des
     * données depuis la base de données.
     */
    public Amitie() {
    }

    /**
     * Constructeur de l'entité Profil avec tous les attributs
     *
     * @param user_demandeur L'utilisateur 1 formant l'amitié
     * @param user_recepteur L'utilisateur 2 formant l'amitié
     * @param status L'etat de l'amitié : PENDING, ACCEPTED, BLOCKED
     * @param since La date de création de l'amitié
     */
    public Amitie(Utilisateur user_demandeur, Utilisateur user_recepteur, FriendStatus status, LocalDateTime since) {
        this.status = status;
        this.since = since;
        this.user_demandeur = user_demandeur;
        this.user_recepteur = user_recepteur;
        validerNonAutoAmitie();
    }

    // verification que l'amitie est possible 
    @PrePersist
    @PreUpdate
    private void validerNonAutoAmitie() {
        if (user_demandeur != null && user_recepteur != null && user_demandeur.equals(user_recepteur)) {
            throw new IllegalStateException("Un utilisateur ne peut pas créer une amitié avec lui-même.");
        }
    }

    // Getters et setters pour les attributs de l'entité Amitie
    /**
     * Getter pour le status de l'amitie
     */
    public FriendStatus getStatus() {
        return status;
    }

    /**
     * Setter pour le status de l'amitie
     *
     * @param status Nouveau status de l'amitie
     */
    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    /**
     * Getter pour la date de creation de l'amitie
     */
    public LocalDateTime getDateCreation() {
        return since;
    }

    /**
     * Getter pour la date de creation de l'amitie
     *
     * @param since Nouvelle date de creation de l'amitie
     */
    public void setDateCreation(LocalDateTime since) {
        this.since = since;
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
     * Obtient l'utilisateur demandeur associé à cette amitie
     */
    public Utilisateur getUserDemandeur() {
        return user_demandeur;
    }

    /**
     * Obtient l'utilisateur recepteur associé à cette amitie
     */
    public Utilisateur getUserRecepteur() {
        return user_recepteur;
    }

}
