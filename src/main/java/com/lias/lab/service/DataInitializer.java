package com.lias.lab.service;

import com.lias.lab.entity.Membre;
import com.lias.lab.entity.Publication;
import com.lias.lab.entity.enums.RoleMembre;
import com.lias.lab.entity.enums.StatutMembre;
import com.lias.lab.repository.MembreRepository;
import com.lias.lab.repository.PublicationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MembreRepository membreRepository;
    private final PublicationRepository publicationRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleScholarService googleScholarService;

    public DataInitializer(MembreRepository membreRepository,
                           PublicationRepository publicationRepository,
                           PasswordEncoder passwordEncoder,
                           GoogleScholarService googleScholarService) {
        this.membreRepository = membreRepository;
        this.publicationRepository = publicationRepository;
        this.passwordEncoder = passwordEncoder;
        this.googleScholarService = googleScholarService;
    }

    @Override
    public void run(String... args) {
        if (membreRepository.count() > 0) return;

        // Créer les membres
        Membre directrice = createMembre("Benabbou", "Faouzia", "faouzia.benabbou@lias.ma",
                RoleMembre.DIRECTEUR, StatutMembre.PERMANENT, 2015);
        Membre viceDirecteur = createMembre("Belangour", "Abdessamad", "abdessamad.belangour@lias.ma",
                RoleMembre.VICE_DIRECTEUR, StatutMembre.PERMANENT, 2016);
        Membre chefEquipe = createMembre("Sael", "Nawal", "nawal.sael@lias.ma",
                RoleMembre.CHEF_EQUIPE, StatutMembre.PERMANENT, 2017);
        Membre doctorant = createMembre("Alaoui", "Karim", "doctorant1@lias.ma",
                RoleMembre.DOCTORANT, StatutMembre.DOCTORANT, 2024);

        List<Membre> savedMembres = membreRepository.saveAll(List.of(directrice, viceDirecteur, chefEquipe, doctorant));

        // Importer automatiquement les publications depuis Google Scholar
        System.out.println("Importation des publications depuis Google Scholar...");
        for (Membre membre : savedMembres) {
            try {
                List<Publication> pubs = googleScholarService.rechercherPublicationsMembre(membre.getId());
                System.out.println("✅ " + pubs.size() + " publications importées pour " + membre.getNomComplet());
            } catch (Exception e) {
                System.err.println("❌ Erreur import pour " + membre.getNomComplet() + ": " + e.getMessage());
            }
        }
        System.out.println("Importation terminée !");
    }

    private Membre createMembre(String nom, String prenom, String email,
                                RoleMembre role, StatutMembre statut, int anneeAffiliation) {
        return Membre.builder()
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .password(passwordEncoder.encode("password"))
                .statut(statut)
                .roles(Set.of(role))
                .etablissement("Faculté des Sciences Ben M'Sik")
                .dateAffiliation(LocalDate.of(anneeAffiliation, 1, 1))
                .actif(true)
                .compteVerrouille(false)
                .build();
    }
}