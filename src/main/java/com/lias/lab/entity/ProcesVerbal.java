package com.lias.lab.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProcesVerbal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private LocalDate dateReunion;

    @Column(length = 2000)
    private String ordreDuJour;

    @Column(length = 4000)
    private String resume;

    @Column(length = 4000)
    private String decisions;

    private String documentUrl;

    @ManyToOne
    @JoinColumn(name = "redacteur_id")
    private Membre redacteur;
}
