package com.projet.poker.dto;

import lombok.Data;

@Data
// Permet de recevoir les données associées au profil (permet de sécuriser
// l'envoi sans envoyer le mot de passe de l'utilisateur).
public class ProfilResponse {

  private String login;
  private String biographie;
  private String email;
  private String imageUrl;
  private Integer niveau;
  private Integer experiencePoints;

  public String getLogin() { return login; }

  public void setLogin(String login) { this.login = login; }

  public String getBiographie() { return biographie; }

  public void setBiographie(String biographie) { this.biographie = biographie; }

  public String getEmail() { return email; }

  public void setEmail(String email) { this.email = email; }

  public String getImageUrl() { return imageUrl; }

  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

  public Integer getNiveau() { return niveau; }

  public void setNiveau(Integer niveau) { this.niveau = niveau; }

  public Integer getExperiencePoints() { return experiencePoints; }

  public void setExperiencePoints(Integer experiencePoints) {
    this.experiencePoints = experiencePoints;
  }
}
