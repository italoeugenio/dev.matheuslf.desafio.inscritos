package dev.matheuslf.desafio.inscritos.exceptions.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotFound extends  UserException{
    public UserNotFound(String message){
        super(message);
    }
}
