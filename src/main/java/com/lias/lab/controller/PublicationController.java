package com.lias.lab.controller;

import com.lias.lab.entity.Publication;
import com.lias.lab.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/publications")
public class PublicationController {

    @Autowired
    private PublicationRepository publicationRepository;

    // Module 12 : Consultation globale et classement/filtrage [cite: 1195, 1196]
    @GetMapping
    public String listerPublications(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) String auteur,
            @RequestParam(required = false) Long equipeId,
            Model model) {

        List<Publication> publications;

        // Filtrage dynamique selon les critères du cahier des charges [cite: 1196, 1197, 1198, 1199]
        if (annee != null) {
            publications = publicationRepository.findByAnnee(annee); // Classement par Année [cite: 1198]
        } else if (auteur != null && !auteur.trim().isEmpty()) {
            publications = publicationRepository.findByAuteursContainingIgnoreCase(auteur); // Classement par Auteur [cite: 1197]
        } else if (equipeId != null) {
            publications = publicationRepository.findByEquipe_Id(equipeId); // Classement par Équipe [cite: 1199]
        } else {
            publications = publicationRepository.findAll(); // Consultation globale par défaut [cite: 1195]
        }

        model.addAttribute("publications", publications);
        return "publications/liste"; // Redirige vers templates/publications/liste.html
    }
}