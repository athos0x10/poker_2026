package com.projet.poker.model.persist;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
  // Attributs de l'entité Utilisateur

  // L'identifiant unique de l'utilisateur
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  // Le login de l'utilisateur
  @NotBlank(message = "Le login est obligatoire")
  @Size(min = 3, max = 20,
        message = "Le login doit faire entre 3 et 20 caractères")
  @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
           message = "Le login contient des caractères non autorisés")
  @Column(nullable = false, unique = true, length = 20)
  private String login;

  // Le mot de passe haché de l'utilisateur
  @NotBlank @Column(nullable = false) private String passwordHash;

  // L'adresse e-mail de l'utilisateur
  @NotBlank(message = "L'email est obligatoire")
  @Email(message = "Format de l'email invalide")
  @Column(nullable = false, unique = true)
  private String email;

  // Date de création du compte utilisateur
  @Column(nullable = false, updatable = false)
  private LocalDateTime dateCreation;

  // Relations avec d'autres entités
  // Relation OneToOne avec l'entité Profil
  @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL)
  private Profil profil;

  // Relation OneToOne avec l'entité Portefeuille
  @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL)
  private Portefeuille portefeuille;

  // Relation OneToMany avec l'entité Amitie
  // La relation est séparé en deux listes (demandeur/receveur)
  @OneToMany(mappedBy = "demandeur") private List<Amitie> demandesEnvoyees;

  @OneToMany(mappedBy = "receveur") private List<Amitie> demandesRecues;

  // Relation OneToMany avec l'entité SessionJoueur
  @OneToMany(mappedBy = "utilisateur")
  private List<SessionJoueur> sessionsJoueur;

  // Constructeurs
  /**
   * Constructeur par défaut de l'entité Utilisateur Ce constructeur est
   * nécessaire pour que JPA puisse instancier l'entité lors de la
   * récupération des données depuis la base de données.
   */
  public Utilisateur() {}

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
  public Long getId() { return id; }

  /**
   * Setter pour l'identifiant unique de l'utilisateur
   *
   * @param id L'identifiant unique de l'utilisateur à définir
   */
  public void setId(Long id) { this.id = id; }

  /**
   * Getter pour le login de l'utilisateur
   *
   * @return Le login de l'utilisateur
   */
  public String getLogin() { return login; }

  /**
   * Setter pour le login de l'utilisateur
   *
   * @param login Le login de l'utilisateur à définir
   */
  public void setLogin(String login) { this.login = login; }

  /**
   * Getter pour le mot de passe haché de l'utilisateur
   *
   * @return Le mot de passe haché de l'utilisateur
   */
  public String getPasswordHash() { return passwordHash; }

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
  public String getEmail() { return email; }

  /**
   * Setter pour l'adresse e-mail de l'utilisateur
   *
   * @param email L'adresse e-mail de l'utilisateur à définir
   */
  public void setEmail(String email) { this.email = email; }

  /**
   * Getter pour la date de création du compte utilisateur
   *
   * @return La date de création du compte utilisateur
   */
  public LocalDateTime getDateCreation() { return dateCreation; }

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
  public Profil getProfil() { return profil; }

  /**
   * Setter pour le profil associé à l'utilisateur
   *
   * @param profil Le profil associé à l'utilisateur à définir
   */
  public void setProfil(Profil profil) { this.profil = profil; }

  /**
   * Getter pour le portefeuille associé à l'utilisateur
   *
   * @return Le portefeuille associé à l'utilisateur
   */
  public Portefeuille getPortefeuille() { return portefeuille; }

  /**
   * Setter pour le portefeuille associé à l'utilisateur
   *
   * @param portefeuille Le portefeuille associé à l'utilisateur à définir
   */
  public void setPortefeuille(Portefeuille portefeuille) {
    this.portefeuille = portefeuille;
  }

  /**
   * Getter pour la liste des amitiés envoyées de l'utilisateur
   *
   * @return La liste des amitiés envoyées de l'utilisateur
   */
  public List<Amitie> getAmitiesEnvoyees() { return this.demandesEnvoyees; }

  /**
   * Setter pour la liste des amitiés envoyées de l'utilisateur
   *
   * @param amities La liste des amitiés envoyées de l'utilisateur à définir
   */
  public void setAmitiesEnvoyees(List<Amitie> amities) {
    this.demandesEnvoyees = amities;
  }

  /**
   * Getter pour la liste des amitiés reçues de l'utilisateur
   *
   * @return La liste des amitiés reçues de l'utilisateur
   */
  public List<Amitie> getAmitiesRecues() { return this.demandesRecues; }

  /**
   * Setter pour la liste des amitiés reçues de l'utilisateur
   *
   * @param amities La liste des amitiés reçues de l'utilisateur à définir
   */
  public void setAmitiesRecues(List<Amitie> amities) {
    this.demandesRecues = amities;
  }

  /**
   * Getter pour la liste des sessions de jeu de l'utilisateur
   *
   * @return La liste des sessions de jeu de l'utilisateur
   */
  public List<SessionJoueur> getSessionsJoueur() { return sessionsJoueur; }

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

  /**
   * Récupère les amis de l'utilisateur. On considère qu'un ami est un
   * FriendStatus Accepted
   *
   * @return la liste des amis de l'utilisateur
   */
  public List<Utilisateur> getAmis() {
    List<Utilisateur> amis = new ArrayList<>();

    // On ajoute ceux à qui on a envoyé une demande qui a été acceptée
    for (Amitie a : demandesEnvoyees) {
      if (a.getStatus() == FriendStatus.ACCEPTED) {
        amis.add(a.getReceveur());
      }
    }

    // On ajoute ceux qui nous ont envoyé une demande qu'on a acceptée
    for (Amitie a : demandesRecues) {
      if (a.getStatus() == FriendStatus.ACCEPTED) {
        amis.add(a.getDemandeur());
      }
    }
    return amis;
  }
}
