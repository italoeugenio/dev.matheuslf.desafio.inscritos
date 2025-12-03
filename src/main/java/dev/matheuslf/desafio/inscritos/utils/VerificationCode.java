package dev.matheuslf.desafio.inscritos.utils;

import java.time.LocalDateTime;

public class VerificationCode {
    //Generate a code with sixDigit
    public static String generateVerificationCode(){
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    public static LocalDateTime codeExpiresAt (int timeMinutes){
        return LocalDateTime.now().plusHours(timeMinutes);
    }
}
