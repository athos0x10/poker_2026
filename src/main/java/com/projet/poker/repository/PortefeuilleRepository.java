package com.projet.poker.repository;

import com.projet.poker.model.persist.Portefeuille;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortefeuilleRepository extends JpaRepository<Portefeuille, Long> {
}
