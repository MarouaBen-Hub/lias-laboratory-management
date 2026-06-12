package com.lias.lab.service;

import com.lias.lab.entity.Materiel;
import com.lias.lab.repository.MaterielRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MaterielService {

    private final MaterielRepository materielRepository;

    public MaterielService(MaterielRepository materielRepository) {
        this.materielRepository = materielRepository;
    }

    public List<Materiel> findAll() {
        return materielRepository.findAll();
    }

    public List<Materiel> findDisponible() {
        return materielRepository.findDisponible();
    }

    public Materiel findById(Long id) {
        return materielRepository.findById(id).orElse(null);
    }

    public Materiel save(Materiel materiel) {
        return materielRepository.save(materiel);
    }

    public Materiel update(Long id, Materiel updated) {
        Materiel existing = findById(id);
        if (existing == null) return null;

        existing.setNom(updated.getNom());
        existing.setDescription(updated.getDescription());
        existing.setCategorie(updated.getCategorie());
        existing.setFournisseur(updated.getFournisseur());
        existing.setNumeroSerie(updated.getNumeroSerie());
        existing.setQuantiteTotale(updated.getQuantiteTotale());
        existing.setQuantiteDisponible(updated.getQuantiteDisponible());

        return materielRepository.save(existing);
    }

    public void delete(Long id) {
        materielRepository.deleteById(id);
    }
}