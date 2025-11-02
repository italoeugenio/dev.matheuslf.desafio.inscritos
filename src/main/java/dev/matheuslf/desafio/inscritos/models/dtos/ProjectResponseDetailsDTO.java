package dev.matheuslf.desafio.inscritos.models.dtos;

import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProjectResponseDetailsDTO(
        UUID project_id,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<TaskResponseDTO> task
) {
    public ProjectResponseDetailsDTO(ProjectModel project){
        this(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getTasks().stream().map(task -> new TaskResponseDTO(task)).toList());
    }
}
