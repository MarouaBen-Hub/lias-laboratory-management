package com.lias.lab.service;

import com.lias.lab.entity.Document;
import com.lias.lab.entity.Membre;
import com.lias.lab.entity.enums.TypeDocument;
import com.lias.lab.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    public Document findById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }

    public List<Document> findByEvenement(Long evenementId) {
        return documentRepository.findByEvenementIdOrderByDateUploadDesc(evenementId);
    }

    public List<Document> findGeneraux() {
        return documentRepository.findDocumentsGeneraux();
    }

    public List<Document> search(String keyword) {
        return documentRepository.searchByKeyword(keyword);
    }

    public List<Document> findByType(TypeDocument type) {
        return documentRepository.findByTypeOrderByDateUploadDesc(type);
    }

    public Document upload(MultipartFile fichier, TypeDocument type, String description,
                           com.lias.lab.entity.Evenement evenement, Membre uploader) throws IOException {

        // Créer le répertoire s'il n'existe pas
        Path repertoire = Paths.get(uploadDir);
        if (!Files.exists(repertoire)) {
            Files.createDirectories(repertoire);
        }

        // Générer un nom unique
        String extension = "";
        String nomOriginal = fichier.getOriginalFilename();
        if (nomOriginal != null && nomOriginal.contains(".")) {
            extension = nomOriginal.substring(nomOriginal.lastIndexOf("."));
        }
        String nomStocke = UUID.randomUUID().toString() + extension;

        // Sauvegarder le fichier
        Path cheminFichier = repertoire.resolve(nomStocke);
        Files.copy(fichier.getInputStream(), cheminFichier, StandardCopyOption.REPLACE_EXISTING);

        // Créer l'entité
        Document document = Document.builder()
                .nomOriginal(nomOriginal)
                .nomStocke(nomStocke)
                .type(type)
                .description(description)
                .tailleOctets(fichier.getSize())
                .contentType(fichier.getContentType())
                .evenement(evenement)
                .uploader(uploader)
                .build();

        return documentRepository.save(document);
    }

    public byte[] telecharger(Long documentId) throws IOException {
        Document document = findById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("Document non trouvé");
        }
        Path chemin = Paths.get(uploadDir).resolve(document.getNomStocke());
        return Files.readAllBytes(chemin);
    }

    public void delete(Long id) throws IOException {
        Document document = findById(id);
        if (document != null) {
            Path chemin = Paths.get(uploadDir).resolve(document.getNomStocke());
            Files.deleteIfExists(chemin);
            documentRepository.delete(document);
        }
    }

    public String getContentType(Long documentId) {
        Document document = findById(documentId);
        return document != null ? document.getContentType() : "application/octet-stream";
    }

    public String getNomOriginal(Long documentId) {
        Document document = findById(documentId);
        return document != null ? document.getNomOriginal() : "document";
    }
}