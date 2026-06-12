package com.lias.lab.controller;

import com.lias.lab.entity.Membre;
import com.lias.lab.entity.RapportAnnuel;
import com.lias.lab.repository.MembreRepository;
import com.lias.lab.service.RapportAnnuelService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;

@Controller
@RequestMapping("/rapports")
public class RapportAnnuelController {

    private final RapportAnnuelService rapportService;
    private final MembreRepository membreRepository;

    public RapportAnnuelController(RapportAnnuelService rapportService, MembreRepository membreRepository) {
        this.rapportService = rapportService;
        this.membreRepository = membreRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String listeRapports(Model model) {
        model.addAttribute("rapports", rapportService.findAll());
        model.addAttribute("anneeCourante", Year.now().getValue());
        return "rapports";
    }

    @GetMapping("/{annee}")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String voirRapport(@PathVariable Integer annee, Model model) {
        RapportAnnuel rapport = rapportService.findByAnnee(annee);
        if (rapport == null) return "redirect:/rapports";
        model.addAttribute("rapport", rapport);
        return "rapport-detail";
    }

    @PostMapping("/generer")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String genererRapport(@RequestParam Integer annee,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            Membre generateur = membreRepository.findByEmail(authentication.getName()).orElse(null);
            rapportService.generer(annee, generateur);
            redirectAttributes.addFlashAttribute("success", "Rapport " + annee + " généré avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rapports";
    }

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rapportService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Rapport supprimé.");
        return "redirect:/rapports";
    }
}