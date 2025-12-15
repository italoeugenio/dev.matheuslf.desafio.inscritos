package dev.matheuslf.desafio.inscritos.models.repository;

import dev.matheuslf.desafio.inscritos.enums.UserRole;
import dev.matheuslf.desafio.inscritos.models.dtos.RegisterUserDTO;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should return 0 when there are no other admins except the current user")
    void countOtherAdminsCase1() {
        RegisterUserDTO data = new RegisterUserDTO("My Admin", "me@gmail.com", "Pass123!");
        UserModel myUser = this.createAdminUser(data);
        long count = userRepository.countOtherAdmins(myUser.getId());
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return the correct count of other admins - 1 that are the logged user")
    void countOtherAdminsCase2() {
        UserModel me = this.createAdminUser(new RegisterUserDTO("Me", "me@gmail.com", "Pass123!"));
        this.createAdminUser(new RegisterUserDTO("Other 1", "other1@gmail.com", "Pass123!"));
        this.createAdminUser(new RegisterUserDTO("Other 2", "other2@gmail.com", "Pass123!"));
        long count = userRepository.countOtherAdmins(me.getId());
        assertThat(count).isEqualTo(2);
    }

    private UserModel createAdminUser(RegisterUserDTO data) {
        UserModel newUser = new UserModel(data);
        newUser.setRole(UserRole.ADMIN);
        this.entityManager.persist(newUser);
        return newUser;
    }
}