package dev.matheuslf.desafio.inscritos.controller;

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

    @Operation(summary = "Create a new project", description = "Creates a new project with the provided information. The start date must be in the future and the end date must be after the start date."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created"),
            @ApiResponse(responseCode = "400", description = "Invalid project data - Check if dates are valid",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectExceptionDetails.class)
                    )}
            )
    })
    @PostMapping("/addProject")
    public ResponseEntity<Void> saveProject(@Valid @RequestBody ProjectRequestDTO data) {
        ProjectModel project = projectService.saveProject(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Get all projects with pagination",
            description = "Retrieves a paginated list of all projects. You can control the page size, page number, and sorting."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Projects retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            )
    })
    @GetMapping("")
    public Page<ProjectResponseDTO> getAll(@Parameter(
            description = "Pagination parameters (page, size, sort)",
            example = "page=0&size=10&sort=startDate,desc"
    ) Pageable pageable) {
        return projectService.getAll(pageable);
    }

    @Operation(summary = "Get a project by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "You can find a project with all it´s informations by ID"),
            @ApiResponse(responseCode = "404", description = "Project not found with this ID",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectNotFoundException.class)
                    )}
            )
    }
    )
    @GetMapping("/{id}")
    public ProjectResponseDetailsDTO getByid(@PathVariable("id") UUID id) {
        return projectService.getById(id);
    }

    @Operation(
            summary = "Delete a project by ID",
            description = "Deletes the project by its ID. However, if you delete a project that has tasks all those tasks will be removed")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Project deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found with the provided ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectExceptionDetails.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Update an existing project",
            description = "Updates all information of an existing project. The start date must be in the future and the end date must be after the start date."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid project data - Check if dates are valid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectExceptionDetails.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found with the provided ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectExceptionDetails.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProject(@PathVariable("id") UUID id, @RequestBody ProjectRequestDTO data) {
        projectService.updateProject(id, data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
