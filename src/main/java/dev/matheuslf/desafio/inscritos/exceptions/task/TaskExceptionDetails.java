package dev.matheuslf.desafio.inscritos.exceptions.task;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskExceptionDetails {
    private String title;
    private int status;
    private String details;
    private String developerMenssage;
    private LocalDateTime timestamp;
}
