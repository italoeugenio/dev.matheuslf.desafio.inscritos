package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.CodeType;
import dev.matheuslf.desafio.inscritos.enums.UserRole;
import dev.matheuslf.desafio.inscritos.exceptions.Authentication.AuthenticationException;
import dev.matheuslf.desafio.inscritos.infra.security.TokenService;
import dev.matheuslf.desafio.inscritos.models.dtos.*;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import dev.matheuslf.desafio.inscritos.models.entities.ValidationCodesModel;
import dev.matheuslf.desafio.inscritos.models.repository.UserRepository;
import dev.matheuslf.desafio.inscritos.models.service.email.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


class UserAuthenticationServiceTest {

    @Captor
    ArgumentCaptor<EmailMessageDTO> emailCaptor;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UserAuthenticationService userAuthenticationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should return the user token")
    void loginCase1() {
        UserModel userModel = new UserModel();
        userModel.setFullName("Ítalo Santana");
        userModel.setId(UUID.randomUUID());
        userModel.setEmail("italo@gmail.com");
        userModel.setRole(UserRole.VIEWER);
        userModel.setPassword("myPassword12345aA!");
        userModel.setVerified(true);
        userModel.setCreateAt(LocalDateTime.now());
        userModel.setValidationCodes(new ArrayList<>());


        AuthenticationRequestDTO data = new AuthenticationRequestDTO(
                userModel.getEmail(), userModel.getPassword()
        );

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(
                userModel,
                null,
                userModel.getAuthorities()
        );
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);

        String expectedToken = "mock-jwt-token-12345";
        when(tokenService.generateToken(userModel)).thenReturn(expectedToken);

        LoginResponseDTO response = userAuthenticationService.login(data);

