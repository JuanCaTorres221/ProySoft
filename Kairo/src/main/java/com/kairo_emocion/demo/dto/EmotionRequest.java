package com.kairo_emocion.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El color es obligatorio")
    private String color;

    @NotNull(message = "La intensidad es obligatoria")
    @Min(value = 1, message = "La intensidad debe ser al menos 1")
    @Max(value = 10, message = "La intensidad debe ser máximo 5")
    private Integer intensity;

    private String description;
}