package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.models.dtos.EmailMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailVerificationCode(EmailMessageDTO registerDTO){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@gmail.com");
        message.setTo(registerDTO.to());
        message.setText(registerDTO.body());
        message.setSubject(registerDTO.subject());

        mailSender.send(message);
    }
}
