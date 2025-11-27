package dev.matheuslf.desafio.inscritos.models.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDTO(
        @NotBlank String email,
        @NotBlank String password
) {
}
