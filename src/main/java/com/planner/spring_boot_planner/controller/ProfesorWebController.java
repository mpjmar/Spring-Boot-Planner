package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.Profesor;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.ProfesorRepository;
import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller 
@RequestMapping("/profesores")
public class ProfesorWebController {

	private final ProfesorRepository profesorRepository;

	public ProfesorWebController(ProfesorRepository profesorRepository) {
		this.profesorRepository = profesorRepository;
	}

	@GetMapping
	public String listarProfesores(Model model, @AuthenticationPrincipal Usuario usuario) {
		model.addAttribute("profesores", profesorRepository.findByUsuarioId(usuario.getId()));
		return "profesores/ProfesorListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model, @AuthenticationPrincipal Usuario usuario) {
		model.addAttribute("profesor", new Profesor());
		model.addAttribute("accion", "Añadir");
		return "profesores/ProfesorFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("profesor") Profesor profesor,
								BindingResult result, Model model, 
								@AuthenticationPrincipal Usuario usuario) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Añadir");
			return "profesores/ProfesorFormView";
		}
		profesor.setUsuario(usuario);
		profesorRepository.save(profesor);
		return "redirect:/profesores";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model, 
										  @AuthenticationPrincipal Usuario usuario) {
		Profesor profesor = profesorRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado: " + id));
		if (!profesor.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/profesores";
		model.addAttribute("profesor", profesor);
		model.addAttribute("accion", "Editar");
		return "profesores/ProfesorFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("profesor") Profesor profesor,
								BindingResult result, Model model, 
								@AuthenticationPrincipal Usuario usuario) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "profesores/ProfesorFormView";
		}
		if (!profesor.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/profesores";
		profesor.setId(id);
		profesorRepository.save(profesor);
		return "redirect:/profesores";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, 
						   @AuthenticationPrincipal Usuario usuario) {
		Profesor profesor = profesorRepository.findById(id).orElse(null);
		if (profesor == null || !profesor.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/profesores?error=forbidden";
		profesorRepository.deleteById(id);
		return "redirect:/profesores";
	}
	
}
