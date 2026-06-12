package com.lias.lab.controller;

import com.lias.lab.entity.Materiel;
import com.lias.lab.service.MaterielService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/materiel")
public class MaterielController {

    private final MaterielService materielService;

    public MaterielController(MaterielService materielService) {
        this.materielService = materielService;
    }

    @GetMapping
    public String listeMateriel(@RequestParam(required = false) String disponible, Model model) {
        if ("oui".equals(disponible)) {
            model.addAttribute("materiels", materielService.findDisponible());
        } else {
            model.addAttribute("materiels", materielService.findAll());
        }
        model.addAttribute("disponibleFiltre", disponible);
        return "materiel";
    }

    @GetMapping("/{id}")
    public String detailMateriel(@PathVariable Long id, Model model) {
        Materiel materiel = materielService.findById(id);
        if (materiel == null) return "redirect:/materiel";
        model.addAttribute("materiel", materiel);
        return "materiel";
    }

    @GetMapping("/nouveau")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String formulaireCreation(Model model) {
        model.addAttribute("materiel", new Materiel());
        return "materiel-form";
    }

    @PostMapping("/nouveau")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String creerMateriel(@ModelAttribute Materiel materiel, RedirectAttributes redirectAttributes) {
        materielService.save(materiel);
        redirectAttributes.addFlashAttribute("success", "Matériel ajouté.");
        return "redirect:/materiel";
    }

    @GetMapping("/{id}/modifier")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String formulaireModification(@PathVariable Long id, Model model) {
        model.addAttribute("materiel", materielService.findById(id));
        return "materiel-form";
    }

    @PostMapping("/{id}/modifier")
    @PreAuthorize("hasAnyRole('DIRECTEUR', 'VICE_DIRECTEUR')")
    public String modifierMateriel(@PathVariable Long id, @ModelAttribute Materiel materiel,
                                   RedirectAttributes redirectAttributes) {
        materielService.update(id, materiel);
        redirectAttributes.addFlashAttribute("success", "Matériel mis à jour.");
        return "redirect:/materiel";
    }

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        materielService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Matériel supprimé.");
        return "redirect:/materiel";
    }
}