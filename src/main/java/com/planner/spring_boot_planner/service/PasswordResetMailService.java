package com.planner.spring_boot_planner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.planner.spring_boot_planner.entity.Usuario;

@Service
public class PasswordResetMailService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetMailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public PasswordResetMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreoRecuperacion(Usuario usuario, String token) throws MailException {
        String resetLink = baseUrl + "/restablecer-password?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(usuario.getEmail());
        mensaje.setSubject("Recuperación de contraseña");
        mensaje.setText(
                "Hola " + usuario.getNombre() + ",\n\n" +
                "Para restablecer tu contraseña, haz clic en el siguiente enlace:\n" +
                resetLink + "\n\n" +
                "Este enlace caduca en 30 minutos.");

        logger.info("Intentando enviar correo de recuperación a {} con token {}", usuario.getEmail(), token);
        logger.debug("URL de restablecimiento: {}", resetLink);
        
        try {
            mailSender.send(mensaje);
            logger.info("Correo de recuperación enviado exitosamente a {}", usuario.getEmail());
        } catch (MailException ex) {
            logger.error("ERROR al enviar correo de recuperación a {}. Causa: {}", 
                usuario.getEmail(), ex.getMessage(), ex);
            throw ex;
        }
    }
}