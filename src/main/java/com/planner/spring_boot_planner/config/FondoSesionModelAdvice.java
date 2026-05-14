package com.planner.spring_boot_planner.config;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.planner.spring_boot_planner.entity.Imagen;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.ImagenRepository;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class FondoSesionModelAdvice {

	private final ImagenRepository imagenRepository;

	public FondoSesionModelAdvice(ImagenRepository imagenRepository) {
		this.imagenRepository = imagenRepository;
	}

	/**
	 * Una URL de fondo por sesión, compartida por el dashboard y el resto de vistas autenticadas.
	 */
	@ModelAttribute("fondoSesion")
	public String fondoSesion(HttpSession session, @AuthenticationPrincipal Usuario usuario) {
		if (usuario == null) {
			return null;
		}
		List<String> urls = imagenRepository.findByUsuarioIdOrderByCreatedAtDesc(usuario.getId())
			.stream()
			.map(Imagen::getUrl)
			.toList();
		String fondo = (String) session.getAttribute("fondoSesion");
		if (fondo == null && !urls.isEmpty()) {
			fondo = urls.get(ThreadLocalRandom.current().nextInt(urls.size()));
			session.setAttribute("fondoSesion", fondo);
		}
		return fondo;
	}
}
