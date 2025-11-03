package dev.matheuslf.desafio.inscritos.models.dtos;

import dev.matheuslf.desafio.inscritos.enums.TaskPriority;
import dev.matheuslf.desafio.inscritos.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskRequestDTO(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull TaskStatus status,
        @NotNull TaskPriority priority,
        @NotNull LocalDateTime dueDate,
        @NotNull UUID projectId
        ) {
}
