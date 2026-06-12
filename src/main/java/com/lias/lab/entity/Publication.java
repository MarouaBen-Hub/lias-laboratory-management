package com.lias.lab.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "publications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "L'auteur principal ou la liste des auteurs est obligatoire")
    private String auteurs;

    @NotNull(message = "L'année de publication est obligatoire")
    private Integer annee;

    private String journalOuConference;

    @ManyToOne
    @JoinColumn(name = "membre_id")
    private Membre creePar;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;
}