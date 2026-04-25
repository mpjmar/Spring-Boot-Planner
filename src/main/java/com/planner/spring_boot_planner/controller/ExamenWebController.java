package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.Examen;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.repository.ExamenRepository;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
	public String listarExamenes(Model model, @AuthenticationPrincipal Usuario usuario) {
		model.addAttribute("examenes", examenRepository.findByUsuarioId(usuario.getId()));
		return "examenes/ExamenListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model, @AuthenticationPrincipal Usuario usuario) {
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
		resolverAsignatura(examen);
		examen.setUsuario(usuario);
		examenRepository.save(examen);
		return "redirect:/examenes";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model, 
										  @AuthenticationPrincipal Usuario usuario) {
		Examen examen = examenRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Examen no encontrado: " + id));
		if (examen.getUsuario().getId() != usuario.getId())
			return "redirect:/examenes";
		model.addAttribute("examen", examen);
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
		model.addAttribute("accion", "Editar");
		return "examenes/ExamenFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("examen") Examen examen,
								BindingResult result, Model model, 
								@AuthenticationPrincipal Usuario usuario) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "examenes/ExamenFormView";
		}
		if (examen.getUsuario().getId() != usuario.getId())
			return "redirect:/examenes";
		examen.setId(id);
		resolverAsignatura(examen);
		examenRepository.save(examen);
		return "redirect:/examenes";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
		Examen examen = examenRepository.findById(id).orElse(null);
		if (examen == null)
			return "redirect:/examenes?error=forbidden";
		examenRepository.deleteById(id);
		return "redirect:/examenes";
	}


	private void resolverAsignatura(Examen examen) {
		if (examen.getAsignatura() != null && examen.getAsignatura().getId() != null) {
			Asignatura asignatura = asignaturaRepository.findById(examen.getAsignatura().getId())
				.orElse(null);
			examen.setAsignatura(asignatura);
		} else {
			examen.setAsignatura(null);
		}
	}
}
