package com.projet.poker.dto;

import lombok.Data;

@Data
// Permet de recevoir les données associées au profil (permet de sécuriser l'envoi sans envoyer le mot de passe de l'utilisateur).
public class ProfilResponse {

    private String login;
    private String biographie;
    private String email;
    private String imageUrl;
    private Integer niveau;
    private Integer experiencePoints;
}
