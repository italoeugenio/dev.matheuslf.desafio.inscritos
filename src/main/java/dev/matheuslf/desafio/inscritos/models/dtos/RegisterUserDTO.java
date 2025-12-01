package dev.matheuslf.desafio.inscritos.models.dtos;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserDTO(
        @NotBlank String fullName,
        @NotBlank String email,
        @NotBlank String password
) {
}
