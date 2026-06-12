package com.lias.lab.service;

import com.lias.lab.entity.*;
import com.lias.lab.entity.enums.StatutDemande;
import com.lias.lab.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RapportAnnuelService {

    private final RapportAnnuelRepository rapportRepository;
    private final EvenementRepository evenementRepository;
    private final PublicationRepository publicationRepository;
    private final MembreRepository membreRepository;
    private final DemandeAdhesionRepository demandeRepository;
    private final MandatRepository mandatRepository;

    public RapportAnnuelService(RapportAnnuelRepository rapportRepository,
                                EvenementRepository evenementRepository,
                                PublicationRepository publicationRepository,
                                MembreRepository membreRepository,
                                DemandeAdhesionRepository demandeRepository,
                                MandatRepository mandatRepository) {
        this.rapportRepository = rapportRepository;
        this.evenementRepository = evenementRepository;
        this.publicationRepository = publicationRepository;
        this.membreRepository = membreRepository;
        this.demandeRepository = demandeRepository;
        this.mandatRepository = mandatRepository;
    }

    public List<RapportAnnuel> findAll() {
        return rapportRepository.findAll();
    }

    public RapportAnnuel findByAnnee(Integer annee) {
        return rapportRepository.findByAnnee(annee).orElse(null);
    }

    public RapportAnnuel generer(Integer annee, Membre generateur) {
        if (rapportRepository.existsByAnnee(annee)) {
            throw new IllegalArgumentException("Rapport " + annee + " existe déjà");
        }

        LocalDate debut = LocalDate.of(annee, 1, 1);
        LocalDate fin = LocalDate.of(annee, 12, 31);

        List<Evenement> evenements = evenementRepository.findAll().stream()
                .filter(e -> e.getDateDebut() != null &&
                        !e.getDateDebut().toLocalDate().isBefore(debut) &&
                        !e.getDateDebut().toLocalDate().isAfter(fin))
                .toList();

        List<Publication> publications = publicationRepository.findAll().stream()
                .filter(p -> p.getAnnee() != null && p.getAnnee().equals(annee))
                .toList();

        long membresActifs = membreRepository.findAll().stream()
                .filter(Membre::isActif)
                .count();

        long demandesAcceptees = demandeRepository.findAll().stream()
                .filter(d -> d.getStatut() == StatutDemande.ACCEPTEE)
                .filter(d -> d.getDateDecision() != null &&
                        !d.getDateDecision().toLocalDate().isBefore(debut) &&
                        !d.getDateDecision().toLocalDate().isAfter(fin))
                .count();

        long nouveauxMandats = mandatRepository.findAll().stream()
                .filter(m -> m.getDateDebut() != null &&
                        !m.getDateDebut().isBefore(debut) &&
                        !m.getDateDebut().isAfter(fin))
                .count();

        StringBuilder sb = new StringBuilder();
        sb.append("RAPPORT D'ACTIVITÉ ").append(annee).append(" - LIAS\n\n");
        sb.append("Événements: ").append(evenements.size()).append("\n");
        sb.append("Publications: ").append(publications.size()).append("\n");
        sb.append("Membres actifs: ").append(membresActifs).append("\n");
        sb.append("Nouvelles adhésions: ").append(demandesAcceptees).append("\n");
        sb.append("Nouveaux mandats: ").append(nouveauxMandats).append("\n");

        RapportAnnuel rapport = RapportAnnuel.builder()
                .annee(annee)
                .contenu(sb.toString())
                .nbEvenements(evenements.size())
                .nbPublications(publications.size())
                .nbMembresActifs((int) membresActifs)
                .nbDemandesAcceptees((int) demandesAcceptees)
                .nbNouveauxMandats((int) nouveauxMandats)
                .genere(true)
                .dateGeneration(LocalDateTime.now())
                .generateur(generateur)
                .build();

        return rapportRepository.save(rapport);
    }

    public void delete(Long id) {
        rapportRepository.deleteById(id);
    }
}