package com.planner.spring_boot_planner.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.entity.Examen;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import com.planner.spring_boot_planner.repository.ExamenRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/examenes")
public class ExamenWebController {

	private final ExamenRepository examenRepository;
	private final AsignaturaRepository asignaturaRepository;

	public ExamenWebController(ExamenRepository examenRepository,
							   AsignaturaRepository asignaturaRepository) {
		this.examenRepository = examenRepository;
		this.asignaturaRepository = asignaturaRepository;
	}

	@GetMapping
	public String listarExamenes(@AuthenticationPrincipal Usuario usuario, Model model) {
		model.addAttribute("examenes", examenRepository.findByUsuarioId(usuario.getId()));
		return "examenes/ExamenListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(@AuthenticationPrincipal Usuario usuario, Model model) {
		model.addAttribute("examen", new Examen());
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
		model.addAttribute("accion", "Añadir");
		return "examenes/ExamenFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("examen") Examen examen,
								BindingResult result, Model model, 
								@AuthenticationPrincipal Usuario usuario) {
		if (result.hasErrors()) {
			model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
			model.addAttribute("accion", "Añadir");
			return "examenes/ExamenFormView";
		}
		examen.setUsuario(usuario);
		resolverAsignatura(examen, usuario);
		examenRepository.save(examen);
		return "redirect:/examenes";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model, 
										  @AuthenticationPrincipal Usuario usuario) {
		Examen examen = examenRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Examen no encontrado: " + id));
		if (examen.getUsuario() == null || !examen.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/examenes";
		model.addAttribute("examen", examen);
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
		model.addAttribute("accion", "Editar");
		return "examenes/ExamenFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable("id") Long id,
								@Valid @ModelAttribute("examen") Examen examen,
								BindingResult result, Model model, 
								@AuthenticationPrincipal Usuario usuario) {
		Examen existente = examenRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Examen no encontrado: " + id));
		if (existente == null || !existente.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/examenes";
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "examenes/ExamenFormView";
		}
		examen.setId(id);
		examen.setUsuario(usuario);
		resolverAsignatura(examen, usuario);
		examenRepository.save(examen);
		return "redirect:/examenes";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable("id") Long id, 
						   @AuthenticationPrincipal Usuario usuario) {
		Examen examen = examenRepository.findById(id).orElse(null);
		if (examen == null || examen.getUsuario() == null || 
			!examen.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/examenes";
		examenRepository.deleteById(id);
		return "redirect:/examenes";
	}


	private void resolverAsignatura(Examen examen, Usuario usuario) {
		if (examen.getAsignatura() != null && examen.getAsignatura().getId() != null) {
			Asignatura asignatura = asignaturaRepository.findById(examen.getAsignatura().getId())
				.filter(a -> a.getUsuario() != null && a.getUsuario().getId().equals(usuario.getId()))
				.orElse(null);
			examen.setAsignatura(asignatura);
		} else {
			examen.setAsignatura(null);
		}
	}
}
