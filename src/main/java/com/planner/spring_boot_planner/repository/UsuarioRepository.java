package com.planner.spring_boot_planner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.planner.spring_boot_planner.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@RestResource(path = "por-email", rel = "por-email")
	Optional<Usuario> findByEmail(String email);
	
}
