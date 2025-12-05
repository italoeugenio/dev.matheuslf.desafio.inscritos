package dev.matheuslf.desafio.inscritos.exceptions.User;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserExceptionDetails {
    private String title;
    private int status;
    private String details;
    private String developerMenssage;
    private LocalDateTime timestamp;
}
