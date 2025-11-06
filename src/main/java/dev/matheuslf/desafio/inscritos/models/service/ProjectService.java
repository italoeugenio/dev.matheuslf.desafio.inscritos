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
        ProjectModel projectModel = new ProjectModel(data);
        if (projectModel.getEndDate().isBefore(projectModel.getStartDate()))
            throw new ProjectException("The end date can't be before start date");
        if (projectModel.getStartDate().isBefore(LocalDateTime.now()))
            throw new ProjectException("The start date can't be one that has passed");
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
        if (data.endDate().isBefore(data.startDate()))
            throw new ProjectException("The end date can't be before start date");
        if (data.startDate().isBefore(LocalDateTime.now()))
            throw new ProjectException("The start date can't be one that has passed");

        ProjectModel project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        for (TaskModel tasks : project.getTasks()) {
            if (tasks.getDueTime().isAfter(data.endDate()) || tasks.getDueTime().isBefore(data.startDate())) {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(tasks);
                throw new ProjectException("You are tring to change a the start and end date from a project, but this task date are not betwwen project date");
            }
        }
        BeanUtils.copyProperties(data, project);
        projectRepository.save(project);
    }
}
