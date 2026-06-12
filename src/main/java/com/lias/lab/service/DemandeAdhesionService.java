package com.lias.lab.service;

import com.lias.lab.entity.DemandeAdhesion;
import com.lias.lab.entity.Membre;
import com.lias.lab.entity.enums.RoleMembre;
import com.lias.lab.entity.enums.StatutDemande;
import com.lias.lab.entity.enums.StatutMembre;
import com.lias.lab.repository.DemandeAdhesionRepository;
import com.lias.lab.repository.MembreRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class DemandeAdhesionService {

    private final DemandeAdhesionRepository demandeRepository;
    private final MembreRepository membreRepository;
    private final PasswordEncoder passwordEncoder;

    public DemandeAdhesionService(DemandeAdhesionRepository demandeRepository,
                                  MembreRepository membreRepository,
                                  PasswordEncoder passwordEncoder) {
        this.demandeRepository = demandeRepository;
        this.membreRepository = membreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<DemandeAdhesion> findAll() {
        return demandeRepository.findAll();
    }

    public List<DemandeAdhesion> findEnAttente() {
        return demandeRepository.findEnAttente();
    }

    public List<DemandeAdhesion> findByEmail(String email) {
        return demandeRepository.findByEmailOrderByDateSoumissionDesc(email);
    }

    public DemandeAdhesion findById(Long id) {
        return demandeRepository.findById(id).orElse(null);
    }

    public Long countEnAttente() {
        return demandeRepository.countEnAttente();
    }

    public DemandeAdhesion soumettre(DemandeAdhesion demande) {
        if (membreRepository.existsByEmail(demande.getEmail())) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email");
        }
        if (demandeRepository.existsByEmailAndStatut(demande.getEmail(), StatutDemande.EN_ATTENTE)) {
            throw new IllegalArgumentException("Une demande est déjà en cours pour cet email");
        }
        demande.setStatut(StatutDemande.EN_ATTENTE);
        return demandeRepository.save(demande);
    }

    public DemandeAdhesion decider(Long demandeId, boolean accepter, Membre decideur) {
        DemandeAdhesion demande = findById(demandeId);
        if (demande == null) {
            throw new IllegalArgumentException("Demande non trouvée");
        }
        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalArgumentException("Cette demande a déjà été traitée");
        }

        demande.setDateDecision(LocalDateTime.now());
        demande.setDecideur(decideur);

        if (accepter) {
            demande.setStatut(StatutDemande.ACCEPTEE);
            Membre nouveauMembre = creerCompteMembre(demande);
            demande.setMembreCreeId(nouveauMembre.getId());
        } else {
            demande.setStatut(StatutDemande.REFUSEE);
        }

        return demandeRepository.save(demande);
    }
    private Membre creerCompteMembre(DemandeAdhesion demande) {
        String passwordTemporaire = genererMotDePassTemporaire();

        // Instanciation classique
        Membre membre = new Membre();
        membre.setNom(demande.getNom());
        membre.setPrenom(demande.getPrenom());
        membre.setEmail(demande.getEmail());
        membre.setPassword(passwordEncoder.encode(passwordTemporaire));

        // Configuration des statuts par défaut
        membre.setStatutActuel(com.lias.lab.entity.enums.StatutMembre.ASSOCIE);
        membre.setActif(true);
        membre.setCompteVerrouille(false);

        return membreRepository.save(membre);
    }

    private String genererMotDePasseTemporaire() {
        return "Temp" + System.currentTimeMillis() % 10000;
    }

    public void delete(Long id) {
        demandeRepository.deleteById(id);
    }
}