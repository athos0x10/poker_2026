package com.projet.poker.repository;

import com.projet.poker.model.persist.Amitie;
import com.projet.poker.model.persist.Utilisateur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import com.projet.poker.model.persist.FriendStatus;

@DataJpaTest // Utilise une base H2 en mémoire pour les tests
public class AmitieRepositoryTest {

    @Autowired
    private AmitieRepository amitieRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    private Utilisateur userA;
    private Utilisateur userB;
    private Utilisateur userC;

    @BeforeEach
    void setUp() {
        // On crée des utilisateurs de test
        userA = createAndSaveUser("UserA", "a@test.com");
        userB = createAndSaveUser("UserB", "b@test.com");
        userC = createAndSaveUser("UserC", "c@test.com");

        // Amitié 1 : UserA est demandeur, UserB est receveur
        Amitie amitie1 = new Amitie();
        amitie1.setDemandeur(userA);
        amitie1.setReceveur(userB);
        amitie1.setStatus(FriendStatus.ACCEPTED);
        amitie1.setDateCreation(LocalDateTime.now());
        amitieRepository.save(amitie1);

        // Amitié 2 : UserC est demandeur, UserA est receveur
        Amitie amitie2 = new Amitie();
        amitie2.setDemandeur(userC);
        amitie2.setReceveur(userA);
        amitie2.setStatus(FriendStatus.PENDING);
        amitie2.setDateCreation(LocalDateTime.now());
        amitieRepository.save(amitie2);
    }

    @Test
    void testFindAllByUser_ShouldReturnBothRoles() {
        // Agir : on cherche toutes les amitiés de UserA
        List<Amitie> result = amitieRepository.findAllByUser(userA);

        // Vérifier
        assertThat(result).hasSize(2);
        
        // On vérifie que les deux relations sont bien présentes
        boolean foundAsDemandeur = result.stream()
                .anyMatch(a -> a.getDemandeur().getLogin().equals("UserA"));
        boolean foundAsReceveur = result.stream()
                .anyMatch(a -> a.getReceveur().getLogin().equals("UserA"));

        assertThat(foundAsDemandeur).isTrue();
        assertThat(foundAsReceveur).isTrue();
    }

    @Test
    void testFindAllByUser_WithNoFriends_ShouldReturnEmpty() {
        Utilisateur lonelyUser = createAndSaveUser("Lonelier", "lonely@test.com");
        List<Amitie> result = amitieRepository.findAllByUser(lonelyUser);
        assertThat(result).isEmpty();
    }

    // Méthode utilitaire pour éviter de répéter le setup des users
    private Utilisateur createAndSaveUser(String login, String email) {
        Utilisateur user = new Utilisateur();
        user.setLogin(login);
        user.setEmail(email);
        user.setPasswordHash("secret");
        user.setDateCreation(LocalDateTime.now());
        return utilisateurRepository.save(user);
    }
}