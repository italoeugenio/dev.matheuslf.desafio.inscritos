package dev.matheuslf.desafio.inscritos.handler;

import dev.matheuslf.desafio.inscritos.exceptions.User.UserException;
import dev.matheuslf.desafio.inscritos.exceptions.User.UserExceptionDetails;
import dev.matheuslf.desafio.inscritos.exceptions.User.UserNotFound;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskException;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskExceptionDetails;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class UserHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<UserExceptionDetails> handlerProjectException(UserException exception) {
        return new ResponseEntity<>(
                UserExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .title("Bad Request Exception, check the documentation")
                        .details(exception.getMessage())
                        .developerMenssage(exception.getClass().getName())
                        .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<UserExceptionDetails> handlerProjectNotFoundException(UserNotFound exception) {
        return new ResponseEntity<>(
                UserExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .title("There is no user with this EMAIL")
                        .details(exception.getMessage())
                        .developerMenssage(exception.getClass().getName())
                        .build(), HttpStatus.NOT_FOUND
        );
    }
}
