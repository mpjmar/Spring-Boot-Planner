package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.Cuadrante;
import com.planner.spring_boot_planner.entity.BloqueEstudio;
import com.planner.spring_boot_planner.repository.CuadranteRepository;
import com.planner.spring_boot_planner.repository.BloqueEstudioRepository;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@GetMapping("/cuadrante/{id}/bloques-json")
	@ResponseBody
	public List<Map<String, Object>> getBloquesJson(@PathVariable Long id) {
		List<BloqueEstudio> bloques = bloqueEstudioRepository.findByCuadranteId(id);
		List<Map<String, Object>> eventos = new ArrayList<>();
		for (BloqueEstudio b : bloques) {
			Map<String, Object> evento = new HashMap<>();
			evento.put("id", b.getId());
			evento.put("title", b.getAsignatura() != null ? b.getAsignatura().getNombre() : "Bloque");
			evento.put("start", b.getFecha() + "T" + b.getHoraInicio());
			evento.put("end", b.getFecha() + "T" + b.getHoraFin());
			evento.put("color", b.getAsignatura() != null ? b.getAsignatura().getColor() : "#b3e0ff");
			eventos.add(evento);
		}
		return eventos;
	}
}
