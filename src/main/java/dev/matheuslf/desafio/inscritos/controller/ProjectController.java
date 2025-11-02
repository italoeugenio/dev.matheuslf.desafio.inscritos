package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.models.dtos.ProjectRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectResponseDetailsDTO;
import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import dev.matheuslf.desafio.inscritos.models.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping("/addProject")
    public ResponseEntity saveProject(@Valid @RequestBody ProjectRequestDTO data){
        ProjectModel project = projectService.saveProject(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/")
    public List<ProjectResponseDTO> getAll(){
        return projectService.getAll();
    }

    @GetMapping("/{id}")
    public ProjectResponseDetailsDTO getByid(@PathVariable("id") UUID id){
        return projectService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteProject(@PathVariable("id") UUID id){
        projectService.deleteProject(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity updateProject(@PathVariable("id") UUID id, @RequestBody ProjectRequestDTO data){
        projectService.updateProject(id,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
