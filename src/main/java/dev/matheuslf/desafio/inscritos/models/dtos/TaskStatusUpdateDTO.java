package dev.matheuslf.desafio.inscritos.models.dtos;

import dev.matheuslf.desafio.inscritos.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateDTO (
        @NotNull TaskStatus status
){
}
