package com.planner.spring_boot_planner.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planner.spring_boot_planner.entity.PasswordResetToken;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.PasswordResetTokenRepository;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;

    public PasswordResetTokenService(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public PasswordResetToken crearToken(Usuario usuario, String token) {
        // Invalida tokens anteriores del usuario
        tokenRepository.deleteByUsuario(usuario);

        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                usuario,
                LocalDateTime.now().plusMinutes(30));
        return tokenRepository.save(resetToken);
    }

    public Optional<PasswordResetToken> obtenerToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Transactional
    public void marcarComoUsado(PasswordResetToken token) {
        token.setUsado(true);
        tokenRepository.save(token);
    }

    @Transactional
    public void eliminarToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }
}
