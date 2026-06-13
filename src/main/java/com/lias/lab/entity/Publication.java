package com.lias.lab.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import com.lias.lab.entity.enums.TypePublication;

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

    private Integer annee;
    
    @Enumerated(EnumType.STRING)
    private TypePublication type;

    // Tous les autres attributs...
    
    // ❌ NE METTEZ PAS de getters/setters!
    // Lombok les génère automatiquement
}
