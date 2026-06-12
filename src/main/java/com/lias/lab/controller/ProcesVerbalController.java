package com.lias.lab.controller;

import com.lias.lab.entity.ProcesVerbal;
import com.lias.lab.repository.MembreRepository;
import com.lias.lab.service.ProcesVerbalService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/pv")
public class ProcesVerbalController {

    private final ProcesVerbalService pvService;
    private final MembreRepository membreRepository;

    public ProcesVerbalController(ProcesVerbalService pvService,
                                  MembreRepository membreRepository) {
        this.pvService = pvService;
        this.membreRepository = membreRepository;
    }

    @GetMapping
    public String listePV(Model model) {
        model.addAttribute("pvList", pvService.findAll());
        return "pv-list";
    }

    @GetMapping("/{id}")
    public String detailPV(@PathVariable Long id, Model model) {
        model.addAttribute("pv", pvService.findById(id));
        return "pv-detail";
    }

    @GetMapping("/nouveau")
    @PreAuthorize("hasRole('DIRECTEUR') or hasRole('VICE_DIRECTEUR')")
    public String formulaireCreation(Model model) {
        model.addAttribute("pv", new ProcesVerbal());
        model.addAttribute("membres", membreRepository.findAll());
        return "pv-form";
    }

    @PostMapping("/nouveau")
    @PreAuthorize("hasRole('DIRECTEUR') or hasRole('VICE_DIRECTEUR')")
    public String creerPV(@ModelAttribute ProcesVerbal pv,
                          @RequestParam(value = "fichier", required = false) MultipartFile fichier) {
        try {
            pvService.save(pv, fichier);
        } catch (Exception e) {
            // handle error
        }
        return "redirect:/pv";
    }

    @GetMapping("/{id}/modifier")
    @PreAuthorize("hasRole('DIRECTEUR') or hasRole('VICE_DIRECTEUR')")
    public String formulaireModification(@PathVariable Long id, Model model) {
        ProcesVerbal pv = pvService.findById(id);
        if (pv == null) return "redirect:/pv";

        model.addAttribute("pv", pv);
        model.addAttribute("membres", membreRepository.findAll());
        return "pv-form";
    }

    @PostMapping("/{id}/modifier")
    @PreAuthorize("hasRole('DIRECTEUR') or hasRole('VICE_DIRECTEUR')")
    public String modifierPV(@PathVariable Long id, @ModelAttribute ProcesVerbal pv,
                             @RequestParam(value = "fichier", required = false) MultipartFile fichier) {
        try {
            pv.setId(id);
            pvService.save(pv, fichier);
        } catch (Exception e) {
            // handle error
        }
        return "redirect:/pv/" + id;
    }

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasRole('DIRECTEUR')")
    public String supprimerPV(@PathVariable Long id) {
        pvService.delete(id);
        return "redirect:/pv";
    }

    @GetMapping("/{id}/telecharger")
    public ResponseEntity<byte[]> telecharger(@PathVariable Long id) {
        try {
            ProcesVerbal pv = pvService.findById(id);
            if (pv == null || pv.getDocumentUrl() == null) {
                return ResponseEntity.notFound().build();
            }
            String nomFichier = pv.getDocumentUrl().substring(pv.getDocumentUrl().lastIndexOf("/") + 1);
            java.nio.file.Path chemin = java.nio.file.Paths.get("uploads/pv").resolve(nomFichier);
            byte[] contenu = java.nio.file.Files.readAllBytes(chemin);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"pv-" + id + ".pdf\"")
                    .body(contenu);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/visualiser")
    public ResponseEntity<byte[]> visualiser(@PathVariable Long id) {
        try {
            ProcesVerbal pv = pvService.findById(id);
            if (pv == null || pv.getDocumentUrl() == null) {
                return ResponseEntity.notFound().build();
            }
            String nomFichier = pv.getDocumentUrl().substring(pv.getDocumentUrl().lastIndexOf("/") + 1);
            java.nio.file.Path chemin = java.nio.file.Paths.get("uploads/pv").resolve(nomFichier);
            byte[] contenu = java.nio.file.Files.readAllBytes(chemin);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(contenu);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}