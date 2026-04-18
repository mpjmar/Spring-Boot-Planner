package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.BloqueEstudio;
import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.repository.BloqueEstudioRepository;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bloquesEstudio")
public class BloqueEstudioWebController {

	private final BloqueEstudioRepository bloqueEstudioRepository;
	private final AsignaturaRepository asignaturaRepository;

	public BloqueEstudioWebController(BloqueEstudioRepository bloqueEstudioRepository,
									  AsignaturaRepository asignaturaRepository) {
		this.bloqueEstudioRepository = bloqueEstudioRepository;
		this.asignaturaRepository = asignaturaRepository;
	}

	@GetMapping
	public String listarBloquesEstudio(Model model) {
		model.addAttribute("bloquesEstudio", bloqueEstudioRepository.findAll());
		return "bloquesEstudio/BloqueEstudioListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("bloqueEstudio", new BloqueEstudio());
		model.addAttribute("asignaturas", asignaturaRepository.findAll());
		model.addAttribute("accion", "Añadir");
		return "bloquesEstudio/BloqueEstudioFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("bloqueEstudio") BloqueEstudio bloqueEstudio,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("asignaturas", asignaturaRepository.findAll());
			model.addAttribute("accion", "Añadir");
			return "bloquesEstudio/BloqueEstudioFormView";
		}
		resolverAsignatura(bloqueEstudio);
		bloqueEstudioRepository.save(bloqueEstudio);
		return "redirect:/bloquesEstudio";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
		BloqueEstudio bloqueEstudio = bloqueEstudioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de estudio no encontrado: " + id));
		model.addAttribute("bloqueEstudio", bloqueEstudio);
		model.addAttribute("asignaturas", asignaturaRepository.findAll());
		model.addAttribute("accion", "Editar");
		return "bloquesEstudio/BloqueEstudioFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable long id,
								@Valid @ModelAttribute("bloqueEstudio") BloqueEstudio bloqueEstudio,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "bloquesEstudio/BloquesEstudioFormView";
		}
		bloqueEstudio.setId(id);
		resolverAsignatura(bloqueEstudio);
		bloqueEstudioRepository.save(bloqueEstudio);
		return "redirect:/bloquesEstudio";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id) {
		bloqueEstudioRepository.deleteById(id);
		return "redirect:/bloquesEstudio";
	}


	private void resolverAsignatura(BloqueEstudio bloqueEstudio) {
		if (bloqueEstudio.getAsignatura() != null && bloqueEstudio.getAsignatura().getId() != null) {
			Asignatura asignatura = asignaturaRepository.findById(bloqueEstudio.getAsignatura().getId())
				.orElse(null);
			bloqueEstudio.setAsignatura(asignatura);
		} else {
			bloqueEstudio.setAsignatura(null);
		}
	}
}
