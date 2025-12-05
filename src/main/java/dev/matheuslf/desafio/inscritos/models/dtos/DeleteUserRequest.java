package dev.matheuslf.desafio.inscritos.models.dtos;

public record DeleteUserRequest(
        String email,
        String deleteMessage
) {
}
