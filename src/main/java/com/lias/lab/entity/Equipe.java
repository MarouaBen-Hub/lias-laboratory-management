package com.lias.lab.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "equipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    private LocalDate dateCreation;

    @ManyToMany(mappedBy = "equipes")
    private Set<Membre> membres = new HashSet<>();
}
