package dev.matheuslf.desafio.inscritos.models.repository;

import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    UserDetails findByEmail(String email);
    UserModel findUserModelByEmail(String email);

    @Query(value = "SELECT * FROM TB_USERS WHERE ROLE = 'ADMIN' LIMIT 1", nativeQuery = true)
    UserModel existAnyAdminUser();

}
