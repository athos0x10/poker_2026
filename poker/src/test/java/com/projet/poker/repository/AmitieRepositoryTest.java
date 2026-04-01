package com.projet.poker.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.projet.poker.model.persist.Amitie;
import com.projet.poker.model.persist.FriendStatus;
import com.projet.poker.model.persist.Utilisateur;
import com.projet.poker.model.persist.UtilisateurRepository; // Tu en auras besoin
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class AmitieRepositoryTest {
    
    @Autowired
    private AmitieRepository amitieRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Test
    public void testSaveAndFindFriends() {
        LocalDateTime a = new LocalDateTime();
        // creation utilisateurs 
        Utilisateur u1 = new Utilisateur("Alice", "alice", "alice@poker.com", a.now());
        Utilisateur u2 = new Utilisateur("Bob", "bob", "bob@poker.com", a.now());

        utilisateurRepository.save(u1);
        utilisateurRepository.save(u2);

        Amitie amitie = new Amitie(u1, u2, FriendStatus.ACCEPTED, LocalDateTime.now());

        // 2. ACTION (Sauvegarder en base)
        amitieRepository.save(amitie);

        // 3. ASSERTION (Vérifier que ça a marché)
        // On utilise la requête que nous avons définie précédemment
        List<Utilisateur> amisDeAlice = amitieRepository.findAllFriendsForUser(u1);

        assertThat(amisDeAlice).hasSize(1);
        assertThat(amisDeAlice.get(0).getNom()).isEqualTo("Bob");
    }

}