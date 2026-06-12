package com.lias.lab.controller;

import com.lias.lab.entity.Membre;
import com.lias.lab.repository.MembreRepository;
import com.lias.lab.service.MembreService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MembreController {

    private final MembreRepository membreRepo;
    private final MembreService membreService;

    public MembreController(MembreRepository membreRepo, MembreService membreService) {
        this.membreRepo = membreRepo;
        this.membreService = membreService;
    }

    @GetMapping("/membres")
    public String listeMembres(Model model) {
        model.addAttribute("membres", membreRepo.findAll());
        return "membres";
    }

    @GetMapping("/profil")
    @PreAuthorize("isAuthenticated()")
    public String monProfil(Authentication authentication) {
        Membre connecte = membreRepo.findByEmail(authentication.getName()).orElse(null);
        if (connecte == null) return "redirect:/login";
        return "redirect:/membres/" + connecte.getId();
    }

    @GetMapping("/membres/{id}")
    public String profilMembre(@PathVariable Long id, Model model) {
        model.addAttribute("membre", membreRepo.findById(id).orElse(null));
        return "profil";
    }

    @GetMapping("/membres/{id}/modifier")
    @PreAuthorize("isAuthenticated() and (@securityService.isCurrentUser(#id) or hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR'))")
    public String modifierProfil(@PathVariable Long id, Model model) {
        model.addAttribute("membre", membreRepo.findById(id).orElse(null));
        return "modifier-profil";
    }

    @PostMapping("/membres/{id}/modifier")
    @PreAuthorize("isAuthenticated() and (@securityService.isCurrentUser(#id) or hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR'))")
    public String enregistrerModification(@PathVariable Long id, @ModelAttribute Membre updatedMembre, RedirectAttributes redirectAttributes) {
        membreService.update(id, updatedMembre);
        redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès.");
        return "redirect:/membres/" + id;
    }
}