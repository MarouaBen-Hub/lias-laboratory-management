package com.lias.lab.repository;

import com.lias.lab.entity.RapportAnnuel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RapportAnnuelRepository extends JpaRepository<RapportAnnuel, Long> {
    Optional<RapportAnnuel> findByAnnee(Integer annee);
    boolean existsByAnnee(Integer annee);
}