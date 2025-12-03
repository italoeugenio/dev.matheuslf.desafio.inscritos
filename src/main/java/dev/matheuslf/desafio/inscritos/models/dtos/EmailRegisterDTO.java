package dev.matheuslf.desafio.inscritos.models.dtos;

public record EmailRegisterDTO(
        String to,
        String subject,
        String body
) {}