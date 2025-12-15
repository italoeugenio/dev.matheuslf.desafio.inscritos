package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.UserRole;
import dev.matheuslf.desafio.inscritos.exceptions.User.UserException;
import dev.matheuslf.desafio.inscritos.exceptions.User.UserNotFound;
import dev.matheuslf.desafio.inscritos.models.dtos.DeleteUserRequest;
import dev.matheuslf.desafio.inscritos.models.dtos.EmailMessageDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.UserResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.UserUpdateRoleRequestDTO;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import dev.matheuslf.desafio.inscritos.models.repository.UserRepository;
import dev.matheuslf.desafio.inscritos.models.service.email.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class UserManagerServiceTest {

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

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(user.getEmail());
        assertThat(result.fullName()).isEqualTo(user.getFullName());

        verify(userRepository, times(1)).findUserModelByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void getByEmailCase2() {
        String email = "nouser@gmail.com";
        when(userRepository.findUserModelByEmail(email)).thenReturn(null);
        assertThrows(UserNotFound.class, () ->{
            userManagerService.getByEmail(email);
        });
    }

    @Test
    @DisplayName("Should update user role")
    void updateUserRoleCase1() {
        UserModel user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setEmail("italo@gmail.com");
        user.setRole(UserRole.VIEWER);
        user.setFullName("Ítalo Santana");

        UserUpdateRoleRequestDTO newRole = new UserUpdateRoleRequestDTO(user.getEmail(), UserRole.ADMIN);

        when(userRepository.findUserModelByEmail(user.getEmail())).thenReturn(user);

        userManagerService.updateUserRole(newRole);

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);

        verify(userRepository, times(1)).findUserModelByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void updateUserRoleCase2() {
        String email = "test@gmail.com";
        UserUpdateRoleRequestDTO data = new UserUpdateRoleRequestDTO(email, UserRole.ADMIN);

        when(userRepository.findUserModelByEmail(data.email())).thenReturn(null);

        assertThrows(UserNotFound.class, () ->{
            userManagerService.updateUserRole(data);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete the user")
    void deleteUserCase1() {
        UserModel user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setEmail("italo@gmail.com");
        user.setRole(UserRole.VIEWER);
        user.setFullName("Ítalo Santana");

        when(userRepository.findUserModelByEmail(user.getEmail())).thenReturn(user);

        DeleteUserRequest data = new DeleteUserRequest(user.getEmail(), "This is a message to explain why we are deleting the account");
        EmailMessageDTO emailMessageDTO = new EmailMessageDTO(data.email(), "DELETED ACCOUNT", data.deleteMessage());

        userManagerService.deleteUser(data);

        verify(userRepository, times(1)).delete(user);
        verify(emailSenderService, times(1)).sendEmailVerificationCode(emailMessageDTO);
    }

    @Test
    @DisplayName("Should throw UserException when trying to delete an ADMIN user")
    void deleteUserCase2() {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest("test@gmail.com", "This is a message to explain why we are deleting the account");

        when(userRepository.findUserModelByEmail(deleteUserRequest.email())).thenReturn(null);

        assertThrows(UserNotFound.class, () ->{
            userManagerService.deleteUser(deleteUserRequest);
        });

        verify(userRepository, times(1)).findByEmail(deleteUserRequest.email());
    }

    @Test
    @DisplayName("Should throw UserException when trying to delete an ADMIN")
    void deleteUser_AdminError() {
        String email = "admin@gmail.com";
        DeleteUserRequest request = new DeleteUserRequest(email, "Reason");

        UserModel adminUser = new UserModel();
        adminUser.setEmail(email);
        adminUser.setRole(UserRole.ADMIN);

        when(userRepository.findUserModelByEmail(email)).thenReturn(adminUser);

        assertThrows(UserException.class, () -> {
            userManagerService.deleteUser(request);
        });

        verify(userRepository, never()).delete(any());
        verify(emailSenderService, never()).sendEmailVerificationCode(any());
    }
}