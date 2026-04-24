package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.BloqueEstudio;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.repository.BloqueEstudioRepository;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
	public String listarBloquesEstudio(Model model, @AuthenticationPrincipal Usuario usuarioAutenticado) {
		model.addAttribute("bloquesEstudio", bloqueEstudioRepository.findByUsuarioId(usuarioAutenticado.getId()));
		return "bloquesEstudio/BloqueEstudioListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(
			@RequestParam(required = false) Long cuadranteId,
			@RequestParam(required = false) String fecha,
			@RequestParam(required = false) String horaInicio,
			@RequestParam(required = false) String horaFin,
			Model model) {

		BloqueEstudio bloqueEstudio = new BloqueEstudio();

		if (fecha != null) bloqueEstudio.setFecha(LocalDate.parse(fecha));
		if (horaInicio != null) bloqueEstudio.setHoraInicio(LocalTime.parse(horaInicio));
		if (horaFin != null) bloqueEstudio.setHoraFin(LocalTime.parse(horaFin));

		model.addAttribute("bloqueEstudio", bloqueEstudio);
		model.addAttribute("asignaturas", asignaturaRepository.findAll());
		model.addAttribute("accion", "Añadir");
		return "bloquesEstudio/BloqueEstudioFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("bloqueEstudio") BloqueEstudio bloqueEstudio,
								BindingResult result, Model model,
								@RequestParam(required = false) Long cuadranteId,
								@AuthenticationPrincipal Usuario usuarioAutenticado) {

		if (bloqueEstudio.getFecha() != null
				&& bloqueEstudio.getHoraInicio() != null && bloqueEstudio.getHoraFin() != null) {
			List<BloqueEstudio> solapados = bloqueEstudioRepository
				.findByFechaAndHoraInicioLessThanAndHoraFinGreaterThan(
					bloqueEstudio.getFecha(),
					bloqueEstudio.getHoraInicio(),
					bloqueEstudio.getHoraFin()
				);
			if (!solapados.isEmpty()) {
				result.rejectValue("horaInicio", "error.bloqueEstudio", "Existe un bloque solapado en este horario.");
			}
		}

		if (result.hasErrors()) {
			model.addAttribute("asignaturas", asignaturaRepository.findAll());
			model.addAttribute("accion", "Añadir");
			return "bloquesEstudio/BloqueEstudioFormView";
		}

		resolverAsignatura(bloqueEstudio);
		bloqueEstudio.setUsuario(usuarioAutenticado);
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
								BindingResult result, Model model,
								@RequestParam(required = false) Long cuadranteId,
								@AuthenticationPrincipal Usuario usuarioAutenticado) {

		if (bloqueEstudio.getFecha() != null && bloqueEstudio.getHoraInicio() != null 
			&& bloqueEstudio.getHoraFin() != null) {
			List<BloqueEstudio> solapados = bloqueEstudioRepository
				.findByFechaAndHoraInicioLessThanAndHoraFinGreaterThan(
					bloqueEstudio.getFecha(),
					bloqueEstudio.getHoraInicio(),
					bloqueEstudio.getHoraFin()
				).stream()
				.filter(b -> !b.getId().equals(id))
				.toList();
			if (!solapados.isEmpty()) {
				result.rejectValue("horaInicio", "error.bloqueEstudio", "Existe un bloque solapado en este horario.");
			}
		}
		
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "bloquesEstudio/BloqueEstudioFormView";
		}
		bloqueEstudio.setId(id);
		resolverAsignatura(bloqueEstudio);
		bloqueEstudio.setUsuario(usuarioAutenticado);
		bloqueEstudioRepository.save(bloqueEstudio);
		return "redirect:/bloquesEstudio";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioAutenticado) {
		BloqueEstudio bloque = bloqueEstudioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de estudio no encontrado: " + id));
		if (!bloque.getUsuario().getId().equals(usuarioAutenticado.getId())) {
			return "redirect:/bloquesEstudio?error=forbidden";
		}
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

	@PostMapping("/bloquesEstudio/{id}/mover")
	@ResponseBody
	public ResponseEntity<?> moverBloque(@PathVariable Long id, @RequestBody Map<String, String> payload) {
		BloqueEstudio bloque = bloqueEstudioRepository.findById(id).orElseThrow();
		bloque.setFecha(LocalDate.parse(payload.get("start").substring(0,10)));
		bloque.setHoraInicio(LocalTime.parse(payload.get("start").substring(11,16)));
		if (payload.get("end") != null) {
			bloque.setHoraFin(LocalTime.parse(payload.get("end").substring(11,16)));
		}
		bloqueEstudioRepository.save(bloque);
		return ResponseEntity.ok().build();
	}
}
