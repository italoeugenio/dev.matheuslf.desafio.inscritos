package dev.matheuslf.desafio.inscritos.models.service.email.implementation;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import dev.matheuslf.desafio.inscritos.models.dtos.EmailMessageDTO;
import dev.matheuslf.desafio.inscritos.models.service.email.EmailSenderService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "api.security.resend.token")
public class ResendEmailSender implements EmailSenderService {

    @Value("${api.security.resend.token}")
    private String secret;

    @Value("${api.security.resend.email}")
    private String email;

    @PostConstruct
    public void testarVariaveis() {
        System.out.println("=== TESTE RESEND CONFIG ===");
        System.out.println("Token carregado: " + (secret != null ? secret.substring(0, 3) + "..." : "NULL"));
        System.out.println("Email carregado: " + email);
        System.out.println("===========================");
    }


    @Override
    public void sendEmailVerificationCode(EmailMessageDTO message) {
        Resend resend = new Resend(secret);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(email)
                .to(message.to())
                .subject(message.subject())
                .html(message.body())
                .build();
        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
    }
}
