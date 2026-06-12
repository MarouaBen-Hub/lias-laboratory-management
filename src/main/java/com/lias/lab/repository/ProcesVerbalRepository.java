package com.lias.lab.repository;

import com.lias.lab.entity.ProcesVerbal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcesVerbalRepository extends JpaRepository<ProcesVerbal, Long> {

    List<ProcesVerbal> findByRedacteurId(Long redacteurId);

    List<ProcesVerbal> findByTitreContainingIgnoreCase(String titre);
}