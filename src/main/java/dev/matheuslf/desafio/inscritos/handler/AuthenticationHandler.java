package dev.matheuslf.desafio.inscritos.handler;

import dev.matheuslf.desafio.inscritos.exceptions.Authentication.AuthenticationExceptionDetails;
import dev.matheuslf.desafio.inscritos.exceptions.Authentication.AuthenticationException;
import dev.matheuslf.desafio.inscritos.exceptions.Authentication.InvalidEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class AuthenticationHandler {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AuthenticationExceptionDetails> handlerAuthenticationException(AuthenticationException exception) {
        return new ResponseEntity<>(
                AuthenticationExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .title("Bad Request Exception, check the documentation")
                        .details(exception.getMessage())
                        .developerMessage(exception.getClass().getName())
                        .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<AuthenticationExceptionDetails> handlerInvalidEmailException(AuthenticationException exception){
        return new ResponseEntity<>(
                AuthenticationExceptionDetails.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .title("Bad Request Exception, check the documentation")
                        .details(exception.getMessage())
                        .developerMessage(exception.getClass().getName())
                        .build(), HttpStatus.BAD_REQUEST
        );
    }
}
