package com.projet.poker.repository;

import com.projet.poker.model.persist.Amitie;
import com.projet.poker.model.persist.Utilisateur;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmitieRepository extends JpaRepository<Amitie, Long> {

    @Query(
            "SELECT a FROM Amitie a WHERE a.demandeur = :user OR a.receveur = :user")
    List<Amitie>
            findAllByUser(@Param("user") Utilisateur user);

    // La méthode ajoutée pour éviter les doublons
    boolean existsByDemandeurAndReceveur(Utilisateur demandeur,
            Utilisateur receveur);
}
