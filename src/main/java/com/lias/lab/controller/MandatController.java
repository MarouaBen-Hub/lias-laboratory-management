package com.lias.lab.controller;

import com.lias.lab.entity.Mandat;
import com.lias.lab.entity.Membre;
import com.lias.lab.entity.enums.RoleMembre;
import com.lias.lab.repository.MembreRepository;
import com.lias.lab.service.MandatService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/mandats")
public class MandatController {

    private final MandatService mandatService;
    private final MembreRepository membreRepository;

    public MandatController(MandatService mandatService, MembreRepository membreRepository) {
        this.mandatService = mandatService;
        this.membreRepository = membreRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String listeMandats(@RequestParam(required = false) RoleMembre role,
                               @RequestParam(required = false) Boolean actifs,
                               Model model) {
        List<Mandat> mandats;
        if (role != null) {
            mandats = mandatService.findByRole(role);
        } else if (Boolean.TRUE.equals(actifs)) {
            mandats = mandatService.findActifs();
        } else {
            mandats = mandatService.findAll();
        }

        model.addAttribute("mandats", mandats);
        model.addAttribute("roles", RoleMembre.values());
        model.addAttribute("roleFiltre", role);
        model.addAttribute("actifsFiltre", actifs);
        model.addAttribute("membres", membreRepository.findAll());
        model.addAttribute("directeurActuel", mandatService.getDirecteurActif());
        return "mandats";
    }

    @GetMapping("/membre/{membreId}")
    public String historiqueMembre(@PathVariable Long membreId, Model model) {
        model.addAttribute("mandats", mandatService.findByMembre(membreId));
        model.addAttribute("membre", membreRepository.findById(membreId).orElse(null));
        return "mandats-membre";
    }

    @PostMapping("/nommer")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String nommerMandat(@RequestParam Long membreId,
                               @RequestParam RoleMembre role,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                               RedirectAttributes redirectAttributes) {
        try {
            Membre membre = membreRepository.findById(membreId)
                    .orElseThrow(() -> new IllegalArgumentException("Membre non trouvé"));

            // Vérifier qu'on ne nomme pas un deuxième directeur sans clore l'ancien
            if (role == RoleMembre.DIRECTEUR && mandatService.isDirecteurActif()) {
                redirectAttributes.addFlashAttribute("warning", "Un directeur est déjà en place. L'ancien mandat sera automatiquement clôturé.");
            }

            mandatService.nommer(membre, role, dateDebut);
            redirectAttributes.addFlashAttribute("success", "Mandat attribué avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/mandats";
    }

    @PostMapping("/{id}/clore")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String cloreMandat(@PathVariable Long id,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                              RedirectAttributes redirectAttributes) {
        try {
            mandatService.clore(id, dateFin);
            redirectAttributes.addFlashAttribute("success", "Mandat clôturé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/mandats";
    }

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String supprimerMandat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mandatService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Mandat supprimé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/mandats";
    }
}