package com.lias.lab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "historique_statuts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueStatut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @Enumerated(EnumType.STRING)
    private com.lias.lab.entity.enums.StatutMembre statut;

    private LocalDate dateDebut;
    private LocalDate dateFin;
}