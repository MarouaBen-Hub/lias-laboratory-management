package com.lias.lab.repository;

import com.lias.lab.entity.HistoriqueAffiliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueAffiliationRepository extends JpaRepository<HistoriqueAffiliation, Long> {

    List<HistoriqueAffiliation> findByMembreIdOrderByDateDebutDesc(Long membreId);
}
