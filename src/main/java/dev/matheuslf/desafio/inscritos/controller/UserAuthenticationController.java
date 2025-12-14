package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.models.dtos.*;
import dev.matheuslf.desafio.inscritos.models.service.UserAuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(name = "User Login", description = "Endpoints for user login and authentication")
public class UserAuthenticationController {

    @Autowired
    private UserAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthenticationRequestDTO data) {
        return ResponseEntity.ok(authenticationService.login(data));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterUserDTO data) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.register(data));
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@Valid @RequestBody ConfirmEmailRequestDTO data){
        return ResponseEntity.ok(authenticationService.confirm(data));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<String> resendCode(@Valid @RequestBody ResendCodeRequestDTO data){
        return ResponseEntity.ok(authenticationService.resendCode(data));
    }

    @PostMapping("/recover-password")
    public ResponseEntity<String> recoverPassword(@Valid @RequestBody RecoverPasswordRequestDTO data){
        return ResponseEntity.ok(authenticationService.recoverPassword(data));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO data){
        return ResponseEntity.ok(authenticationService.resetPassword(data));
    }

    @DeleteMapping("/delete-my-account")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails user){
        authenticationService.deleteUser(user);
        return ResponseEntity.noContent().build();
    }
}