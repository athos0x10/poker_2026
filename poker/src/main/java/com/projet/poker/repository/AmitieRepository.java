package com.projet.poker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projet.poker.model.persist.Amitie;
import com.projet.poker.model.persist.Utilisateur;

@Repository
public interface AmitieRepository extends JpaRepository<Amitie, Long> {

  /**
     * Récupère la liste des utilisateurs qui sont amis avec l'utilisateur 'u'
     * (L'amitié doit être au statut ACCEPTED)
     */
    @Query("SELECT CASE " +
           "  WHEN a.user_demandeur = :u THEN a.user_recepteur " +
           "  ELSE a.user_demandeur END " +
           "FROM Amitie a " +
           "WHERE (a.user_demandeur = :u OR a.user_recepteur = :u) " +
           "AND a.status = com.projet.poker.model.persist.FriendStatus.ACCEPTED")
    List<Utilisateur> findAllFriendsForUser(@Param("u") Utilisateur u);
}
