package com.lias.lab.service;

import com.lias.lab.entity.Evenement;
import com.lias.lab.entity.Membre;
import com.lias.lab.entity.enums.StatutEvenement;
import com.lias.lab.repository.EvenementRepository;
import com.lias.lab.repository.MembreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final MembreRepository membreRepository;

    public EvenementService(EvenementRepository evenementRepository, MembreRepository membreRepository) {
        this.evenementRepository = evenementRepository;
        this.membreRepository = membreRepository;
    }

    public List<Evenement> findAll() {
        return evenementRepository.findAll();
    }

    public Evenement findById(Long id) {
        return evenementRepository.findById(id).orElse(null);
    }

    public List<Evenement> findUpcoming() {
        return evenementRepository.findUpcoming(LocalDateTime.now());
    }

    public List<Evenement> findPast() {
        return evenementRepository.findPast(LocalDateTime.now());
    }

    public List<Evenement> search(String keyword) {
        return evenementRepository.searchByKeyword(keyword);
    }

    public List<Evenement> findByType(com.lias.lab.entity.enums.TypeEvenement type) {
        return evenementRepository.findByTypeOrderByDateDebutDesc(type);
    }

    public List<Evenement> findRecent() {
        return evenementRepository.findTop5ByOrderByDateCreationDesc();
    }

    public Evenement save(Evenement evenement, Long organisateurPrincipalId, Set<Long> coOrganisateursIds) {
        Membre organisateurPrincipal = membreRepository.findById(organisateurPrincipalId).orElse(null);
        evenement.setOrganisateur(organisateurPrincipal);

        Set<Membre> organisateurs = new HashSet<>();
        if (coOrganisateursIds != null) {
            for (Long id : coOrganisateursIds) {
                membreRepository.findById(id).ifPresent(organisateurs::add);
            }
        }
        if (organisateurPrincipal != null) {
            organisateurs.add(organisateurPrincipal);
        }
        evenement.setOrganisateurs(organisateurs);

        if (evenement.getStatut() == null) {
            evenement.setStatut(StatutEvenement.PLANIFIE);
        }

        return evenementRepository.save(evenement);
    }

    public Evenement update(Long id, Evenement updated, Long organisateurPrincipalId, Set<Long> coOrganisateursIds) {
        Evenement existing = findById(id);
        if (existing == null) return null;

        existing.setTitre(updated.getTitre());
        existing.setDescription(updated.getDescription());
        existing.setType(updated.getType());
        existing.setDateDebut(updated.getDateDebut());
        existing.setDateFin(updated.getDateFin());
        existing.setLieu(updated.getLieu());
        existing.setStatut(updated.getStatut());

        return save(existing, organisateurPrincipalId, coOrganisateursIds);
    }

    public void delete(Long id) {
        evenementRepository.deleteById(id);
    }

    public boolean canEdit(Long evenementId, String email) {
        Evenement e = findById(evenementId);
        if (e == null) return false;

        Membre membre = membreRepository.findByEmail(email).orElse(null);
        if (membre == null) return false;

        if (membre.isDirecteur()) return true;
        if (membre.isViceDirecteur()) return true;

        if (e.getOrganisateur() != null && e.getOrganisateur().getId().equals(membre.getId())) {
            return true;
        }

        return e.getOrganisateurs().stream().anyMatch(o -> o.getId().equals(membre.getId()));
    }
}