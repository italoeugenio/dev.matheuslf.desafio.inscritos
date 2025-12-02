package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectException;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectNotFoundException;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectResponseDetailsDTO;
import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import dev.matheuslf.desafio.inscritos.models.entities.TaskModel;
import dev.matheuslf.desafio.inscritos.models.repository.ProjectRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public ProjectModel saveProject(ProjectRequestDTO data) {
        validateProjectDates(data);
        ProjectModel projectModel = new ProjectModel(data);
        return projectRepository.save(projectModel);
    }

    public Page<ProjectResponseDTO> getAll(Pageable pageable) {
        Page<ProjectModel> projectModels = projectRepository.findAll(pageable);
        return projectModels.map(project -> new ProjectResponseDTO(project));
    }

    public ProjectResponseDetailsDTO getById(UUID id) {
        var projectModel = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        ProjectResponseDetailsDTO project = new ProjectResponseDetailsDTO(projectModel);
        return project;
    }

    public void deleteProject(UUID id) {
        ProjectModel projectModel = projectRepository.findById(id).
                orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        projectRepository.deleteById(id);
    }

    @Transactional
    public void updateProject(UUID id, ProjectRequestDTO data) {
        validateProjectDates(data);
        ProjectModel project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        if (project.getTasks().isEmpty()) {
            BeanUtils.copyProperties(data, project);
            projectRepository.save(project);
            return;
        }

        TaskModel minTaskByDueTime = project.getTasks().stream()
                .min(Comparator.comparing(task -> task.getDueTime()))
                .orElse(null);

        TaskModel maxTaskByDueTime = project.getTasks().stream()
                .max(Comparator.comparing(task -> task.getDueTime()))
                .orElse(null);


        if (data.startDate().isAfter(minTaskByDueTime.getDueTime()) || data.endDate().isBefore(maxTaskByDueTime.getDueTime())) {
            throw new ProjectException(
                    String.format(
                            "Cannot update project dates. Your tasks span from %s to %s. " +
                                    "Please set start date on or before %s and end date on or after %s" +
                                    " (Earliest task id: %s, Latest task id: %s)",
                            minTaskByDueTime.getDueTime(), maxTaskByDueTime.getDueTime(),
                            minTaskByDueTime.getDueTime(), maxTaskByDueTime.getDueTime(),
                            minTaskByDueTime.getId(), maxTaskByDueTime.getId()
                    ));
        }
        BeanUtils.copyProperties(data, project);
        projectRepository.save(project);
    }

    public void validateProjectDates(ProjectRequestDTO data) {
        if (data.endDate().isBefore(data.startDate()))
            throw new ProjectException("The end date can't be before start date");
        if (data.startDate().isBefore(LocalDateTime.now()))
            throw new ProjectException("The start date can't be one that has passed");
    }
}
