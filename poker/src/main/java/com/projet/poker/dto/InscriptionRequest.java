package com.projet.poker.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data // Génère getters/setters avec Lombok
// Permet d'avoir un format sécurisé lors de la demande de création d'un compte.
public class InscriptionRequest {

    // évite les injections du type XSS ?
    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "Le login contient des caractères non autorisés")
    private String login;

    @NotBlank
    @Email
    private String email;

    // Le mdp est hashé avant de traverser le réseau.
    @NotBlank
    private String passwordHash;
}
