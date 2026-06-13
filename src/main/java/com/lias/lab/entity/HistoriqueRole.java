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
@Table(name = "historique_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    private String role; // Ou ton enum RoleGouvernance si disponible

    private LocalDate dateDebut;
    private LocalDate dateFin;
}
