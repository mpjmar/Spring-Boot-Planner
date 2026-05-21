package com.planner.spring_boot_planner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.planner.spring_boot_planner.entity.Rol;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.UsuarioRepository;

@Service
public class RolRegistroService {

	private final String adminEmail;
	private final UsuarioRepository usuarioRepository;

	public RolRegistroService(@Value("${app.admin-email:}") String adminEmail,
							  UsuarioRepository usuarioRepository) {
		this.adminEmail = adminEmail;
		this.usuarioRepository = usuarioRepository;
	}

	/**
	 * Si el email coincide con el admin configurado y aún no es ADMIN, actualiza en BD (útil tras añadir APP_ADMIN_EMAIL).
	 */
	@Transactional
	public void promoverAdminSiCorresponde(Usuario usuario) {
		if (usuario == null || usuario.getId() == null) {
			return;
		}
		if (esEmailAdministrador(usuario.getEmail()) && usuario.getRol() != Rol.ADMIN) {
			int updated = usuarioRepository.updateRolById(usuario.getId(), Rol.ADMIN);
			if (updated > 0) {
				usuario.setRol(Rol.ADMIN);
			}
		}
	}

	/**
	 * Asigna ADMIN si el email coincide con {@code app.admin-email} (p. ej. variable APP_ADMIN_EMAIL en Render).
	 */
	public void aplicarRolAlRegistro(Usuario usuario) {
		if (usuario == null) {
			return;
		}
		if (esEmailAdministrador(usuario.getEmail())) {
			usuario.setRol(Rol.ADMIN);
		} else {
			usuario.setRol(Rol.USER);
		}
	}

	public boolean esEmailAdministrador(String email) {
		if (!StringUtils.hasText(email) || !StringUtils.hasText(adminEmail)) {
			return false;
		}
		return adminEmail.trim().equalsIgnoreCase(email.trim());
	}
}
