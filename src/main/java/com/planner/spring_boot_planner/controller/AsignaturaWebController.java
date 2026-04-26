package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.entity.Profesor;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import com.planner.spring_boot_planner.repository.ProfesorRepository;
import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller 
@RequestMapping("/asignaturas")
public class AsignaturaWebController {

	private final AsignaturaRepository asignaturaRepository;
	private final ProfesorRepository profesorRepository;

	public AsignaturaWebController(AsignaturaRepository asignaturaRepository, 
								   ProfesorRepository profesorRepository) {
		this.asignaturaRepository = asignaturaRepository;
		this.profesorRepository = profesorRepository;
	}

	@GetMapping
	public String listarAsignaturas(@AuthenticationPrincipal Usuario usuario, Model model) {
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
		return "asignaturas/AsignaturaListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(@AuthenticationPrincipal Usuario usuario, Model model) {
		model.addAttribute("asignatura", new Asignatura());
		model.addAttribute("profesores", profesorRepository.findByUsuarioId(usuario.getId()));
		model.addAttribute("accion", "Añadir");
		return "asignaturas/AsignaturaFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("asignatura") Asignatura asignatura,
								BindingResult result, Model model, 
								@AuthenticationPrincipal Usuario usuario) {
		if (result.hasErrors()) {
			model.addAttribute("profesores", profesorRepository.findByUsuarioId(usuario.getId()));
			model.addAttribute("accion", "Añadir");
			return "asignaturas/AsignaturaFormView";
		}
		resolverProfesor(asignatura, usuario);
		asignatura.setUsuario(usuario);
		asignaturaRepository.save(asignatura);
		return "redirect:/asignaturas";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model, 
										  @AuthenticationPrincipal Usuario usuario) {
		Asignatura asignatura = asignaturaRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Asignatura no encontrada: " + id));
		if (asignatura == null || !asignatura.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/asignaturas";
		model.addAttribute("asignatura", asignatura);
		model.addAttribute("profesores", profesorRepository.findByUsuarioId(usuario.getId()));
		model.addAttribute("accion", "Editar");
		return "asignaturas/AsignaturaFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("asignatura") Asignatura asignatura,
								BindingResult result, Model model, 
								@AuthenticationPrincipal Usuario usuario) {
		Asignatura existente = asignaturaRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Asignatura no encontrada: " + id));
		if (existente == null || !existente.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/asignaturas";
		if (result.hasErrors()) {
			model.addAttribute("profesores", profesorRepository.findByUsuarioId(usuario.getId()));
			model.addAttribute("accion", "Editar");
			return "asignaturas/AsignaturaFormView";
		}
		asignatura.setId(id);
		asignatura.setUsuario(usuario);
		resolverProfesor(asignatura, usuario);
		asignaturaRepository.save(asignatura);
		return "redirect:/asignaturas";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, 
						   @AuthenticationPrincipal Usuario usuario) {
		Asignatura asignatura = asignaturaRepository.findById(id).orElse(null);
		if (asignatura == null || asignatura.getUsuario() == null || 
			!asignatura.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/asignaturas";
		asignaturaRepository.deleteById(id);
		return "redirect:/asignaturas";
	}

	private void resolverProfesor(Asignatura asignatura, Usuario usuario) {
		if (asignatura.getProfesor() != null && asignatura.getProfesor().getId() != null) {
			Profesor profesor = profesorRepository.findById(asignatura.getProfesor().getId())
				.filter(p -> p.getUsuario() != null && p.getUsuario().getId().equals(usuario.getId()))
				.orElse(null);
			asignatura.setProfesor(profesor);
		} else {
			asignatura.setProfesor(null);
		}
	}
}