        assertNotNull(response);
        assertEquals(expectedToken, response.token());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateToken(userModel);
    }


    @Test
    @DisplayName("Should throw BadCredentialsException when credentials are invalid")
    void loginCase2() {
        AuthenticationRequestDTO data = new AuthenticationRequestDTO(
                "test@gmail.com",
                "wrongPassword"
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        Exception exception = assertThrows(
                BadCredentialsException.class, () -> userAuthenticationService.login(data)
        );

        assertThat(exception.getMessage()).isEqualTo("Bad credentials");
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should register a new user")
    void registerCase1() {
        RegisterUserDTO data = new RegisterUserDTO(
                "Ítalo Santana",
                "italo@gmail.com",
                "password123@ABC"
        );

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        UserModel newUser = new UserModel();
        newUser.setFullName(data.fullName());
        newUser.setEmail(data.email().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(UserRole.VIEWER);

        var userDetails = userRepository.findByEmail(data.email());

        when(userRepository.findByEmail(data.email())).thenReturn((userDetails));

        userAuthenticationService.register(data);

        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());

        EmailMessageDTO email = emailCaptor.getValue();

        assertThat(email.to()).isEqualTo(data.email());
        assertThat(email.subject()).isEqualTo("Verification Code");
        assertThat(newUser.getRole()).isEqualTo(UserRole.VIEWER);

        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    @DisplayName("Should throw an exception when user is already registered")
    void registerCase2() {
        RegisterUserDTO data = new RegisterUserDTO(
                "Ítalo Santana",
                "italo@gmail.com",
                "password123@ABC"
        );

        when(userRepository.findByEmail(data.email().toLowerCase())).thenThrow(new AuthenticationException("Could not complete registration. If you already have an account, please sign in"));

        Exception exception = assertThrows(
                AuthenticationException.class, () -> userAuthenticationService.register(data)
        );

        assertThat(exception.getMessage()).isEqualTo("Could not complete registration. If you already have an account, please sign in");
    }


    @Test
    @DisplayName("Should confirm an account")
    void confirmCase1() {
        ConfirmEmailRequestDTO data = new ConfirmEmailRequestDTO(
                "italo@gmail.com",
                "123456"
        );

        String encryptedPassword = new BCryptPasswordEncoder().encode("password123@ABC");

        UserModel newUser = new UserModel();
        newUser.setFullName("Ítalo Santana");
        newUser.setEmail(data.email().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(UserRole.VIEWER);
        newUser.setVerified(false);

        String codeValue = "123456";
        ValidationCodesModel validationCode = new ValidationCodesModel();
        validationCode.setCode(codeValue);
        validationCode.setCodeType(CodeType.EMAIL_VERIFICATION);
        validationCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        validationCode.setUser(newUser);
        newUser.getValidationCodes().add(validationCode);


        when(userRepository.findUserModelByEmail(data.email())).thenReturn(newUser);

        String result = userAuthenticationService.confirm(data);

        verify(userRepository, times(1)).findUserModelByEmail(data.email());
        verify(userRepository, times(1)).save(newUser);

        assertThat(result).isEqualTo("Email verified successfully");
    }

    @Test
    @DisplayName("Should throw an exception when user not found")
    void confirmCase2() {
        ConfirmEmailRequestDTO data = new ConfirmEmailRequestDTO(
                "italo@gmail.com",
                "123456"
        );

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(null);

        Exception exception = assertThrows(
                AuthenticationException.class, () -> userAuthenticationService.confirm(data)
        );

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    @DisplayName("Should throw an exception when user is already verified")
    void confirmCase3() {
        ConfirmEmailRequestDTO data = new ConfirmEmailRequestDTO(
                "italo@gmail.com",
                "123456"
        );

        String encryptedPassword = new BCryptPasswordEncoder().encode("password123@ABC");

        UserModel newUser = new UserModel();
        newUser.setFullName("Ítalo Santana");
        newUser.setEmail(data.email().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(UserRole.VIEWER);
        newUser.setVerified(true);

        when(userRepository.findUserModelByEmail(newUser.getEmail())).thenReturn(newUser);

        Exception exception = assertThrows(
                AuthenticationException.class, () -> userAuthenticationService.confirm(data)
        );

        assertThat(exception.getMessage()).isEqualTo("User already verified");
    }

    @Test
    @DisplayName("Should throw an exception when code is invalid or expired")
    void confirmCase4() {
        ConfirmEmailRequestDTO data = new ConfirmEmailRequestDTO(
                "italo@gmail.com",
                "123456"
        );

        String encryptedPassword = new BCryptPasswordEncoder().encode("password123@ABC");

        UserModel newUser = new UserModel();
        newUser.setFullName("Ítalo Santana");
        newUser.setEmail(data.email().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(UserRole.VIEWER);
        newUser.setVerified(false);

        ValidationCodesModel validationCode = new ValidationCodesModel();
        validationCode.setCode("123456");
        validationCode.setCodeType(CodeType.EMAIL_VERIFICATION);
        validationCode.setExpiresAt(LocalDateTime.now().minusMinutes(10));
        validationCode.setUser(newUser);

        when(userRepository.findUserModelByEmail(newUser.getEmail())).thenReturn(newUser);

        Exception exception = assertThrows(
                AuthenticationException.class, () -> userAuthenticationService.confirm(data)
        );

        assertThat(exception.getMessage()).isEqualTo("Invalid or expired verification code");
    }


    @Test
    @DisplayName("Should send an email with a new code")
    void resendCodeCase1() {
        ResendCodeRequestDTO data = new ResendCodeRequestDTO(
                "italo@gmail.com"
        );
        String encryptedPassword = new BCryptPasswordEncoder().encode("password123@ABC");

        UserModel newUser = new UserModel();
        newUser.setFullName("Ítalo Santana");
        newUser.setEmail(data.email().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(UserRole.VIEWER);
        newUser.setVerified(false);

        ValidationCodesModel validationCode = new ValidationCodesModel();
        validationCode.setCode("123456");
        validationCode.setCodeType(CodeType.EMAIL_VERIFICATION);
        validationCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        validationCode.setUser(newUser);

        newUser.getValidationCodes().add(validationCode);

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(newUser);

        userAuthenticationService.resendCode(data);

        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());
        verify(userRepository, times(1)).save(newUser);

        EmailMessageDTO email = emailCaptor.getValue();

        assertThat(email.to()).isEqualTo(data.email());
        assertThat(email.subject()).isEqualTo("New Verification Code");
    }

    @Test
    @DisplayName("Should throw an exception when user not found")
    void resendCodeCase2() {
        ResendCodeRequestDTO data = new ResendCodeRequestDTO(
                "italo@gmail.com"
        );

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(null);

        Exception exception = assertThrows(
                AuthenticationException.class, () -> userAuthenticationService.resendCode(data)
        );

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    @DisplayName("Should throw an exception when user is already verified")
    void resendCodeCase3() {
        ResendCodeRequestDTO data = new ResendCodeRequestDTO(
                "italo@gmail.com"
        );
        String encryptedPassword = new BCryptPasswordEncoder().encode("password123@ABC");

        UserModel newUser = new UserModel();
        newUser.setFullName("Ítalo Santana");
        newUser.setEmail(data.email().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(UserRole.VIEWER);
        newUser.setVerified(true);

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(newUser);

        Exception exception = assertThrows(
                AuthenticationException.class, () -> userAuthenticationService.resendCode(data)
        );

        assertThat(exception.getMessage()).isEqualTo("User already verified");
    }

    @Test
    @DisplayName("Should throw an exception if a new code is requested before the cooldown period ends")
    void resendCodeCase4() {
        ResendCodeRequestDTO data = new ResendCodeRequestDTO(
                "italo@gmail.com"
        );
        String encryptedPassword = new BCryptPasswordEncoder().encode("password123@ABC");

        UserModel newUser = new UserModel();
        newUser.setFullName("Ítalo Santana");
        newUser.setEmail(data.email().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(UserRole.VIEWER);
        newUser.setVerified(false);

        ValidationCodesModel validationCode = new ValidationCodesModel();
        validationCode.setCode("123456");
        validationCode.setCodeType(CodeType.EMAIL_VERIFICATION);
        validationCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        validationCode.setUser(newUser);

        newUser.getValidationCodes().add(validationCode);

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(newUser);

        Exception exception = assertThrows(
                AuthenticationException.class, () -> userAuthenticationService.resendCode(data)
        );

        assertThat(exception.getMessage()).isEqualTo("You can request a new code only after 5 minutes from the last one");
    }

    @Test
    @DisplayName("Should delete your account")
    void deleteUserCase1() {
        String encryptedPassword = new BCryptPasswordEncoder().encode("password123@ABC");

        UserModel user = new UserModel();
        user.setFullName("Ítalo Santana");
        user.setEmail("italo@gmail.com");
        user.setPassword(encryptedPassword);
        user.setRole(UserRole.VIEWER);
        user.setVerified(true);

        when(userRepository.findUserModelByEmail(user.getEmail())).thenReturn(user);

        userAuthenticationService.deleteUser(user);

        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());
        verify(userRepository, times(1)).delete(user);

        EmailMessageDTO email = emailCaptor.getValue();

        assertThat(email.to()).isEqualTo(user.getEmail());
        assertThat(email.subject()).isEqualTo("Delete your account");
        // Note: Mantendo o texto original do seu Service para o teste passar
        assertThat(email.body()).isEqualTo("This email confirm that yout deleted yout account");
    }

    @Test
    @DisplayName("Should throw an exception when user not found")
    void deleteUserCase2(){
        String encryptedPassword = new BCryptPasswordEncoder().encode("password123@ABC");

        UserModel user = new UserModel();
        user.setFullName("Ítalo Santana");
        user.setEmail("italo@gmail.com");
        user.setPassword(encryptedPassword);
        user.setRole(UserRole.VIEWER);
        user.setVerified(true);

        when(userRepository.findUserModelByEmail(user.getEmail())).thenReturn(null);

        Exception exception = assertThrows(
                AuthenticationException.class, () -> userAuthenticationService.deleteUser(user)
        );

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    @DisplayName("Should send an email with a code to reset your password")
    void recoverPasswordCase1() {
        RecoverPasswordRequestDTO data = new RecoverPasswordRequestDTO("italo@gmail.com");

        UserModel user = new UserModel();
        user.setEmail(data.email());
        user.setVerified(true);
        user.setValidationCodes(new ArrayList<>());

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(user);

        String result = userAuthenticationService.recoverPassword(data);

        verify(userRepository, times(1)).save(user);
        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());

        EmailMessageDTO email = emailCaptor.getValue();
        assertThat(email.to()).isEqualTo(data.email());
        assertThat(email.subject()).isEqualTo("Password Reset Code");
        assertThat(result).isEqualTo("Password reset code sent to your email");
    }

    @Test
    @DisplayName("Should throw an exception when user not found")
    void recoverPasswordCase2() {
        RecoverPasswordRequestDTO data = new RecoverPasswordRequestDTO("unknown@gmail.com");
        when(userRepository.findUserModelByEmail(anyString())).thenReturn(null);

        Exception exception = assertThrows(AuthenticationException.class,
                () -> userAuthenticationService.recoverPassword(data));

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    @DisplayName("Should throw an exception when user is not verified")
    void recoverPasswordCase3() {
        RecoverPasswordRequestDTO data = new RecoverPasswordRequestDTO("italo@gmail.com");
        UserModel user = new UserModel();
        user.setEmail(data.email());
        user.setVerified(false);

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(user);

        Exception exception = assertThrows(AuthenticationException.class,
                () -> userAuthenticationService.recoverPassword(data));

        assertThat(exception.getMessage()).isEqualTo("User email not verified. Please verify your email first.");
    }

    @Test
    @DisplayName("Should throw an exception because of cooldown time")
    void recoverPasswordCase4() {
        RecoverPasswordRequestDTO data = new RecoverPasswordRequestDTO("italo@gmail.com");
        UserModel user = new UserModel();
        user.setEmail(data.email());
        user.setVerified(true);
        user.setValidationCodes(new ArrayList<>());

        ValidationCodesModel recentCode = new ValidationCodesModel();
        recentCode.setCodeType(CodeType.PASSWORD_RESET);
        recentCode.setCreateAt(LocalDateTime.now().minusMinutes(2));
        user.getValidationCodes().add(recentCode);

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(user);

        Exception exception = assertThrows(AuthenticationException.class,
                () -> userAuthenticationService.recoverPassword(data));

        assertThat(exception.getMessage()).contains("You can request a new password reset code only after 5 minutes");
    }


    @Test
    @DisplayName("Should change the password")
    void resetPasswordCase1() {
        ResetPasswordRequestDTO data = new ResetPasswordRequestDTO(
                "italo@gmail.com",
                "123456",
                "newStrongPassword123!"
        );

        UserModel user = new UserModel();
        user.setEmail(data.email());
        user.setPassword("oldPassword");
        user.setValidationCodes(new ArrayList<>());

        ValidationCodesModel validCode = new ValidationCodesModel();
        validCode.setCode("123456");
        validCode.setCodeType(CodeType.PASSWORD_RESET);
        validCode.setConfirmedAt(null);
        validCode.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        user.getValidationCodes().add(validCode);

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(user);

        String result = userAuthenticationService.resetPassword(data);

        verify(userRepository, times(1)).save(user);
        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());

        EmailMessageDTO email = emailCaptor.getValue();
        assertThat(email.subject()).isEqualTo("Password Changed Successfully");
        assertThat(result).isEqualTo("Password reset successfully");
        assertThat(user.getPassword()).isNotEqualTo("oldPassword");
        assertNotNull(validCode.getConfirmedAt());
    }

    @Test
    @DisplayName("Should throw an exception when user not found")
    void resetPasswordCase2() {
        ResetPasswordRequestDTO data = new ResetPasswordRequestDTO("error@test.com", "123456", "pass");
        when(userRepository.findUserModelByEmail(anyString())).thenReturn(null);

        Exception exception = assertThrows(AuthenticationException.class,
                () -> userAuthenticationService.resetPassword(data));

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    @DisplayName("Should throw an exception when code is invalid or expired")
    void resetPasswordCase3() {
        ResetPasswordRequestDTO data = new ResetPasswordRequestDTO("italo@gmail.com", "wrong_code", "pass");

        UserModel user = new UserModel();
        user.setEmail(data.email());
        user.setValidationCodes(new ArrayList<>());

        ValidationCodesModel expiredCode = new ValidationCodesModel();
        expiredCode.setCode("123456");
        expiredCode.setCodeType(CodeType.PASSWORD_RESET);
        expiredCode.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        user.getValidationCodes().add(expiredCode);

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(user);

        Exception exception = assertThrows(AuthenticationException.class,
                () -> userAuthenticationService.resetPassword(data));

        assertThat(exception.getMessage()).isEqualTo("Invalid or expired password reset code");
    }
}