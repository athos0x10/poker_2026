package com.projet.poker.dto;
import java.util.List;

import lombok.Data;

@Data
// Permet de recevoir les données associées aux amitiés.
public class AmitieResponse {

    private String login;
    private List<AmiDetailDTO> amities;
}

