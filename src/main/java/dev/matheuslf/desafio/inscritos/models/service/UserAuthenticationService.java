package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.UserRole;
import dev.matheuslf.desafio.inscritos.exceptions.Authentication.AuthenticationException;
import dev.matheuslf.desafio.inscritos.exceptions.Authentication.InvalidEmailException;
import dev.matheuslf.desafio.inscritos.infra.security.TokenService;
import dev.matheuslf.desafio.inscritos.models.dtos.AuthenticationRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.EmailRegisterDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.LoginResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.RegisterUserDTO;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import dev.matheuslf.desafio.inscritos.models.repository.UserRepository;
import dev.matheuslf.desafio.inscritos.utils.EmailValidator;
import dev.matheuslf.desafio.inscritos.utils.VerificationCode;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Service
public class UserAuthenticationService {

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

    public ResponseEntity<String> register(RegisterUserDTO data) throws BadRequestException {
        if(!EmailValidator.isValidEmail(data.email().toLowerCase())){
            throw new InvalidEmailException("Invalid e-mail. You need enter a valid e-mail");
        }

        if(userRepository.findByEmail(data.email().toLowerCase()) != null){
            throw new AuthenticationException("Could not complete registration. If you already have an account, please sing in");
        }
        
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        String verificationToken =  VerificationCode.generateVerificationCode();
        LocalDateTime verificationCodeExpiresAt = VerificationCode.codeExpiresAt(10);
        UserModel newUser = new UserModel(data.fullName(), data.email().toLowerCase(), encryptedPassword, UserRole.VIEWER, verificationToken,verificationCodeExpiresAt);

        EmailRegisterDTO email = new EmailRegisterDTO(
                newUser.getEmail(),
                "Verification Code",
                "Your code is: " + verificationToken
                        + "\nIt expires in 10 minutes."
        );

        emailSenderService.sendEmailVerificationCode(email);
        userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.OK).body("message: Registration successful. Please check your email for verification code.\n" +
                "email: " + newUser.getEmail()
        );
    }


    public ResponseEntity<String> confirm(String userEmail, String code){
        UserModel user = userRepository.findUserModelByEmail(userEmail.toLowerCase());

        if (user == null) throw new AuthenticationException("User not found");
        if (user.isVerified()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User already verified");
        }
        if (user.getVerificationToken() == null) throw new AuthenticationException("No verification code found");
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) throw new AuthenticationException("Verification code has expired");
        if (!user.getVerificationToken().equals(code)) throw new AuthenticationException("Invalid verification code");

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationCodeExpiresAt(null);
        user.setUpdateAt(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok("Email verified successfully");
    }

    public ResponseEntity<String> resendCode(String email) {
        UserModel user = userRepository.findUserModelByEmail(email.toLowerCase());
        if (user == null) throw new AuthenticationException("User not found");
        if (user.isVerified()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already verified");

        LocalDateTime now = LocalDateTime.now();

        int timeToNewCode = 5;
        if (user.getVerificationCodeExpiresAt() != null &&
                now.isBefore(user.getVerificationCodeExpiresAt().minusMinutes(timeToNewCode))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("You can request a new code only after " + timeToNewCode +" minutes from the last one");
        }

        String newVerificationCode = VerificationCode.generateVerificationCode();
        LocalDateTime newExpiration = VerificationCode.codeExpiresAt(10);

        user.setVerificationToken(newVerificationCode);
        user.setVerificationCodeExpiresAt(newExpiration);
        user.setUpdateAt(now);
        userRepository.save(user);

        EmailRegisterDTO emailDTO = new EmailRegisterDTO(
                user.getEmail(),
                "New Verification Code",
                "Your new verification code is: " + newVerificationCode +
                        "\nIt expires in 10 minutes."
        );
        emailSenderService.sendEmailVerificationCode(emailDTO);

        return ResponseEntity.ok("New verification code sent successfully. It expires at " + newExpiration);
    }

    public ResponseEntity recoverPassword(String email){
        return null;
    }

    public ResponseEntity changePassword(){
        return null;
    }

}
