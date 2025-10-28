package dev.matheuslf.desafio.inscritos.models.dtos;

import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponseDTO (
        UUID id,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate) {

    public ProjectResponseDTO(ProjectModel project){
        this(project.getId(),project.getName(),project.getDescription(),project.getStartDate(),project.getStartDate());
    }

}
