package com.projet.poker.repository;

import com.projet.poker.model.persist.Profil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilRepository extends JpaRepository<Profil, Long> {
}
