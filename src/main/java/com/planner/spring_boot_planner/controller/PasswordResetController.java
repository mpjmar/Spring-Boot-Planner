package com.planner.spring_boot_planner.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.planner.spring_boot_planner.entity.PasswordResetToken;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.PasswordResetTokenRepository;
import com.planner.spring_boot_planner.repository.UsuarioRepository;

@Controller
public class PasswordResetController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    public PasswordResetController(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository tokenRepository,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/recuperar-password")
    public String procesarRecuperarPassword(@RequestParam("email") String email, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Invalida tokens anteriores del usuario
            tokenRepository.deleteByUsuario(usuario);

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(
                    token,
                    usuario,
                    LocalDateTime.now().plusMinutes(30));
            tokenRepository.save(resetToken);

            String resetLink = baseUrl + "/restablecer-password?token=" + token;

            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(usuario.getEmail());
            mensaje.setSubject("Recuperación de contraseña");
            mensaje.setText(
                    "Hola " + usuario.getNombre() + ",\n\n" +
                    "Para restablecer tu contraseña, haz clic en el siguiente enlace:\n" +
                    resetLink + "\n\n" +
                    "Este enlace caduca en 30 minutos.");

            mailSender.send(mensaje);
        }

        model.addAttribute("mensaje", "Si el email existe, recibirás un enlace para restablecer tu contraseña.");
        return "recuperarPassword";
    }

    @GetMapping("/restablecer-password")
    public String mostrarFormularioRestablecer(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().isUsado() || tokenOpt.get().estaExpirado()) {
            model.addAttribute("error", "El enlace no es válido o ha expirado.");
            return "recuperarPassword";
        }

        model.addAttribute("token", token);
        return "restablecerPassword";
    }

    @PostMapping("/restablecer-password")
    public String procesarRestablecerPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("repitePassword") String repitePassword,
            Model model) {

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().isUsado() || tokenOpt.get().estaExpirado()) {
            model.addAttribute("error", "El enlace no es válido o ha expirado.");
            return "recuperarPassword";
        }

        if (!password.equals(repitePassword)) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "restablecerPassword";
        }

        PasswordResetToken resetToken = tokenOpt.get();
        Usuario usuario = resetToken.getUsuario();

        usuario.setPassword(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);

        resetToken.setUsado(true);
        tokenRepository.save(resetToken);

        return "redirect:/login?resetSuccess";
    }
}