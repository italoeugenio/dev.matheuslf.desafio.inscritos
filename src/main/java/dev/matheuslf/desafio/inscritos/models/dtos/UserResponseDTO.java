package dev.matheuslf.desafio.inscritos.models.dtos;

import dev.matheuslf.desafio.inscritos.enums.UserRole;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String fullName,
        String email,
        UserRole role,
        LocalDateTime createAt,
        LocalDateTime updateAt
) {
    public UserResponseDTO(UserModel user){
        this(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreateAt(),
                user.getUpdateAt());
    }
}
