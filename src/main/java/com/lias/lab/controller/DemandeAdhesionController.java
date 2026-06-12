package com.lias.lab.controller;

import com.lias.lab.entity.DemandeAdhesion;
import com.lias.lab.entity.Membre;
import com.lias.lab.entity.enums.StatutDemande;
import com.lias.lab.repository.MembreRepository;
import com.lias.lab.service.DemandeAdhesionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/demandes")
public class DemandeAdhesionController {

    private final DemandeAdhesionService demandeService;
    private final MembreRepository membreRepository;

    public DemandeAdhesionController(DemandeAdhesionService demandeService, MembreRepository membreRepository) {
        this.demandeService = demandeService;
        this.membreRepository = membreRepository;
    }

    // ========== PUBLIC : Soumission ==========

    @GetMapping("/adherer")
    public String formulaireAdhesion(Model model) {
        model.addAttribute("demande", new DemandeAdhesion());
        return "demande-form";
    }

    @PostMapping("/adherer")
    public String soumettreAdhesion(@ModelAttribute DemandeAdhesion demande,
                                    RedirectAttributes redirectAttributes) {
        try {
            demandeService.soumettre(demande);
            redirectAttributes.addFlashAttribute("success", "Votre demande a été soumise avec succès. Vous serez notifié par email.");
            return "redirect:/demandes/adherer";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("demande", demande);
            return "redirect:/demandes/adherer";
        }
    }

    // ========== DIRECTEUR/VICE-DIRECTEUR : Gestion ==========

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String listeDemandes(@RequestParam(required = false) StatutDemande statut, Model model) {
        if (statut != null) {
            model.addAttribute("demandes", demandeService.findAll().stream()
                    .filter(d -> d.getStatut() == statut).toList());
        } else {
            model.addAttribute("demandes", demandeService.findEnAttente());
        }
        model.addAttribute("statuts", StatutDemande.values());
        model.addAttribute("statutFiltre", statut);
        model.addAttribute("nbEnAttente", demandeService.countEnAttente());
        return "demandes";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String detailDemande(@PathVariable Long id, Model model) {
        model.addAttribute("demande", demandeService.findById(id));
        return "demande-detail";
    }

    @PostMapping("/{id}/accepter")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String accepterDemande(@PathVariable Long id,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            Membre decideur = membreRepository.findByEmail(authentication.getName()).orElse(null);
            demandeService.decider(id, true, decideur);
            redirectAttributes.addFlashAttribute("success", "Demande acceptée. Le compte membre a été créé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/demandes";
    }

    @PostMapping("/{id}/refuser")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String refuserDemande(@PathVariable Long id,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            Membre decideur = membreRepository.findByEmail(authentication.getName()).orElse(null);
            demandeService.decider(id, false, decideur);
            redirectAttributes.addFlashAttribute("success", "Demande refusée.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/demandes";
    }

    // ========== SUIVI PUBLIC ==========

    @GetMapping("/suivi")
    public String suiviDemandes(@RequestParam String email, Model model) {
        model.addAttribute("demandes", demandeService.findByEmail(email));
        model.addAttribute("emailRecherche", email);
        return "mes-demandes";
    }
}