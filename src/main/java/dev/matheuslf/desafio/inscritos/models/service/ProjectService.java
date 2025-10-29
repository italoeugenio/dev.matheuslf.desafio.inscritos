package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectException;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectResponseDTO;
import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import dev.matheuslf.desafio.inscritos.models.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    
    public void saveProject(ProjectRequestDTO data){
        ProjectModel projectModel = new ProjectModel(data);
        if(projectModel.getEndDate().isBefore(projectModel.getStartDate())) throw new ProjectException("The end date can't be before start date");
        if(projectModel.getStartDate().isBefore(LocalDateTime.now())) throw new ProjectException("The start date can't be one that has passed");
        projectRepository.save(projectModel);
    }

    public List<ProjectResponseDTO> getAll(){
        List<ProjectResponseDTO> projects = projectRepository.findAll().stream()
                .map(project -> new ProjectResponseDTO(project))
                .toList();
        return projects;
    }
}
