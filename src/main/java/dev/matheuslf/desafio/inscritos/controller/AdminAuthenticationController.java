package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.models.dtos.*;
import dev.matheuslf.desafio.inscritos.models.service.AdminAuthenticationService;
import dev.matheuslf.desafio.inscritos.models.service.UserAuthenticationService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("internal")
public class AdminAuthenticationController {

    @Autowired
    private AdminAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthenticationRequestDTO data) {
        return authenticationService.login(data);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterUserDTO data) throws AuthenticationException, BadRequestException {
        return authenticationService.register(data);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@Valid @RequestBody ConfirmEmailRequestDTO data){
        return authenticationService.confirm(data);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<String> resendCode(@Valid @RequestBody ResendCodeRequestDTO data){
        return authenticationService.resendCode(data);
    }

    @PostMapping("/recover-password")
    public ResponseEntity<String> recoverPassword(@Valid @RequestBody RecoverPasswordRequestDTO data){
        return authenticationService.recoverPassword(data);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO data){
        return authenticationService.resetPassword(data);
    }

    @DeleteMapping("/delete-my-account")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails user){
        return authenticationService.deleteUser(user);
    }

}

