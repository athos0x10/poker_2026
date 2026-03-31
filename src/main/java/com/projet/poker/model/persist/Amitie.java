package com.projet.poker.model.persist;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;


@Entity
public class Amitie {
    // Attributs de l'entité Profil

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // L'identifiant unique de l'amitie
    private Long id;
    // ID de l'user 1
    private Long user1Id;
    // ID de l'user 2
    private Long user2Id;
    // Status de l'amitie : PENDING, ACCEPTED, BLOCKED
    private FriendStatus status;
    // Date de creation de l'amitie
    private LocalDateTime since;

    // Relations avec d'autres entités
    // Relation ManyToOne avec l'entité Utilisateur 1
    @ManyToOne
    private Utilisateur user1;
    // Relation ManyToOne avec l'entité Utilisateur 2
    @ManyToOne
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
        this.user1Id = user1.getId();
        this.user2Id = user2.getId();
        this.status = status;
        this.since = since;
        this.user1 = user1;
        this.user2 = user2;
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
     * Obtient l'ID de utilisateur 1 associé à cette amitie
     */
    public Long getIdUtilisateur1() {
        return user1Id;
    }

    // Getters et setters pour les relations avec d'autres entités
    /**
     * Obtient l'ID de utilisateur 1 associé à cette amitie
     */
    public Long getIdUtilisateur2() {
        return user2Id;
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
