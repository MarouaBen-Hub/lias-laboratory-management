package com.lias.lab.service;

import com.lias.lab.entity.Membre;
import com.lias.lab.repository.MembreRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MembreService {

    private final MembreRepository membreRepository;
    private final PasswordEncoder passwordEncoder;

    public MembreService(MembreRepository membreRepository, PasswordEncoder passwordEncoder) {
        this.membreRepository = membreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Membre> findAll() {
        return membreRepository.findAll();
    }

    public List<Membre> findActiveMembers() {
        return membreRepository.findAll().stream()
                .filter(Membre::isActif)
                .toList();
    }

    public Membre findById(Long id) {
        return membreRepository.findById(id).orElse(null);
    }

    public Membre findByEmail(String email) {
        return membreRepository.findByEmail(email).orElse(null);
    }

    public Membre save(Membre membre) {
        return membreRepository.save(membre);
    }

    public Membre update(Long id, Membre updated) {
        Membre existing = findById(id);
        if (existing == null) return null;

        existing.setNom(updated.getNom());
        existing.setPrenom(updated.getPrenom());
        existing.setEmail(updated.getEmail());
        existing.setPhoto(updated.getPhoto());
        existing.setEtablissement(updated.getEtablissement());
        existing.setLaboratoireOrigine(updated.getLaboratoireOrigine());
        existing.setBiographie(updated.getBiographie());
        existing.setCentresInteret(updated.getCentresInteret());
        existing.setDateNaissance(updated.getDateNaissance());
        existing.setDateEmbauche(updated.getDateEmbauche());

        return membreRepository.save(existing);
    }

    public void delete(Long id) {
        membreRepository.deleteById(id);
    }

    public void changerStatut(Long id, boolean actif) {
        Membre membre = findById(id);
        if (membre != null) {
            membre.setActif(actif);
            membreRepository.save(membre);
        }
    }

    public void verrouillerCompte(Long id, boolean verrouille) {
        Membre membre = findById(id);
        if (membre != null) {
            membre.setCompteVerrouille(verrouille);
            membreRepository.save(membre);
        }
    }
}