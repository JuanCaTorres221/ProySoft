package com.kairo_emocion.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "El color no puede estar vacío")
    private String color;

    @NotNull(message = "La intensidad es obligatoria")
    @Min(1)
    @Max(10)
    private Integer intensity;

    @Column(columnDefinition = "TEXT")
    private String description;

}

