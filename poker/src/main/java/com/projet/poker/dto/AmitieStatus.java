package com.projet.poker.dto;

import com.projet.poker.model.persist.FriendStatus;

import lombok.Data;

// Permet de recevoir les données associées aux amitiés.
@Data
class AmitieStatus {
    private String amiLogin;
    private FriendStatus status;
    private Long amitieId;
}

