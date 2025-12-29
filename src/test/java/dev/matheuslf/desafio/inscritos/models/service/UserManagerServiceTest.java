package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.UserRole;
import dev.matheuslf.desafio.inscritos.exceptions.User.UserException;
import dev.matheuslf.desafio.inscritos.exceptions.User.UserNotFound;
import dev.matheuslf.desafio.inscritos.models.dtos.DeleteUserRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.EmailMessageDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.UserResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.UserUpdateRoleRequestDTO;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import dev.matheuslf.desafio.inscritos.models.repository.UserRepository;
import dev.matheuslf.desafio.inscritos.models.service.email.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserManagerServiceTest {

    @Captor // Crie e inicialize automaticamente um ArgumentCaptor<EmailMessageDTO> antes de cada teste sem ele a variavel ficaria null
    //ArgumentCaptor - Permite capturar o objeto real
    ArgumentCaptor<EmailMessageDTO> emailCaptor;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UserManagerService userManagerService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should return a user by id")
    void getByEmailCase1() {
        UserModel user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setEmail("italo@gmail.com");
        user.setRole(UserRole.VIEWER);
        user.setFullName("Ítalo Santana");

        when(userRepository.findUserModelByEmail(user.getEmail())).thenReturn(user);

        UserResponseDTO result = userManagerService.getByEmail(user.getEmail());

        assertThat(result). isNotNull();
        assertThat(result.email()).isEqualTo(user.getEmail());
        assertThat(result.fullName()).isEqualTo(user.getFullName());

        verify(userRepository, times(1)).findUserModelByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void getByEmailCase2() {
        String email = "nouser@gmail.com";
        when(userRepository.findUserModelByEmail(email)).thenReturn(null);

        UserNotFound exception = assertThrows(
            UserNotFound.class, () -> userManagerService.getByEmail(email)
        );

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    @Test
    @DisplayName("Should update user role")
    void updateUserRoleCase1() {
        UserModel user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setEmail("italo@gmail.com");
        user.setRole(UserRole.VIEWER);
        user.setFullName("Ítalo Santana");

        when(userRepository.findUserModelByEmail(user.getEmail())).thenReturn(user);

        UserUpdateRoleRequestDTO data = new UserUpdateRoleRequestDTO(user.getEmail(), UserRole.ADMIN);

        userManagerService.updateUserRole(data);

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        verify(userRepository, times(1)).findUserModelByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);

    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void updateUserRoleCase2() {
        String email = "nouser@gmail.com";

        when(userRepository.findUserModelByEmail(email)).thenReturn(null);

        UserUpdateRoleRequestDTO request = new UserUpdateRoleRequestDTO(email, UserRole.ADMIN);

        Exception exception = assertThrows(
                UserNotFound.class, () -> userManagerService.updateUserRole(request)
        );

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Should delete an user")
    void deleteUserCase1() {
        UserModel user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setEmail("italo@gmail.com");
        user.setRole(UserRole.VIEWER);
        user.setFullName("Ítalo Santana");

        when(userRepository.findUserModelByEmail(user.getEmail())).thenReturn(user);

        DeleteUserRequestDTO data = new DeleteUserRequestDTO(user.getEmail(), "Delete message");

        userManagerService.deleteUser(data);

        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailCaptor.capture());
        EmailMessageDTO email = emailCaptor.getValue();

        assertThat(email.to()).isEqualTo(user.getEmail());
        assertThat(email.subject()).isEqualTo("DELETED ACCOUNT");
        assertThat(email.body()).isEqualTo("Delete message");

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void deleteUserCase2() {
        String email = "nouser@gmail.com";
        when(userRepository.findUserModelByEmail(email)).thenReturn(null);

        DeleteUserRequestDTO data =
                new DeleteUserRequestDTO(email, "msg");

        UserNotFound exception = assertThrows(
                UserNotFound.class, () -> userManagerService.deleteUser(data)
        );

        assertThat(exception.getMessage()).isEqualTo("User not found");

        verify(userRepository, never()).delete(any());
        verify(emailSenderService, never()).sendEmailVerificationCode(any());
    }

    @Test
    @DisplayName("Should throw exception when try to delete an user with role admin")
    void deleteUserCase3() {
        UserModel user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setEmail("italo@gmail.com");
        user.setRole(UserRole.ADMIN);
        user.setFullName("Ítalo Santana");

        when(userRepository.findUserModelByEmail(user.getEmail())).thenReturn(user);

        DeleteUserRequestDTO data =
                new DeleteUserRequestDTO(user.getEmail(), "msg");

        UserException exception = assertThrows(
                UserException.class, () -> userManagerService.deleteUser(data)
        );

        assertThat(exception.getMessage()).isEqualTo("You can´t delete a user with ADMIN role");

        verify(userRepository, never()).delete(user);
        verify(emailSenderService, never()).sendEmailVerificationCode(any());
    }

}