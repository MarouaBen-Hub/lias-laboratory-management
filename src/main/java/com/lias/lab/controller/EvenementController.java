package com.lias.lab.controller;

import com.lias.lab.entity.Evenement;
import com.lias.lab.entity.Membre;
import com.lias.lab.entity.enums.StatutEvenement;
import com.lias.lab.entity.enums.TypeEvenement;
import com.lias.lab.repository.MembreRepository;
import com.lias.lab.service.EvenementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/evenements")
public class EvenementController {

    private final EvenementService evenementService;
    private final MembreRepository membreRepository;

    public EvenementController(EvenementService evenementService, MembreRepository membreRepository) {
        this.evenementService = evenementService;
        this.membreRepository = membreRepository;
    }

    @GetMapping
    public String listeEvenements(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) TypeEvenement type,
            @RequestParam(required = false) String periode,
            Model model) {

        List<Evenement> evenements;

        if (recherche != null && !recherche.isBlank()) {
            evenements = evenementService.search(recherche);
        } else if (type != null) {
            evenements = evenementService.findByType(type);
        } else if ("avenir".equals(periode)) {
            evenements = evenementService.findUpcoming();
        } else if ("passe".equals(periode)) {
            evenements = evenementService.findPast();
        } else {
            evenements = evenementService.findAll();
        }

        model.addAttribute("evenements", evenements);
        model.addAttribute("types", TypeEvenement.values());
        model.addAttribute("recherche", recherche);
        model.addAttribute("typeFiltre", type);
        model.addAttribute("periode", periode);
        return "evenements";
    }

    @GetMapping("/{id}")
    public String detailEvenement(@PathVariable Long id, Model model) {
        model.addAttribute("evenement", evenementService.findById(id));
        model.addAttribute("statuts", StatutEvenement.values());
        return "evenement-detail";
    }

    @GetMapping("/nouveau")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR', 'CHEF_EQUIPE')")
    public String formulaireCreation(Model model) {
        model.addAttribute("evenement", new Evenement());
        model.addAttribute("types", TypeEvenement.values());
        model.addAttribute("statuts", StatutEvenement.values());
        model.addAttribute("membres", membreRepository.findAll());
        return "evenement-form";
    }

    @PostMapping("/nouveau")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR', 'CHEF_EQUIPE')")
    public String creerEvenement(
            @ModelAttribute Evenement evenement,
            @RequestParam Long organisateurPrincipalId,
            @RequestParam(required = false) List<Long> coOrganisateursIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            Authentication authentication) {

        evenement.setDateDebut(dateDebut);
        evenement.setDateFin(dateFin);

        Set<Long> coOrganisateurs = coOrganisateursIds != null ? new HashSet<>(coOrganisateursIds) : new HashSet<>();

        Membre connecte = membreRepository.findByEmail(authentication.getName()).orElse(null);
        if (connecte != null && !connecte.isDirecteur() && !connecte.isViceDirecteur()) {
            organisateurPrincipalId = connecte.getId();
        }

        evenementService.save(evenement, organisateurPrincipalId, coOrganisateurs);
        return "redirect:/evenements";
    }

    @GetMapping("/{id}/modifier")
    @PreAuthorize("isAuthenticated()")
    public String formulaireModification(@PathVariable Long id, Model model) {
        Evenement evenement = evenementService.findById(id);
        if (evenement == null) return "redirect:/evenements";

        model.addAttribute("evenement", evenement);
        model.addAttribute("types", TypeEvenement.values());
        model.addAttribute("statuts", StatutEvenement.values());
        model.addAttribute("membres", membreRepository.findAll());
        model.addAttribute("organisateurPrincipalId", evenement.getOrganisateur() != null ? evenement.getOrganisateur().getId() : null);
        model.addAttribute("coOrganisateursIds", evenement.getOrganisateurs().stream().map(Membre::getId).collect(Collectors.toSet()));

        return "evenement-form";
    }

    @PostMapping("/{id}/modifier")
    @PreAuthorize("@evenementService.canEdit(#id, authentication.name)")
    public String modifierEvenement(
            @PathVariable Long id,
            @ModelAttribute Evenement evenement,
            @RequestParam Long organisateurPrincipalId,
            @RequestParam(required = false) List<Long> coOrganisateursIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        evenement.setDateDebut(dateDebut);
        evenement.setDateFin(dateFin);

        Set<Long> coOrganisateurs = coOrganisateursIds != null ? new HashSet<>(coOrganisateursIds) : new HashSet<>();
        evenementService.update(id, evenement, organisateurPrincipalId, coOrganisateurs);
        return "redirect:/evenements/" + id;
    }

    @PostMapping("/{id}/statut")
    @PreAuthorize("@evenementService.canEdit(#id, authentication.name)")
    public String changerStatut(@PathVariable Long id, @RequestParam StatutEvenement statut) {
        Evenement e = evenementService.findById(id);
        if (e != null) {
            e.setStatut(statut);
            evenementService.update(id, e,
                    e.getOrganisateur() != null ? e.getOrganisateur().getId() : null,
                    e.getOrganisateurs().stream().map(Membre::getId).collect(Collectors.toSet()));
        }
        return "redirect:/evenements/" + id;
    }

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR') or @evenementService.canEdit(#id, authentication.name)")
    public String supprimerEvenement(@PathVariable Long id) {
        evenementService.delete(id);
        return "redirect:/evenements";
    }
}