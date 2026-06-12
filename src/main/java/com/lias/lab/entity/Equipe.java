package com.lias.lab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
