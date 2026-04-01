package com.projet.poker.repository;

import com.projet.poker.model.persist.Utilisateur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Configure une base H2 de test automatiquement
public class UtilisateurRepositoryTest {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Test
    public void testSaveAndFindByLogin() {
        // On prépare un utilisateur
        Utilisateur user = new Utilisateur();
        user.setLogin("joueur_pro");
        user.setEmail("pro@poker.fr");
        user.setPasswordHash("secret123");

        // On l'enregistre en base
        utilisateurRepository.save(user);

        //  On vérifie qu'on peut le récupérer
        Optional<Utilisateur> found = utilisateurRepository.findByLogin("joueur_pro");
        
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("pro@poker.fr");
    }
    
    @Test
    public void testExistsByEmail() {
        // GIVEN
        Utilisateur user = new Utilisateur();
        user.setLogin("test_mail");
        user.setEmail("unique@poker.fr");
        user.setPasswordHash("pass");
        utilisateurRepository.save(user);

        // WHEN & THEN
        boolean exists = utilisateurRepository.existsByEmail("unique@poker.fr");
        boolean notExists = utilisateurRepository.existsByEmail("inconnu@poker.fr");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}