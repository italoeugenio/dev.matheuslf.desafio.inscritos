package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.models.dtos.AuthenticationRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.RegisterAdminDTO;
import dev.matheuslf.desafio.inscritos.models.service.AdminAuthenticationService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("internal")
public class AdminAuthenticationController {

    @Autowired
    private AdminAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody AuthenticationRequestDTO data) {
        return authenticationService.login(data);
    }

    @PostMapping("/user/create")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterAdminDTO data) throws AuthenticationException, BadRequestException {
        return authenticationService.register(data);
    }
}
