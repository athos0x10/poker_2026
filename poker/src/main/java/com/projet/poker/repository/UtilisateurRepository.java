package com.projet.poker.repository;

import com.projet.poker.model.persist.Utilisateur;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepository
        extends JpaRepository<Utilisateur, Long> {

    /**
     * Permet de récupérer un utilisateur de la bdd à partir de son login.
     * Utilisé pour l'authentification.
     *
     * @param login le login de l'utilisateur.
     * @return un Optional contenant l'utilisateur s'il existe.
     */
    Optional<Utilisateur> findByLogin(String login);

    /**
     * Permet de vérifier si un email est déjà utilisé. Utile pour valider une
     * nouvelle inscription.
     *
     * @param email l'adresse e-mail à tester.
     * @return true si l'email existe déjà.
     */
    boolean existsByEmail(String email);

    /**
     * Permet de vérifier si un login est déjà pris.
     *
     * @param login le login à tester.
     * @return true si le login existe déjà.
     */
    boolean existsByLogin(String login);
}
