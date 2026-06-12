package com.lias.lab.repository;

import com.lias.lab.entity.DemandeAdhesion;
import com.lias.lab.entity.enums.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeAdhesionRepository extends JpaRepository<DemandeAdhesion, Long> {

    List<DemandeAdhesion> findByStatutOrderByDateSoumissionDesc(StatutDemande statut);

    List<DemandeAdhesion> findByEmailOrderByDateSoumissionDesc(String email);

    @Query("SELECT d FROM DemandeAdhesion d WHERE d.statut = 'EN_ATTENTE' ORDER BY d.dateSoumission ASC")
    List<DemandeAdhesion> findEnAttente();

    @Query("SELECT COUNT(d) FROM DemandeAdhesion d WHERE d.statut = 'EN_ATTENTE'")
    Long countEnAttente();

    boolean existsByEmailAndStatut(String email, StatutDemande statut);
}