package com.lias.lab.entity;
import com.lias.lab.entity.enums.StatutMembre;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
@Entity
@Table(name = "membres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
    @Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sécurité & Connexion
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private boolean active; // false pour Ancien membre désactivé, true pour les autres

    // Profil Public / Modifiable
    private String nom;
    private String prenom;
    private String photo;
    private String etablissementOrigine;
    private String laboratoireOrigine;
    @Column(columnDefinition = "TEXT")
    private String biographie;
    private String centresInteret;
    private String googleScholarId;

    // Données Temporelles Confidentielles
    private LocalDate dateNaissance;
    private LocalDate dateEmbauche;
    private LocalDate dateAffiliationLias;

    @Enumerated(EnumType.STRING)
    private StatutMembre statutActuel;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipeActuelle;

    // Historiques
    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL)
    private List<HistoriqueStatut> historiqueStatuts;

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL)
    private List<HistoriqueRole> historiqueRoles;
    public boolean isDirecteur() {
        return this.statutActuel != null && this.statutActuel.name().equalsIgnoreCase("DIRECTEUR");
    }

    public boolean isViceDirecteur() {
        return this.statutActuel != null && this.statutActuel.name().equalsIgnoreCase("VICE_DIRECTEUR");
    }
}
