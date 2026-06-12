package com.lias.lab.entity;

import com.lias.lab.entity.enums.StatutDemande;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande_adhesion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeAdhesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String email;

    private String cv;

    @Column(length = 2000)
    private String lettreMotivation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    private LocalDateTime dateSoumission;

    private LocalDateTime dateDecision;

    @ManyToOne
    @JoinColumn(name = "decideur_id")
    private Membre decideur;

    private Long membreCreeId;

    @PrePersist
    public void prePersist() {
        if (dateSoumission == null) {
            dateSoumission = LocalDateTime.now();
        }
    }
}
