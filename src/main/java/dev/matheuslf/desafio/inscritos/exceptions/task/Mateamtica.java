package dev.matheuslf.desafio.inscritos.exceptions.task;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Mateamtica {
    public static void main(String[] args) {
        var senha  = "12345678wW@";
        String hash = new BCryptPasswordEncoder().encode(senha);
        System.out.println(hash);
    }
}
