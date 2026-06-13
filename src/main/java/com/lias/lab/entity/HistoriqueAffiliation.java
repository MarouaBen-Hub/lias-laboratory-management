package com.lias.lab.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import com.lias.lab.entity.enums.StatutMembre;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
