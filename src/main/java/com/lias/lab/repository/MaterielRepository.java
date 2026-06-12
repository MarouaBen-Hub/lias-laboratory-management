package com.lias.lab.repository;

import com.lias.lab.entity.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterielRepository extends JpaRepository<Materiel, Long> {

    List<Materiel> findByCategorieOrderByDateArrivageDesc(String categorie);

    List<Materiel> findByNomContainingIgnoreCaseOrderByNom(String nom);

    @Query("SELECT m FROM Materiel m WHERE m.quantiteDisponible > 0 ORDER BY m.dateArrivage DESC")
    List<Materiel> findDisponible();

    @Query("SELECT m FROM Materiel m WHERE m.quantiteDisponible = 0 ORDER BY m.dateArrivage DESC")
    List<Materiel> findEpuise();
}