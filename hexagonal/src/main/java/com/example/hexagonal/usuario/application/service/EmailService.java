package com.example.hexagonal.usuario.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendConfirmationEmail(String email, String token) {
        String confirmationLink = "http://localhost:8081/usuarios/confirm?token=" + token;
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Confirmação de E-mail");
        mailMessage.setText("Clique no link para confirmar seu e-mail: " + confirmationLink);
        mailMessage.setFrom("noreply@seusite.com");
        javaMailSender.send(mailMessage);
    }

    public void sendPasswordResetEmail(String email, String token) {
        String resetLink = "http://localhost:8081/usuarios/reset-password?token=" + token;
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Recuperação de Senha");
        mailMessage.setText("Clique no link para redefinir sua senha: " + resetLink);
        mailMessage.setFrom("noreply@seusite.com");
        javaMailSender.send(mailMessage);
    }
}
