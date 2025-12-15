package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.UserRole;
import dev.matheuslf.desafio.inscritos.models.dtos.UserResponseDTO;
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

import static org.assertj.core.api.Assertions.*;
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

    }

    @Test
    void updateUserRole() {
    }

    @Test
    void deleteUser() {
    }

}