package dev.matheuslf.desafio.inscritos.models.dtos;

import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponseDTO (
        UUID project_id,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate) {

    public ProjectResponseDTO(ProjectModel project){
        this(project.getId(),project.getName(),project.getDescription(),project.getStartDate(),project.getEndDate());
    }

}
