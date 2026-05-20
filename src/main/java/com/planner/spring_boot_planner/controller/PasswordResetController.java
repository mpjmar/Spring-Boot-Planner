package com.planner.spring_boot_planner.controller;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.planner.spring_boot_planner.entity.PasswordResetToken;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.UsuarioRepository;
import com.planner.spring_boot_planner.service.PasswordResetMailService;
import com.planner.spring_boot_planner.service.PasswordResetTokenService;

@Controller
public class PasswordResetController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final PasswordResetMailService passwordResetMailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetController(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenService passwordResetTokenService,
            PasswordResetMailService passwordResetMailService,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordResetTokenService = passwordResetTokenService;
        this.passwordResetMailService = passwordResetMailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/recuperar-password")
    public String mostrarFormularioRecuperar() {
        return "recuperarPassword";
    }

    @PostMapping("/recuperar-password")
    public String procesarRecuperarPassword(@RequestParam("email") String email, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            PasswordResetToken resetToken = null;

            try {
                String token = UUID.randomUUID().toString();
                resetToken = passwordResetTokenService.crearToken(usuario, token);

                passwordResetMailService.enviarCorreoRecuperacion(usuario, token);
                model.addAttribute("mensaje", "Si el email existe, recibirás un enlace para restablecer tu contraseña.");
            } catch (MailException ex) {
                logger.error("Error enviando correo de recuperación a {}", email, ex);
                if (resetToken != null) {
                    passwordResetTokenService.eliminarToken(resetToken);
                }
                model.addAttribute("error", "No se ha podido enviar el correo de recuperación. Revisa la configuración del email.");
            }
        } else {
            model.addAttribute("mensaje", "Si el email existe, recibirás un enlace para restablecer tu contraseña.");
        }
        return "recuperarPassword";
    }

    @GetMapping("/restablecer-password")
    public String mostrarFormularioRestablecer(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenService.obtenerToken(token);

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

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenService.obtenerToken(token);

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

        passwordResetTokenService.marcarComoUsado(resetToken);

        return "redirect:/login?resetSuccess";
    }
}