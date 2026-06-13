package com.lias.lab.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomOriginal;

    @Column(nullable = false)
    private String nomStocke;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument type;

    private String description;

    private Long tailleOctets;

    private String contentType;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @ManyToOne
    @JoinColumn(name = "uploader_id", nullable = false)
    private Membre uploader;

    private LocalDateTime dateUpload;

    @PrePersist
    public void prePersist() {
        if (dateUpload == null) {
            dateUpload = LocalDateTime.now();
        }
    }
}
