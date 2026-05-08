package com.projet.poker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.projet.poker.model.persist.*;
import com.projet.poker.repository.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AmitieServiceTest {

  @Mock private AmitieRepository amitieRepository;
  @Mock private UtilisateurRepository utilisateurRepository;

  @InjectMocks private AmitieService amitieService;

  @Test
  void testEnvoyerDemande_Success() {
    // Given
    Utilisateur demandeur = new Utilisateur();
    demandeur.setId(1L);
    Utilisateur receveur = new Utilisateur();
    receveur.setId(2L);

    when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(demandeur));
    when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(receveur));
    when(amitieRepository.existsByDemandeurAndReceveur(any(), any()))
        .thenReturn(false);

    // When
    amitieService.envoyerDemande(1L, 2L);

    // Then
    verify(amitieRepository, times(1)).save(any(Amitie.class));
  }

  @Test
  void testEnvoyerDemande_SelfAdd_ShouldFail() {
    // Test de la règle : pas d'auto-invitation
    assertThrows(RuntimeException.class,
                 () -> { amitieService.envoyerDemande(1L, 1L); });
  }
}