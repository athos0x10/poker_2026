package com.projet.poker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projet.poker.model.persist.Amitie;
import com.projet.poker.model.persist.Utilisateur;

@Repository
public interface AmitieRepository extends JpaRepository<Amitie, Long> {

  /**
   * findByUser
   * Permet de récupérer les amis de l'utilisateur user
   *
   * @param user l'utilisateur dont on cherche les amis
   */

  List<Utilisateur> findByUser(Utilisateur user);
}
