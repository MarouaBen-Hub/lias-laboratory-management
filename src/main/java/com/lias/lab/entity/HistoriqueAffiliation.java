package com.lias.lab.entity;

import com.lias.lab.entity.enums.StatutMembre;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "historique_affiliation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueAffiliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @Enumerated(EnumType.STRING)
    private StatutMembre statut;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private String raison;
}
