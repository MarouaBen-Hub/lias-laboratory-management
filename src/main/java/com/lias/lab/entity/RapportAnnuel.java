package com.lias.lab.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rapport_annuel")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RapportAnnuel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer annee;

    @Column(length = 4000)
    private String contenu;

    private Integer nbEvenements;
    private Integer nbPublications;
    private Integer nbMembresActifs;
    private Integer nbDemandesAcceptees;
    private Integer nbNouveauxMandats;

    private boolean genere = false;
    private LocalDateTime dateGeneration;

    @ManyToOne
    @JoinColumn(name = "generateur_id")
    private Membre generateur;
}
