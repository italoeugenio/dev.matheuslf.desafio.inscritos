package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.UserRole;
import dev.matheuslf.desafio.inscritos.exceptions.Authentication.AuthenticationException;
import dev.matheuslf.desafio.inscritos.exceptions.Authentication.InvalidEmailException;
import dev.matheuslf.desafio.inscritos.infra.security.TokenService;
import dev.matheuslf.desafio.inscritos.models.dtos.AuthenticationRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.LoginResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.RegisterUserDTO;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import dev.matheuslf.desafio.inscritos.models.repository.UserRepository;
import dev.matheuslf.desafio.inscritos.utils.EmailValidator;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserAuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public ResponseEntity login(@Valid @RequestBody AuthenticationRequestDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email().toLowerCase(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((UserModel) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    public ResponseEntity<Void> register(RegisterUserDTO data) throws BadRequestException {
        if(!EmailValidator.isValidEmail(data.email().toLowerCase())){
            throw new InvalidEmailException("Invalid e-mail. You need enter a valid e-mail");
        }

        if(userRepository.findByEmail(data.email().toLowerCase()) != null){
            throw new AuthenticationException("Could not complete registration. If you already have an account, please sing in");
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        UserModel newUser = new UserModel(data.fullName(), data.email().toLowerCase(), encryptedPassword, UserRole.VIEWER);
        userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }

//    auth.route("/", confirm);
//    auth.route("/recover-password", requestRecovery);
//    auth.route("/change-password", changePassword);
}
