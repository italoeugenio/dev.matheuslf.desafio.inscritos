package dev.matheuslf.desafio.inscritos.models.service.email;

import dev.matheuslf.desafio.inscritos.models.dtos.EmailMessageDTO;

public interface EmailSenderService {
    void sendEmailVerificationCode(EmailMessageDTO message);
}
