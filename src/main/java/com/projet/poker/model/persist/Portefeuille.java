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
@Table(name = "portefeuilles")
public class Portefeuille {
    // Attributs de l'entité Portefeuille

    // L'identifiant unique du portefeuille
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Solde d'argent réel dans le portefeuille
    @Column(nullable = false, precision = 15, scale = 2)
    private double globalBalance;

    // Relations avec d'autres entités
    // Relation OneToOne avec l'entité Utilisateur
    @OneToOne
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    // Constructeurs
    /**
     * Constructeur par défaut de l'entité Portefeuille Ce constructeur est
     * nécessaire pour que JPA puisse instancier l'entité lors de la
     * récupération des données depuis la base de données.
     */
    public Portefeuille() {
    }

    /**
     * Constructeur de l'entité Portefeuille avec tous les attributs
     *
     * @param globalBalance Le solde d'argent réel dans le portefeuille
     * @param utilisateur L'utilisateur associé à ce portefeuille
     */
    public Portefeuille(double globalBalance, Utilisateur utilisateur) {
        this.globalBalance = globalBalance;
        this.utilisateur = utilisateur;
    }

    // Getters et setters pour les attributs de l'entité Portefeuille
    /**
     * Obtient le solde d'argent réel dans le portefeuille
     */
    public double getGlobalBalance() {
        return globalBalance;
    }

    /**
     * Modifie le solde d'argent réel dans le portefeuille
     *
     * @param globalBalance Le nouveau solde d'argent réel à définir
     */
    public void setGlobalBalance(double globalBalance) {
        this.globalBalance = globalBalance;
    }

    /**
     * Obtient l'id associé à ce portefeuille
     */
    public Long getId() {
        return id;
    }

    /**
     * Modifie l'id associé à ce portefeuille
     *
     * @param id Le nouvel id à définir pour ce portefeuille
     *
     */
    public void setId(Long id) {
        this.id = id;
    }

    // Getters et setters pour les relations avec d'autres entités
    /**
     * Obtient l'utilisateur associé à ce portefeuille
     */
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    /**
     * Modifie l'utilisateur associé à ce portefeuille
     *
     * @param utilisateur Le nouvel utilisateur à associer à ce portefeuille
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    // Méthodes supplémentaires pour la logique métier de l'entité Portefeuille
    /**
     * Ajoute une somme d'argent au solde du portefeuille
     *
     * @param amount La somme d'argent à ajouter au solde du portefeuille
     */
    public void addFunds(double amount) {
        this.globalBalance += amount;
    }

    /**
     * Retire une somme d'argent du solde du portefeuille
     *
     * @param amount La somme d'argent à retirer du solde du portefeuille
     * @throws IllegalArgumentException si le montant à retirer est supérieur au
     * solde actuel
     */
    public void withdrawFunds(double amount) {
        if (amount > this.globalBalance) {
            throw new IllegalArgumentException("Le montant à retirer dépasse le solde actuel du portefeuille.");
        }
        this.globalBalance -= amount;
    }

}
