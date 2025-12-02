package dev.matheuslf.desafio.inscritos;

import dev.matheuslf.desafio.inscritos.models.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class InscritosApplication {
	@Autowired
	private EmailSenderService emailSenderService;

    public static void main(String[] args) {
        SpringApplication.run(InscritosApplication.class, args);
    }

	@EventListener(ApplicationReadyEvent.class)
	public void sendMail(){
		emailSenderService.sendEmail("italoeugenio539@gmail.com", "This is a test", "Esse é o conteudo ");
	}

}
