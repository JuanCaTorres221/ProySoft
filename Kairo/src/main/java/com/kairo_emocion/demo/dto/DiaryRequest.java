package com.kairo_emocion.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryRequest {

    @NotNull(message = "El usuario es requerido")
    private Long userId;

    @NotNull(message = "La emoción es requerida")
    private Long emotionId;

    private String notes;

    @NotNull(message = "La fecha es requerida")
    private LocalDate entryDate;
}