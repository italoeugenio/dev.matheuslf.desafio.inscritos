package dev.matheuslf.desafio.inscritos.models.service.email.implementation;

import dev.matheuslf.desafio.inscritos.models.dtos.EmailMessageDTO;
import dev.matheuslf.desafio.inscritos.models.service.email.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "spring.mail.host")
public class SmtpEmailSender implements EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmailVerificationCode(EmailMessageDTO messageDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@gmail.com");
        message.setTo(messageDTO.to());
        message.setText(messageDTO.body());
        message.setSubject(messageDTO.subject());
        mailSender.send(message);
    }
}
