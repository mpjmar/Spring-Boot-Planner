package com.planner.spring_boot_planner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planner.spring_boot_planner.entity.PasswordResetToken;
import com.planner.spring_boot_planner.entity.Usuario;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUsuario(Usuario usuario);
}
