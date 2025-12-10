package dev.matheuslf.desafio.inscritos.models.service.email.implementation;

import dev.matheuslf.desafio.inscritos.models.dtos.EmailMessageDTO;
import dev.matheuslf.desafio.inscritos.models.service.email.EmailSenderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(SmtpEmailSender.class)
public class ConsoleEmailSenderService implements EmailSenderService {
    @Override
    public void sendEmailVerificationCode(EmailMessageDTO message) {
        System.out.println("\nDevelopment Email Transport:");
        System.out.println("=========================");
        System.out.println("To: " + message.to());
        System.out.println("Subject: " + message.subject());
        System.out.println("Body: " + message.body());
        System.out.println("=========================\n");
    }
}
