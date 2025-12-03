package dev.matheuslf.desafio.inscritos.models.repository;

import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    UserDetails findByEmail(String email);
    UserModel findUserModelByEmail(String email);
}
