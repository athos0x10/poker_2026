package com.projet.poker.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.projet.poker.model.persist.*;
import com.projet.poker.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PortefeuilleServiceTest {

    @Mock private PortefeuilleRepository portefeuilleRepository;
    @InjectMocks private PortefeuilleService portefeuilleService;

    @Test
    void testRetirerFonds_SoldeInsuffisant() {
        Portefeuille p = new Portefeuille();
        p.setGlobalBalance(new java.math.BigDecimal("10.00"));
        when(portefeuilleRepository.findById(1L)).thenReturn(Optional.of(p));

        // On vérifie que retirer 50 alors qu'on a 10 jette une exception
        assertThrows(IllegalArgumentException.class, () -> {
            portefeuilleService.retirerFonds(1L, new java.math.BigDecimal("50.00"));
        });
    }
}