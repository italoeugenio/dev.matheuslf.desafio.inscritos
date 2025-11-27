package dev.matheuslf.desafio.inscritos.exceptions.Authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthenticationExceptionRegister extends RuntimeException {
    private String message;

    public AuthenticationExceptionRegister(String message){
        super(message);
    }
}
