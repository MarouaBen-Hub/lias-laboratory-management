package com.lias.lab.repository;

import com.lias.lab.entity.Publication; // <-- Corrigé pour pointer sur le bon dossier entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    // Module 12 : Classement et filtrage des publications [cite: 175, 180]

    // 1. Classement par année [cite: 182]
    List<Publication> findByAnnee(Integer annee);

    // 2. Classement par auteur [cite: 181]
    List<Publication> findByAuteursContainingIgnoreCase(String auteur);

    // 3. Classement par équipe (Traverse l'objet Equipe pour trouver son Id) [cite: 183]
    List<Publication> findByEquipe_Id(Long equipeId);
}