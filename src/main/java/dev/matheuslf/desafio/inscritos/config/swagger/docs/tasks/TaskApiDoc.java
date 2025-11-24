package dev.matheuslf.desafio.inscritos.config.swagger.docs.tasks;

import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectNotFoundException;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskExceptionDetails;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskNotFoundException;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskResponseDetailsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static dev.matheuslf.desafio.inscritos.config.swagger.docs.tasks.TaskApiDocConstants.*;

public @interface TaskApiDoc {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = CREATE_TASK_SUMMARY,
            description = CREATE_TASK_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = RESPONSE_201_CREATED),
            @ApiResponse(responseCode = "400", description = RESPONSE_400_INVALID,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskExceptionDetails.class))
            ),
            @ApiResponse(responseCode = "404", description = RESPONSE_404_PROJECT_NOT_FOUND,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectNotFoundException.class))
            )
    })
    @interface CreateTasks {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = GET_ALL_TASKS_SUMMARY,
            description = GET_ALL_TASKS_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESPONSE_200_RETRIEVED,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDetailsDTO.class))
            )
    })
    @interface GetAllTasks {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = GET_TASK_BY_ID_SUMMARY,
            description = GET_TASK_BY_ID_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESPONSE_200_FOUND,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDetailsDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = RESPONSE_404_NOT_FOUND,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskNotFoundException.class))
            )
    })
    @interface GetTaskById {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = DELETE_TASK_SUMMARY,
            description = DELETE_TASK_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = RESPONSE_204_DELETED),
            @ApiResponse(responseCode = "404", description = RESPONSE_404_NOT_FOUND,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskNotFoundException.class))
            )
    })
    @interface DeleteTask {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = FIND_TASKS_SUMMARY,
            description = FIND_TASKS_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESPONSE_200_RETRIEVED,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDetailsDTO.class))
            )
    })
    @interface GetTaskByParams {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = UPDATE_TASK_SUMMARY,
            description = UPDATE_TASK_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESPONSE_200_OK),
            @ApiResponse(responseCode = "400", description = RESPONSE_400_INVALID,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskExceptionDetails.class))
            ),
            @ApiResponse(responseCode = "404", description = RESPONSE_404_NOT_FOUND,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskNotFoundException.class))
            )
    })
    @interface UpdateTask {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = UPDATE_TASK_STATUS_SUMMARY,
            description = UPDATE_TASK_STATUS_DESC
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESPONSE_200_OK),
            @ApiResponse(responseCode = "404", description = RESPONSE_404_NOT_FOUND,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskNotFoundException.class))
            )
    })
    @interface UpdateTaskStatus {}
}