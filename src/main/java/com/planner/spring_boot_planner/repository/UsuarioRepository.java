package com.planner.spring_boot_planner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.planner.spring_boot_planner.entity.Rol;
import com.planner.spring_boot_planner.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@RestResource(path = "por-email", rel = "por-email")
	Optional<Usuario> findByEmail(String email);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Usuario u SET u.rol = :rol WHERE u.id = :id AND (u.rol IS NULL OR u.rol <> :rol)")
	int updateRolById(@Param("id") Long id, @Param("rol") Rol rol);
}
