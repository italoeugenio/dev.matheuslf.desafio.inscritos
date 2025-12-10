package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.CodeType;
import dev.matheuslf.desafio.inscritos.enums.UserRole;
import dev.matheuslf.desafio.inscritos.exceptions.Authentication.AuthenticationException;
import dev.matheuslf.desafio.inscritos.infra.security.TokenService;
import dev.matheuslf.desafio.inscritos.models.dtos.*;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import dev.matheuslf.desafio.inscritos.models.entities.ValidationCodesModel;
import dev.matheuslf.desafio.inscritos.models.repository.UserRepository;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;

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

    private String generateRandomCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public ResponseEntity login(AuthenticationRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email().toLowerCase(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((UserModel) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    public ResponseEntity<String> register(RegisterUserDTO data) {
        if (userRepository.findByEmail(data.email().toLowerCase()) != null) {
            throw new AuthenticationException("Could not complete registration. If you already have an account, please sign in");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        UserModel newUser = new UserModel();
        newUser.setFullName(data.fullName());
        newUser.setEmail(data.email().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(UserRole.VIEWER);

        String codeValue = generateRandomCode();
        ValidationCodesModel validationCode = new ValidationCodesModel();
        validationCode.setCode(codeValue);
        validationCode.setCodeType(CodeType.EMAIL_VERIFICATION);
        validationCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        validationCode.setUser(newUser);

        newUser.getValidationCodes().add(validationCode);

        EmailMessageDTO email = new EmailMessageDTO(
                newUser.getEmail(),
                "Verification Code",
                "Your code is: " + codeValue + "\nIt expires in 10 minutes."
        );

        emailSenderService.sendEmailVerificationCode(email);
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.OK).body("message: Registration successful. Please check your email for verification code.\n" +
                "email: " + newUser.getEmail()
        );
    }

    public ResponseEntity<String> confirm(ConfirmEmailRequestDTO data) {
        UserModel user = userRepository.findUserModelByEmail(data.email().toLowerCase());

        if (user == null) throw new AuthenticationException("User not found");
        if (user.isVerified()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already verified");
        }

        Optional<ValidationCodesModel> validCodeOpt = user.getValidationCodes().stream()
                .filter(code -> code.getCode().equals(data.code()))
                .filter(code -> code.getCodeType() == CodeType.EMAIL_VERIFICATION)
                .filter(code -> code.getConfirmedAt() == null)
                .filter(code -> code.getExpiresAt().isAfter(LocalDateTime.now()))
                .findFirst();

        if (validCodeOpt.isEmpty()) {
            throw new AuthenticationException("Invalid or expired verification code");
        }

        ValidationCodesModel validCode = validCodeOpt.get();
        validCode.setConfirmedAt(LocalDateTime.now());

        user.setVerified(true);
        user.setUpdateAt(LocalDateTime.now());

        userRepository.save(user);
        return ResponseEntity.ok("Email verified successfully");
    }

    @Transactional
    public ResponseEntity<String> resendCode(ResendCodeRequestDTO data) {
        UserModel user = userRepository.findUserModelByEmail(data.email().toLowerCase());
        if (user == null) throw new AuthenticationException("User not found");
        if (user.isVerified()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already verified");

        LocalDateTime now = LocalDateTime.now();
        int timeToNewCode = 5;

        Optional<ValidationCodesModel> lastCode = user.getValidationCodes().stream()
                .filter(c -> c.getCodeType() == CodeType.EMAIL_VERIFICATION)
                .max(Comparator.comparing(codes -> codes.getExpiresAt()));

        if (lastCode.isPresent()) {
            LocalDateTime createdApprox = lastCode.get().getExpiresAt().minusMinutes(10);
            if (now.isBefore(createdApprox.plusMinutes(timeToNewCode))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("You can request a new code only after " + timeToNewCode + " minutes from the last one");
            }
        }

        String newVerificationCode = generateRandomCode();
        ValidationCodesModel newCode = new ValidationCodesModel();
        newCode.setCode(newVerificationCode);
        newCode.setCodeType(CodeType.EMAIL_VERIFICATION);
        newCode.setExpiresAt(now.plusMinutes(10));
        newCode.setUser(user);

        user.getValidationCodes().add(newCode);
        user.setUpdateAt(now);
        userRepository.save(user);

        EmailMessageDTO emailDTO = new EmailMessageDTO(
                user.getEmail(),
                "New Verification Code",
                "Your new verification code is: " + newVerificationCode +
                        "\nIt expires in 10 minutes."
        );
        emailSenderService.sendEmailVerificationCode(emailDTO);

        return ResponseEntity.ok("New verification code sent successfully.");
    }

    @Transactional
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails user) {
        var userModel = userRepository.findUserModelByEmail(user.getUsername());
        if (userModel == null) {
            throw new AuthenticationException("User not found");
        }
        EmailMessageDTO email = new EmailMessageDTO(
                user.getUsername(),
                "Delete your account",
                "This email confirm that yout deleted yout account"
        );
        emailSenderService.sendEmailVerificationCode(email);
        userRepository.delete(userModel);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<String> recoverPassword(RecoverPasswordRequestDTO data){
        UserModel user = userRepository.findUserModelByEmail(data.email().toLowerCase());
        if(user == null) throw new AuthenticationException("User not found");
        if(!user.isVerified()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User email not verified. Please verify your email first.");


        LocalDateTime now = LocalDateTime.now();
        int timeToNewCode = 5;

        Optional<ValidationCodesModel> lastCode = user.getValidationCodes().stream()
                .filter(code -> code.getCodeType() == CodeType.PASSWORD_RESET)
                .filter(code -> code.getConfirmedAt() == null)
                .max(Comparator.comparing(code -> code.getCreateAt()));
        if (lastCode.isPresent()) {
            LocalDateTime createdApprox = lastCode.get().getCreateAt();
            if (now.isBefore(createdApprox.plusMinutes(timeToNewCode))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("You can request a new password reset code only after " + timeToNewCode + " minutes from the last one");
            }
        }

        String resetCode = generateRandomCode();
        ValidationCodesModel newCode = new ValidationCodesModel();
        newCode.setCode(resetCode);
        newCode.setCodeType(CodeType.PASSWORD_RESET);
        newCode.setExpiresAt(now.plusMinutes(15));
        newCode.setUser(user);

        user.getValidationCodes().add(newCode);
        user.setUpdateAt(now);
        userRepository.save(user);

        EmailMessageDTO emailMessage = new EmailMessageDTO(
                user.getEmail(),
                "Password Reset Code",
                "Your password reset code is: " + resetCode +
                        "\nThis code expires in 15 minutes." +
                        "\nIf you didn't request this, please ignore this email."
        );
        emailSenderService.sendEmailVerificationCode(emailMessage);

        return ResponseEntity.ok("Password reset code sent to your email");
    }

    @Transactional
    public ResponseEntity<String> resetPassword(ResetPasswordRequestDTO data) {
        UserModel user = userRepository.findUserModelByEmail(data.email().toLowerCase());

        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        Optional<ValidationCodesModel> validCodeOpt = user.getValidationCodes().stream()
                .filter(code -> code.getCode().equals(data.code()))
                .filter(code -> code.getCodeType() == CodeType.PASSWORD_RESET)
                .filter(code -> code.getConfirmedAt() == null)
                .filter(code -> code.getExpiresAt().isAfter(LocalDateTime.now()))
                .findFirst();

        if (validCodeOpt.isEmpty()) {
            throw new AuthenticationException("Invalid or expired password reset code");
        }

        ValidationCodesModel validCode = validCodeOpt.get();
        validCode.setConfirmedAt(LocalDateTime.now());

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.newPassword());
        user.setPassword(encryptedPassword);
        user.setUpdateAt(LocalDateTime.now());

        userRepository.save(user);

        EmailMessageDTO confirmEmail = new EmailMessageDTO(
                user.getEmail(),
                "Password Changed Successfully",
                "Your password has been changed successfully. " +
                        "If you didn't make this change, please contact support immediately."
        );
        emailSenderService.sendEmailVerificationCode(confirmEmail);

        return ResponseEntity.ok("Password reset successfully");
    }
}
