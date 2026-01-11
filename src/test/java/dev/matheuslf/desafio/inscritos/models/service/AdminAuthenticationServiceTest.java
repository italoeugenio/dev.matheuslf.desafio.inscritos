package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.CodeType;
import dev.matheuslf.desafio.inscritos.enums.UserRole;
import dev.matheuslf.desafio.inscritos.exceptions.Authentication.AuthenticationException;
import dev.matheuslf.desafio.inscritos.exceptions.User.UserAdminDelete;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class AdminAuthenticationServiceTest {

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
    private AdminAuthenticationService adminAuthenticationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return the admin token upon successful login")
    void loginCase1() {
        UserModel adminModel = new UserModel();
        adminModel.setEmail("admin@test.com");
        adminModel.setRole(UserRole.ADMIN);

        AuthenticationRequestDTO data = new AuthenticationRequestDTO(adminModel.getEmail(), "password123");

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(adminModel, null, adminModel.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);

        String expectedToken = "mock-jwt-token";
        when(tokenService.generateToken(adminModel)).thenReturn(expectedToken);

        LoginResponseDTO response = adminAuthenticationService.login(data);

        assertNotNull(response);
        assertEquals(expectedToken, response.token());
        verify(tokenService, times(1)).generateToken(adminModel);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when admin credentials are invalid")
    void loginCase2() {
        AuthenticationRequestDTO data = new AuthenticationRequestDTO("admin@test.com", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> adminAuthenticationService.login(data));
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should register a new admin user")
    void registerCase1() {
        RegisterUserDTO data = new RegisterUserDTO("Admin User", "admin@test.com", "password123");

        when(userRepository.findByEmail(data.email())).thenReturn(null);

        adminAuthenticationService.register(data);

        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());
        verify(userRepository, times(1)).save(any(UserModel.class));

        EmailMessageDTO email = emailCaptor.getValue();
        assertThat(email.to()).isEqualTo(data.email());
        assertThat(email.subject()).isEqualTo("Verification Code");
    }

    @Test
    @DisplayName("Should throw an exception when admin email is already registered")
    void registerCase2() {
        RegisterUserDTO data = new RegisterUserDTO("Admin User", "admin@test.com", "password123");
        when(userRepository.findByEmail(anyString())).thenReturn(new UserModel());

        assertThrows(AuthenticationException.class, () -> adminAuthenticationService.register(data));
    }

    @Test
    @DisplayName("Should confirm an admin account")
    void confirmCase1() {
        ConfirmEmailRequestDTO data = new ConfirmEmailRequestDTO("admin@test.com", "123456");
        UserModel admin = new UserModel();
        admin.setEmail(data.email());
        admin.setVerified(false);
        admin.setValidationCodes(new ArrayList<>());

        ValidationCodesModel code = new ValidationCodesModel();
        code.setCode("123456");
        code.setCodeType(CodeType.EMAIL_VERIFICATION);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        admin.getValidationCodes().add(code);

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(admin);

        String result = adminAuthenticationService.confirm(data);

        assertThat(result).isEqualTo("Email verified successfully");
        assertTrue(admin.isVerified());
        verify(userRepository, times(1)).save(admin);
    }

    @Test
    @DisplayName("Should send a new verification code for admin")
    void resendCodeCase1() {
        ResendCodeRequestDTO data = new ResendCodeRequestDTO("admin@test.com");
        UserModel admin = new UserModel();
        admin.setEmail(data.email());
        admin.setVerified(false);
        admin.setValidationCodes(new ArrayList<>());

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(admin);

        adminAuthenticationService.resendCode(data);

        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());
        assertThat(emailCaptor.getValue().subject()).isEqualTo("New Verification Code");
    }

    @Test
    @DisplayName("Should delete admin account when other admins exist")
    void deleteUserCase1() {
        UserModel admin = new UserModel();
        admin.setId(UUID.randomUUID());
        admin.setEmail("admin@test.com");

        when(userRepository.findUserModelByEmail(admin.getEmail())).thenReturn(admin);
        when(userRepository.countOtherAdmins(admin.getId())).thenReturn(1L);

        adminAuthenticationService.deleteUser(admin);

        verify(userRepository, times(1)).delete(admin);
        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());
        assertThat(emailCaptor.getValue().subject()).isEqualTo("Account Deletion Confirmation");
    }

    @Test
    @DisplayName("Should throw UserAdminDelete exception if trying to delete the last admin")
    void deleteUserCase2() {
        UserModel admin = new UserModel();
        admin.setId(UUID.randomUUID());
        admin.setEmail("admin@test.com");

        when(userRepository.findUserModelByEmail(admin.getEmail())).thenReturn(admin);
        when(userRepository.countOtherAdmins(admin.getId())).thenReturn(0L);

        assertThrows(UserAdminDelete.class, () -> adminAuthenticationService.deleteUser(admin));
        verify(userRepository, never()).delete(admin);
    }

    @Test
    @DisplayName("Should send a password recovery code to admin")
    void recoverPasswordCase1() {
        RecoverPasswordRequestDTO data = new RecoverPasswordRequestDTO("admin@test.com");
        UserModel admin = new UserModel();
        admin.setEmail(data.email());
        admin.setVerified(true);
        admin.setValidationCodes(new ArrayList<>());

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(admin);

        String result = adminAuthenticationService.recoverPassword(data);

        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());
        assertThat(emailCaptor.getValue().subject()).isEqualTo("Password Reset Code");
        assertThat(result).isEqualTo("Password reset code sent to your email");
    }

    @Test
    @DisplayName("Should successfully reset admin password")
    void resetPasswordCase1() {
        ResetPasswordRequestDTO data = new ResetPasswordRequestDTO("admin@test.com", "123456", "newPass123");
        UserModel admin = new UserModel();
        admin.setEmail(data.email());
        admin.setValidationCodes(new ArrayList<>());

        ValidationCodesModel code = new ValidationCodesModel();
        code.setCode("123456");
        code.setCodeType(CodeType.PASSWORD_RESET);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        admin.getValidationCodes().add(code);

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(admin);

        String result = adminAuthenticationService.resetPassword(data);

        verify(userRepository, times(1)).save(admin);
        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());
        assertThat(emailCaptor.getValue().subject()).isEqualTo("Password Changed Successfully");
        assertThat(result).isEqualTo("Password reset successfully");
    }

    @Test
    @DisplayName("Should throw an exception when password reset code is invalid for admin")
    void resetPasswordCase2() {
        ResetPasswordRequestDTO data = new ResetPasswordRequestDTO("admin@test.com", "wrong", "newPass");
        UserModel admin = new UserModel();
        admin.setEmail(data.email());
        admin.setValidationCodes(new ArrayList<>());

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(admin);

        assertThrows(AuthenticationException.class, () -> adminAuthenticationService.resetPassword(data));
    }
}