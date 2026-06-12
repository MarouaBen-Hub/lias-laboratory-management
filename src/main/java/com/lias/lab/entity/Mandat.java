package com.lias.lab.entity;

import com.lias.lab.entity.enums.RoleMembre;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "mandat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mandat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleMembre role;

    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;

    private boolean actif = true;
}
