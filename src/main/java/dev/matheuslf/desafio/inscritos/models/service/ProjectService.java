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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        LocalDateTime minDate = project.getTasks().get(0).getDueTime();
        LocalDateTime maxDate = project.getTasks().get(0).getDueTime();
        UUID idMinDate = project.getTasks().get(0).getId();
        UUID idMaxDate = project.getTasks().get(0).getId();
        for (int i = 0; i < project.getTasks().size(); i++) {
            if (project.getTasks().get(i).getDueTime().isBefore(minDate)) {
                minDate = project.getTasks().get(i).getDueTime();
                idMinDate = project.getTasks().get(i).getId();
            }
            if (project.getTasks().get(i).getDueTime().isAfter(maxDate)) {
                maxDate = project.getTasks().get(i).getDueTime();
                idMaxDate = project.getTasks().get(i).getId();
            }
        }
        if (data.startDate().isAfter(minDate) || data.endDate().isBefore(maxDate)) {
            throw new ProjectException(
                    String.format(
                            "Cannot update project dates. Your tasks span from %s to %s. " +
                                    "Please set start date on or before %s and end date on or after %s" +
                                    " (Earliest task id: %s, Latest task id: %s)",
                            minDate, maxDate,
                            minDate, maxDate,
                            idMinDate, idMaxDate
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
