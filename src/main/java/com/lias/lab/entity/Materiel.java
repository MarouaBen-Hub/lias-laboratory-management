package com.lias.lab.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "materiel")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Materiel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(length = 2000)
    private String description;

    private String categorie;
    private Integer quantiteTotale;
    private Integer quantiteDisponible;
    private String fournisseur;
    private LocalDate dateArrivage;
    private String numeroSerie;
    private LocalDate dateCreation;

    @PrePersist
    public void prePersist() {
        if (dateCreation == null) dateCreation = LocalDate.now();
        if (quantiteDisponible == null && quantiteTotale != null) quantiteDisponible = quantiteTotale;
    }
}
