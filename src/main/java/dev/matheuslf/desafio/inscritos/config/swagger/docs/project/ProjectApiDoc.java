package dev.matheuslf.desafio.inscritos.config.swagger.docs.project;

import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectExceptionDetails;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static dev.matheuslf.desafio.inscritos.config.swagger.docs.project.ProjectApiDocConstants.*;

public @interface ProjectApiDoc {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = CREATE_PROJECT_SUMMARY,
            description = CREATE_PROJECT_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = RESPONSE_201_CREATED),
            @ApiResponse(responseCode = "400", description = RESPONSE_400_INVALID,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectExceptionDetails.class))
            )
    })
    @interface CreateProject {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = GET_ALL_PROJECTS_SUMMARY,
            description = GET_ALL_PROJECTS_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESPONSE_200_RETRIEVED,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            )
    })
    @interface GetAllProjects {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = GET_PROJECT_BY_ID_SUMMARY)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESPONSE_200_FOUND),
            @ApiResponse(responseCode = "404", description = RESPONSE_404_NOT_FOUND,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectNotFoundException.class))
            )
    })
    @interface GetProjectById {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = DELETE_PROJECT_SUMMARY,
            description = DELETE_PROJECT_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = RESPONSE_204_DELETED),
            @ApiResponse(responseCode = "404", description = RESPONSE_404_NOT_FOUND,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectNotFoundException.class))
            )
    })
    @interface DeleteProject {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = UPDATE_PROJECT_SUMMARY,
            description = UPDATE_PROJECT_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESPONSE_200_OK),
            @ApiResponse(responseCode = "400", description = RESPONSE_400_INVALID,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectExceptionDetails.class))
            ),
            @ApiResponse(responseCode = "404", description = RESPONSE_404_NOT_FOUND,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectExceptionDetails.class))
            )
    })
    @interface UpdateProject {}
}