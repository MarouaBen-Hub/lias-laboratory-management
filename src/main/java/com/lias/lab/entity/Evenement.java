package com.lias.lab.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import com.lias.lab.entity.enums.StatutEvenement;
import com.lias.lab.entity.enums.TypeEvenement;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "evenement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvenement type;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private String lieu;

    @ManyToOne
    @JoinColumn(name = "organisateur_id")
    private Membre organisateur;

    @ManyToMany
    @JoinTable(
        name = "evenement_organisateurs",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "organisateur_id")
    )
    private Set<Membre> organisateurs = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private StatutEvenement statut;

    private LocalDate dateCreation;

    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDate.now();
        }
    }
}
