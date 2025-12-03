package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.models.dtos.AuthenticationRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ConfirmEmailRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.RegisterUserDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ResendCodeRequestDTO;
import dev.matheuslf.desafio.inscritos.models.service.UserAuthenticationService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("auth")
public class UserAuthenticationController {

    @Autowired
    private UserAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthenticationRequestDTO data) {
        return authenticationService.login(data);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterUserDTO data) throws AuthenticationException, BadRequestException {
        return authenticationService.register(data);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestBody @Valid ConfirmEmailRequestDTO data){
        return authenticationService.confirm(data.email().toLowerCase(), data.code());
    }

    @PostMapping("/resend-code")
    public ResponseEntity<String> resendCode(@RequestBody @Valid ResendCodeRequestDTO data){
        return authenticationService.resendCode(data.email());
    }
}
