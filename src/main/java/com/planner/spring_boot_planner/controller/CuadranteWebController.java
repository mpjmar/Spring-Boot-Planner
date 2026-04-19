package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.Cuadrante;
import com.planner.spring_boot_planner.entity.BloqueEstudio;
import com.planner.spring_boot_planner.repository.CuadranteRepository;
import com.planner.spring_boot_planner.repository.BloqueEstudioRepository;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cuadrantes")
public class CuadranteWebController {

	private final CuadranteRepository cuadranteRepository;
	private final BloqueEstudioRepository bloqueEstudioRepository;

	public CuadranteWebController(CuadranteRepository cuadranteRepository,
								  BloqueEstudioRepository bloqueEstudioRepository) {
		this.cuadranteRepository = cuadranteRepository;
		this.bloqueEstudioRepository = bloqueEstudioRepository;
	}

	@GetMapping
	public String listarCuadrantes(Model model) {
		model.addAttribute("cuadrantes", cuadranteRepository.findAll());
		return "cuadrantes/CuadrantesListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("cuadrante", new Cuadrante());
		model.addAttribute("bloquesEstudio", bloqueEstudioRepository.findAll());
		model.addAttribute("accion", "Añadir");
		return "cuadrantes/CuadranteFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("cuadrante") Cuadrante cuadrante,
								@RequestParam List<Long> bloquesEstudio,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("bloquesEstudio", bloqueEstudioRepository.findAll());
			model.addAttribute("accion", "Añadir");
			return "cuadrantes/CuadranteFormView";
		}
		List<BloqueEstudio> bloques = bloqueEstudioRepository.findAllById(bloquesEstudio);
		cuadrante.setBloquesEstudio(bloques);

		cuadranteRepository.save(cuadrante);
		return "redirect:/cuadrantes";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
		Cuadrante cuadrante = cuadranteRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Cuadrante no encontrado: " + id));
		model.addAttribute("cuadrante", cuadrante);
		model.addAttribute("bloquesEstudio", bloqueEstudioRepository.findAll());
		model.addAttribute("accion", "Editar");
		return "cuadrantes/CuadranteFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("cuadrante") Cuadrante cuadrante,
								@RequestParam List<Long> bloquesEstudio,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("bloquesEstudio", bloqueEstudioRepository.findAll());
			model.addAttribute("accion", "Editar");
			return "cuadrantes/CuadranteFormView";
		}
		cuadrante.setId(id);
		List<BloqueEstudio> bloques = bloqueEstudioRepository.findAllById(bloquesEstudio);
		cuadrante.setBloquesEstudio(bloques);

		cuadranteRepository.save(cuadrante);
		return "redirect:/cuadrantes";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, Model model) {
		cuadranteRepository.deleteById(id);
		return "redirect:/cuadrantes";
	}
}
