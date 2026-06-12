package com.lias.lab.service;

import com.lias.lab.entity.Membre;
import com.lias.lab.entity.Publication;
import com.lias.lab.repository.MembreRepository;
import com.lias.lab.repository.PublicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final MembreRepository membreRepository;

    public PublicationService(PublicationRepository publicationRepository, MembreRepository membreRepository) {
        this.publicationRepository = publicationRepository;
        this.membreRepository = membreRepository;
    }

    public List<Publication> findAll() {
        return publicationRepository.findAll();
    }

    public Publication findById(Long id) {
        return publicationRepository.findById(id).orElse(null);
    }

    public List<Publication> findByMembre(Long membreId) {
        return publicationRepository.findAllByMembreId(membreId);
    }

    public List<Publication> search(String keyword) {
        return publicationRepository.searchByKeyword(keyword);
    }

    public List<Publication> findByAnnee(Integer annee) {
        return publicationRepository.findByAnneeOrderByAnneeDesc(annee);
    }

    public List<Publication> findRecent() {
        return publicationRepository.findTop10ByOrderByDateCreationDesc();
    }

    public Publication save(Publication publication, Long auteurPrincipalId, Set<Long> coAuteursIds) {
        Membre auteurPrincipal = membreRepository.findById(auteurPrincipalId).orElse(null);
        publication.setAuteurPrincipal(auteurPrincipal);

        Set<Membre> auteurs = new HashSet<>();
        if (coAuteursIds != null) {
            for (Long id : coAuteursIds) {
                membreRepository.findById(id).ifPresent(auteurs::add);
            }
        }
        if (auteurPrincipal != null) {
            auteurs.add(auteurPrincipal);
        }
        publication.setAuteurs(auteurs);

        return publicationRepository.save(publication);
    }

    public Publication update(Long id, Publication updated, Long auteurPrincipalId, Set<Long> coAuteursIds) {
        Publication existing = findById(id);
        if (existing == null) return null;

        existing.setTitre(updated.getTitre());
        existing.setResume(updated.getResume());
        existing.setAnnee(updated.getAnnee());
        existing.setType(updated.getType());
        existing.setDoi(updated.getDoi());
        existing.setUrl(updated.getUrl());

        return save(existing, auteurPrincipalId, coAuteursIds);
    }

    public void delete(Long id) {
        publicationRepository.deleteById(id);
    }

    public boolean canEdit(Long publicationId, String email) {
        Publication p = findById(publicationId);
        if (p == null) return false;

        Membre membre = membreRepository.findByEmail(email).orElse(null);
        if (membre == null) return false;

        if (membre.isDirecteur()) return true;

        if (p.getAuteurPrincipal() != null && p.getAuteurPrincipal().getId().equals(membre.getId())) {
            return true;
        }

        return p.getAuteurs().stream().anyMatch(a -> a.getId().equals(membre.getId()));
    }
}