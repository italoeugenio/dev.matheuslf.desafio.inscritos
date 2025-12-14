package dev.matheuslf.desafio.inscritos.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailMessageDTO(
        @NotBlank(message = "Recipient email is required")
        @Email(message = "Recipient email must be valid")
        String to,

        @NotBlank(message = "Subject is required")
        @Size(max = 255, message = "Subject cannot exceed 255 characters")
        String subject,

        @NotBlank(message = "Email body is required")
        @Size(max = 5000, message = "Email body cannot exceed 5000 characters")
        String body
) {}
