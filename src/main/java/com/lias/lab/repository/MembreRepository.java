package com.lias.lab.repository;

import com.lias.lab.entity.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembreRepository extends JpaRepository<Membre, Long> {
    Optional<Membre> findByEmail(String email);
    boolean existsByEmail(String email);
}