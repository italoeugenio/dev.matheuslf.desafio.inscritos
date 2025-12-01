package dev.matheuslf.desafio.inscritos.models.dtos;

import dev.matheuslf.desafio.inscritos.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterAdminDTO(
        @NotBlank String fullName,
        @NotBlank String email,
        @NotBlank String password,
        @NotNull UserRole role
        ) {
}
