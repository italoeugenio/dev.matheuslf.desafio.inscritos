package dev.matheuslf.desafio.inscritos.handler;

import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectException;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectExceptionDetails;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectNotFoundException;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskException;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskExceptionDetails;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class TaskExceptionHandler {
    @ExceptionHandler(TaskException.class)
    public ResponseEntity<TaskExceptionDetails> handlerProjectException(TaskException exception) {
        return new ResponseEntity<>(
                TaskExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .title("Bad Request Exception, check the documentation")
                        .details(exception.getMessage())
                        .developerMenssage(exception.getClass().getName())
                        .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<TaskExceptionDetails> handlerProjectNotFoundException(TaskException exception) {
        return new ResponseEntity<>(
                TaskExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .title("There is no task with this ID")
                        .details(exception.getMessage())
                        .developerMenssage(exception.getClass().getName())
                        .build(), HttpStatus.NOT_FOUND
        );
    }
}
