package com.projet.poker.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data // Génère getters/setters avec Lombok
// Permet de générer un objet pour se connecter à son compte (si il existe)
public class LoginRequest {

    @NotBlank @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "Le login contient des caractères non autorisés")
    private String login;

    @NotBlank
    private String password;
    
}
