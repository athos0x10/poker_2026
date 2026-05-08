package com.projet.poker.dto;

import lombok.Data;

@Data
public class AmiDetailDTO {

    private Long amitieId;
    private String amiLogin;
    private String status;
    private boolean isReceiver; // true si c'est le joueur connecté qui a reçu la demande
}
