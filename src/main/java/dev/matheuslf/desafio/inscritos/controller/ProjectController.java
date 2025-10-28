package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.models.dtos.ProjectRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectResponseDTO;
import dev.matheuslf.desafio.inscritos.models.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping("/addProject")
    public void saveProject(@Valid @RequestBody ProjectRequestDTO data){
        projectService.saveProject(data);
        return;
    }

    @GetMapping("/")
    public List<ProjectResponseDTO> getAll(){
        return projectService.getAll();
    }
}
