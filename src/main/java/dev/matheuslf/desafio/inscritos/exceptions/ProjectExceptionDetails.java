package dev.matheuslf.desafio.inscritos.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectExceptionDetails {
    private String title;
    private int status;
    private String details;
    private String developerMenssage;
    private LocalDateTime timestamp;
}
