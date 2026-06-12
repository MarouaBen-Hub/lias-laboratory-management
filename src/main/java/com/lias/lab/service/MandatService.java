package com.lias.lab.service;

import com.lias.lab.entity.Mandat;
import com.lias.lab.entity.Membre;
import com.lias.lab.entity.enums.RoleMembre;
import com.lias.lab.repository.MandatRepository;
import com.lias.lab.repository.MembreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MandatService {

    private final MandatRepository mandatRepository;
    private final MembreRepository membreRepository;

    public MandatService(MandatRepository mandatRepository, MembreRepository membreRepository) {
        this.mandatRepository = mandatRepository;
        this.membreRepository = membreRepository;
    }

    public List<Mandat> findAll() {
        return mandatRepository.findAll();
    }

    public List<Mandat> findActifs() {
        return mandatRepository.findByActifTrueOrderByDateDebutDesc();
    }

    public List<Mandat> findByMembre(Long membreId) {
        return mandatRepository.findByMembreIdOrderByDateDebutDesc(membreId);
    }

    public List<Mandat> findByRole(RoleMembre role) {
        return mandatRepository.findByRoleOrderByDateDebutDesc(role);
    }

    public Mandat findById(Long id) {
        return mandatRepository.findById(id).orElse(null);
    }

    public Mandat nommer(Membre membre, RoleMembre role, LocalDate dateDebut) {
        // Vérifier si un mandat du même rôle est déjà actif
        mandatRepository.findActifByRole(role).ifPresent(ancien -> {
            ancien.setDateFin(dateDebut.minusDays(1));
            ancien.setActif(false);
            mandatRepository.save(ancien);
        });

        // Retirer l'ancien rôle du membre s'il en a un du même type
        List<Mandat> actifsMembre = mandatRepository.findActifsByMembre(membre.getId());
        for (Mandat m : actifsMembre) {
            if (m.getRole() == role) {
                m.setDateFin(dateDebut.minusDays(1));
                m.setActif(false);
                mandatRepository.save(m);
            }
        }

        // Ajouter le nouveau rôle au membre
        membre.getRoles().add(role);
        membreRepository.save(membre);

        Mandat mandat = Mandat.builder()
                .membre(membre)
                .role(role)
                .dateDebut(dateDebut)
                .actif(true)
                .build();

        return mandatRepository.save(mandat);
    }

    public Mandat clore(Long mandatId, LocalDate dateFin) {
        Mandat mandat = findById(mandatId);
        if (mandat == null) return null;

        mandat.setDateFin(dateFin);
        mandat.setActif(false);

        // Retirer le rôle du membre
        Membre membre = mandat.getMembre();
        membre.getRoles().remove(mandat.getRole());
        membreRepository.save(membre);

        return mandatRepository.save(mandat);
    }

    public void delete(Long id) {
        mandatRepository.deleteById(id);
    }

    public boolean isDirecteurActif() {
        return mandatRepository.countActifByRole(RoleMembre.DIRECTEUR) > 0;
    }

    public Membre getDirecteurActif() {
        return mandatRepository.findActifByRole(RoleMembre.DIRECTEUR)
                .map(Mandat::getMembre)
                .orElse(null);
    }
}