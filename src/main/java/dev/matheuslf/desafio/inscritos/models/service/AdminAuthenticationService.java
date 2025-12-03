package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.exceptions.Authentication.AuthenticationException;
import dev.matheuslf.desafio.inscritos.exceptions.Authentication.InvalidEmailException;
import dev.matheuslf.desafio.inscritos.infra.security.TokenService;
import dev.matheuslf.desafio.inscritos.models.dtos.AuthenticationRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.EmailRegisterDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.LoginResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.RegisterAdminDTO;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import dev.matheuslf.desafio.inscritos.models.repository.UserRepository;
import dev.matheuslf.desafio.inscritos.utils.EmailValidator;
import dev.matheuslf.desafio.inscritos.utils.VerificationCode;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Service
public class AdminAuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailSenderService emailSenderService;

    public ResponseEntity login(@Valid @RequestBody AuthenticationRequestDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email().toLowerCase(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((UserModel) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    public ResponseEntity<String> register(RegisterAdminDTO data) throws BadRequestException {
        if(!EmailValidator.isValidEmail(data.email().toLowerCase())){
            throw new InvalidEmailException("Invalid e-mail. You need enter a valid e-mail");
        }

        if(userRepository.findByEmail(data.email().toLowerCase()) != null){
            throw new AuthenticationException("Could not complete registration. If you already have an account, please sing in");
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        String verificationToken =  VerificationCode.generateVerificationCode();
        LocalDateTime verificationCodeExpiresAt = VerificationCode.codeExpiresAt(15);
        UserModel newUser = new UserModel(data.fullName(), data.email().toLowerCase(), encryptedPassword, data.role(),verificationToken,verificationCodeExpiresAt);

        EmailRegisterDTO email = new EmailRegisterDTO(
                newUser.getEmail(),
                "Verification Code",
                "Your code is: " + verificationToken
                        + "\nIt expires in 10 minutes."
        );
        emailSenderService.sendEmailVerificationCode(email);
        userRepository.save(newUser);
        return ResponseEntity.ok().body("Check your email to confirm account");
    }

    public ResponseEntity confirm(){
        return null;

    }

    public ResponseEntity recoverPassword(){
        return null;
    }

    public ResponseEntity changePassword(){
        return null;
    }

}
