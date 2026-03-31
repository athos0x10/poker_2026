package com.projet.poker.model.persist;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;


@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
}) // evite une amitie AB et BA
public class Amitie {
    // Attributs de l'entité Profil

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // L'identifiant unique de l'amitie
    private Long id;
    // Status de l'amitie : PENDING, ACCEPTED, BLOCKED
    private FriendStatus status;
    // Date de creation de l'amitie
    private LocalDateTime since;

    // Relations avec d'autres entités
    // Relation ManyToOne avec l'entité Utilisateur 1
    @ManyToOne
    @JoinColumn(name = "user1_id")
    private Utilisateur user1;
    // Relation ManyToOne avec l'entité Utilisateur 2
    @ManyToOne
    @JoinColumn(name = "user2_id")
    private Utilisateur user2;


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
     * @param user1 L'utilisateur 1 formant l'amitié
     * @param user2 L'utilisateur 2 formant l'amitié
     * @param status L'etat de l'amitié : PENDING, ACCEPTED, BLOCKED
     * @param since La date de création de l'amitié
     */
    public Amitie(Utilisateur user1, Utilisateur user2, FriendStatus status, LocalDateTime since) {
        this.status = status;
        this.since = since;
        this.user1 = user1;
        this.user2 = user2;
        validerNonAutoAmitie();
    }

    // verification que l'amitie est possible 
    @PrePersist
    @PreUpdate
    private void validerNonAutoAmitie() {
        if (user1 != null && user2 != null && user1.getId().equals(user2.getId())) {
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
    public void getDateCreation(LocalDateTime since) {
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
     * Obtient l'utilisateur 1 associé à cette amitie
     */
    public Utilisateur getUser1() {
        return user1;
    }

    /**
     * Obtient l'utilisateur 2 associé à cette amitie
     */
    public Utilisateur getUser2() {
        return user2;
    }



}
