package com.lias.lab.repository;

import com.lias.lab.entity.Mandat;
import com.lias.lab.entity.enums.RoleMembre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MandatRepository extends JpaRepository<Mandat, Long> {

    List<Mandat> findByMembreIdOrderByDateDebutDesc(Long membreId);

    List<Mandat> findByRoleOrderByDateDebutDesc(RoleMembre role);

    List<Mandat> findByActifTrueOrderByDateDebutDesc();

    @Query("SELECT m FROM Mandat m WHERE m.actif = true AND m.role = :role")
    Optional<Mandat> findActifByRole(@Param("role") RoleMembre role);

    @Query("SELECT m FROM Mandat m WHERE m.actif = true AND m.membre.id = :membreId")
    List<Mandat> findActifsByMembre(@Param("membreId") Long membreId);

    @Query("SELECT COUNT(m) FROM Mandat m WHERE m.actif = true AND m.role = :role")
    Long countActifByRole(@Param("role") RoleMembre role);

    @Query("SELECT m FROM Mandat m WHERE m.dateFin IS NULL AND m.dateDebut <= CURRENT_DATE")
    List<Mandat> findEnCoursSansDateFin();
}