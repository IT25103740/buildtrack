package com.buildtrack.material.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "materials")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Material {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 120)
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String unit;                     // e.g. bag, kg, m3

    @Min(0)
    @Builder.Default
    @Column(nullable = false)
    private int reorderThreshold = 0;
}
