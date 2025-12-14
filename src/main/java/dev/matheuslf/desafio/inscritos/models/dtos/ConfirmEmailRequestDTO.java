package dev.matheuslf.desafio.inscritos.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConfirmEmailRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String code
) {}
