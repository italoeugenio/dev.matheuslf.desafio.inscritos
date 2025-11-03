package dev.matheuslf.desafio.inscritos.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ProjectRequestDTO(
       @NotBlank String name,
       @NotBlank String description,
       @NotNull LocalDateTime startDate,
       @NotNull LocalDateTime endDate) {
}
