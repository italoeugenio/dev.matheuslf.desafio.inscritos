package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.models.dtos.AuthenticationRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.RegisterUserDTO;
import dev.matheuslf.desafio.inscritos.models.service.UserAuthenticationService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.naming.AuthenticationException;

@Controller
@RequestMapping("auth")
public class UserAuthenticationController {

    @Autowired
    private UserAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody AuthenticationRequestDTO data) {
        return authenticationService.login(data);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterUserDTO data) throws AuthenticationException, BadRequestException {
        return authenticationService.register(data);
    }
}
