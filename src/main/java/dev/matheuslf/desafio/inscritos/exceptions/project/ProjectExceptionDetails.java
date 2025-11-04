package dev.matheuslf.desafio.inscritos.exceptions.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectExceptionDetails {
    @Schema(example = "Bad Request Exception, check the documentation")
    private String title;
    @Schema(example = "400")
    private int status;
    @Schema(example = "The end date can't be before start date")
    private String details;
    @Schema(example = "dev.matheuslf.desafio.inscritos.exceptions.project.ProjectException")
    private String developerMessage;
    @Schema(example = "2025-11-04T13:47:25.0729909")
    private LocalDateTime timestamp;
}
