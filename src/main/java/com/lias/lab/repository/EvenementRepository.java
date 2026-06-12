package com.lias.lab.repository;

import com.lias.lab.entity.Evenement;
import com.lias.lab.entity.enums.StatutEvenement;
import com.lias.lab.entity.enums.TypeEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    List<Evenement> findByTypeOrderByDateDebutDesc(TypeEvenement type);

    List<Evenement> findByStatutOrderByDateDebutDesc(StatutEvenement statut);

    List<Evenement> findByOrganisateurIdOrderByDateDebutDesc(Long organisateurId);

    @Query("SELECT e FROM Evenement e WHERE e.dateDebut >= :now ORDER BY e.dateDebut ASC")
    List<Evenement> findUpcoming(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Evenement e WHERE e.dateFin < :now ORDER BY e.dateDebut DESC")
    List<Evenement> findPast(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Evenement e WHERE e.titre LIKE %:keyword% OR e.description LIKE %:keyword% OR e.lieu LIKE %:keyword%")
    List<Evenement> searchByKeyword(@Param("keyword") String keyword);

    List<Evenement> findTop5ByOrderByDateCreationDesc();
}