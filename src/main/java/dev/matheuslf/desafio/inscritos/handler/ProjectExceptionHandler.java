package dev.matheuslf.desafio.inscritos.handler;

import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectException;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectExceptionDetails;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ProjectExceptionHandler {
    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<ProjectExceptionDetails> handlerProjectException(ProjectException exception) {
        return new ResponseEntity<>(
                ProjectExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .title("Bad Request Exception, check the documentation")
                        .details(exception.getMessage())
                        .developerMenssage(exception.getClass().getName())
                        .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ProjectExceptionDetails> handlerProjectNotFoundException(ProjectException exception) {
        return new ResponseEntity<>(
                ProjectExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .title("There is no project with this ID")
                        .details(exception.getMessage())
                        .developerMenssage(exception.getClass().getName())
                        .build(), HttpStatus.NOT_FOUND
        );
    }
}
