package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.config.swagger.docs.project.ProjectApiDoc;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectException;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectExceptionDetails;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectNotFoundException;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectResponseDetailsDTO;
import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import dev.matheuslf.desafio.inscritos.models.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("projects")
@Tag(name = "Projects", description = "Endpoints to manage the projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @ProjectApiDoc.CreateProject
    @PostMapping("/addProject")
    public ResponseEntity<Void> saveProject(@Valid @RequestBody ProjectRequestDTO data) {
        ProjectModel project = projectService.saveProject(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ProjectApiDoc.GetAllProjects
    @GetMapping("")
    public Page<ProjectResponseDTO> getAll(Pageable pageable) {
        return projectService.getAll(pageable);
    }

    @ProjectApiDoc.GetProjectById
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDetailsDTO> getByid(@PathVariable("id") UUID id) {
        var project = projectService.getById(id);
        return ResponseEntity.ok().body(project);
    }


    @ProjectApiDoc.DeleteProject
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ProjectApiDoc.UpdateProject
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProject(@PathVariable("id") UUID id, @RequestBody ProjectRequestDTO data) {
        projectService.updateProject(id, data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
