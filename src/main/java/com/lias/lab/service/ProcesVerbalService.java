package com.lias.lab.service;

import com.lias.lab.entity.ProcesVerbal;
import com.lias.lab.repository.ProcesVerbalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProcesVerbalService {

    private final ProcesVerbalRepository pvRepository;

    @Value("uploads/pv")
    private String pvUploadDir;

    public ProcesVerbalService(ProcesVerbalRepository pvRepository) {
        this.pvRepository = pvRepository;
    }

    public List<ProcesVerbal> findAll() {
        return pvRepository.findAll();
    }

    public ProcesVerbal findById(Long id) {
        return pvRepository.findById(id).orElse(null);
    }

    public ProcesVerbal save(ProcesVerbal pv) {
        return pvRepository.save(pv);
    }

    public ProcesVerbal save(ProcesVerbal pv, org.springframework.web.multipart.MultipartFile fichier) throws java.io.IOException {
        if (fichier != null && !fichier.isEmpty()) {
            java.nio.file.Path repertoire = java.nio.file.Paths.get(pvUploadDir);
            if (!java.nio.file.Files.exists(repertoire)) {
                java.nio.file.Files.createDirectories(repertoire);
            }
            String extension = "";
            String nomOriginal = fichier.getOriginalFilename();
            if (nomOriginal != null && nomOriginal.contains(".")) {
                extension = nomOriginal.substring(nomOriginal.lastIndexOf("."));
            }
            String nomStocke = java.util.UUID.randomUUID().toString() + extension;
            java.nio.file.Path cheminFichier = repertoire.resolve(nomStocke);
            java.nio.file.Files.copy(fichier.getInputStream(), cheminFichier, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            pv.setDocumentUrl("/pv/uploads/" + nomStocke);
        }
        return pvRepository.save(pv);
    }

    public void delete(Long id) {
        pvRepository.deleteById(id);
    }
}