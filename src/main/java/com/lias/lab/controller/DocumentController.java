package com.lias.lab.controller;

import com.lias.lab.entity.Document;
import com.lias.lab.entity.Evenement;
import com.lias.lab.entity.Membre;
import com.lias.lab.entity.enums.TypeDocument;
import com.lias.lab.repository.EvenementRepository;
import com.lias.lab.repository.MembreRepository;
import com.lias.lab.service.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final MembreRepository membreRepository;
    private final EvenementRepository evenementRepository;

    public DocumentController(DocumentService documentService,
                              MembreRepository membreRepository,
                              EvenementRepository evenementRepository) {
        this.documentService = documentService;
        this.membreRepository = membreRepository;
        this.evenementRepository = evenementRepository;
    }

    @GetMapping
    public String listeDocuments(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) TypeDocument type,
            @RequestParam(required = false) Long evenementId,
            Model model) {

        java.util.List<Document> documents;

        if (recherche != null && !recherche.isBlank()) {
            documents = documentService.search(recherche);
        } else if (type != null) {
            documents = documentService.findByType(type);
        } else if (evenementId != null) {
            documents = documentService.findByEvenement(evenementId);
        } else {
            documents = documentService.findGeneraux();
        }

        model.addAttribute("documents", documents);
        model.addAttribute("types", TypeDocument.values());
        model.addAttribute("evenements", evenementRepository.findAll());
        model.addAttribute("recherche", recherche);
        model.addAttribute("typeFiltre", type);
        model.addAttribute("evenementFiltre", evenementId);
        return "documents";
    }

    @GetMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public String formulaireUpload(Model model) {
        model.addAttribute("types", TypeDocument.values());
        model.addAttribute("evenements", evenementRepository.findAll());
        return "document-upload";
    }

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public String uploadDocument(
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam TypeDocument type,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long evenementId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            if (fichier.isEmpty()) {
                throw new IllegalArgumentException("Veuillez sélectionner un fichier");
            }

            Membre uploader = membreRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            Evenement evenement = evenementId != null ?
                    evenementRepository.findById(evenementId).orElse(null) : null;

            documentService.upload(fichier, type, description, evenement, uploader);
            redirectAttributes.addFlashAttribute("success", "Document uploadé avec succès.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/documents";
    }

    @GetMapping("/{id}/telecharger")
    public ResponseEntity<byte[]> telecharger(@PathVariable Long id) {
        try {
            byte[] contenu = documentService.telecharger(id);
            String contentType = documentService.getContentType(id);
            String nomOriginal = documentService.getNomOriginal(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomOriginal + "\"")
                    .body(contenu);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/visualiser")
    public ResponseEntity<byte[]> visualiser(@PathVariable Long id) {
        try {
            byte[] contenu = documentService.telecharger(id);
            String contentType = documentService.getContentType(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(contenu);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasRole('DIRECTEUR') or @documentService.findById(#id).uploader.email == authentication.name")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            documentService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Document supprimé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/documents";
    }
}