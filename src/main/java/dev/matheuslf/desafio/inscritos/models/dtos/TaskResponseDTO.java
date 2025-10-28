package dev.matheuslf.desafio.inscritos.models.dtos;

import dev.matheuslf.desafio.inscritos.enums.TaskPriority;
import dev.matheuslf.desafio.inscritos.enums.TaskStatus;
import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import dev.matheuslf.desafio.inscritos.models.entities.TaskModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponseDTO(
         UUID id,
         String title,
         String description,
         TaskStatus status,
         TaskPriority priority,
         LocalDateTime dueTime,
         ProjectModel project
) {
    public TaskResponseDTO(TaskModel task){
        this(task.getId(),task.getTitle(),task.getDescription(),task.getStatus(),task.getPriority(),task.getDueTime(), task.getProject());
    }
}
