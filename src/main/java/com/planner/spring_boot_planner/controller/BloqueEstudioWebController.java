package com.planner.spring_boot_planner.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.entity.BloqueEstudio;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import com.planner.spring_boot_planner.repository.BloqueEstudioRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/bloquesEstudio")
public class BloqueEstudioWebController {

	private static final String COLOR_DEFECTO = "#4A5568";

	private final BloqueEstudioRepository bloqueEstudioRepository;
	private final AsignaturaRepository asignaturaRepository;

	public BloqueEstudioWebController(BloqueEstudioRepository bloqueEstudioRepository,
									  AsignaturaRepository asignaturaRepository) {
		this.bloqueEstudioRepository = bloqueEstudioRepository;
		this.asignaturaRepository = asignaturaRepository;
	}

	@GetMapping
	public String listarBloquesEstudio(@AuthenticationPrincipal Usuario usuario, Model model) {
		LocalDate hoy = LocalDate.now();

		List<BloqueEstudio> bloques = bloqueEstudioRepository.findByUsuarioId(usuario.getId())
			.stream()
			.filter(bloque -> !bloque.getFecha().isBefore(hoy))
			.sorted(
				Comparator.comparing(BloqueEstudio::getFecha)
						.thenComparing(BloqueEstudio::getHoraInicio)
			)
			.toList();

		model.addAttribute("bloquesEstudio", bloques);
		return "bloquesEstudio/BloqueEstudioListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(
			@RequestParam(required = false) String fecha,
			@RequestParam(required = false) String horaInicio,
			@RequestParam(required = false) String horaFin,
			Model model, @AuthenticationPrincipal Usuario usuario) {

		BloqueEstudio bloqueEstudio = new BloqueEstudio();

		if (fecha != null) 
			bloqueEstudio.setFecha(LocalDate.parse(fecha));
		if (horaInicio != null) 
			bloqueEstudio.setHoraInicio(LocalTime.parse(horaInicio));
		if (horaFin != null) 
			bloqueEstudio.setHoraFin(LocalTime.parse(horaFin));

		model.addAttribute("bloqueEstudio", bloqueEstudio);
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
		model.addAttribute("accion", "Añadir");
		return "bloquesEstudio/BloqueEstudioFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("bloqueEstudio") BloqueEstudio bloqueEstudio,
								BindingResult result, Model model,
								@AuthenticationPrincipal Usuario usuario) {
		resolverAsignatura(bloqueEstudio, usuario);
		resolverColor(bloqueEstudio);
		validarSolapados(null, bloqueEstudio, usuario.getId(), result);
		if (result.hasErrors()) {
			model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
			model.addAttribute("accion", "Añadir");
			return "bloquesEstudio/BloqueEstudioFormView";
		}
		bloqueEstudio.setUsuario(usuario);
		bloqueEstudioRepository.save(bloqueEstudio);
		return "redirect:/bloquesEstudio";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model, 
										  @AuthenticationPrincipal Usuario usuario) {
		BloqueEstudio bloqueEstudio = bloqueEstudioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de estudio no encontrado: " + id));
		if (!bloqueEstudio.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/bloquesEstudio";
		model.addAttribute("bloqueEstudio", bloqueEstudio);
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
		model.addAttribute("accion", "Editar");
		return "bloquesEstudio/BloqueEstudioFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable long id,
								@Valid @ModelAttribute("bloqueEstudio") BloqueEstudio bloqueEstudio,
								BindingResult result, Model model,
								@AuthenticationPrincipal Usuario usuario) {
		BloqueEstudio existente = bloqueEstudioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de estudios no encontrado: " + id));
		if (!existente.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/bloquesEstudio";
		bloqueEstudio.setId(id);
		resolverAsignatura(bloqueEstudio, usuario);
		resolverColor(bloqueEstudio);
		validarSolapados(id, bloqueEstudio, usuario.getId(), result);
		if (result.hasErrors()) {
			model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
			model.addAttribute("accion", "Editar");
			return "bloquesEstudio/BloqueEstudioFormView";
		}
		bloqueEstudio.setUsuario(usuario);
		bloqueEstudioRepository.save(bloqueEstudio);
		return "redirect:/bloquesEstudio";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, 
						   @AuthenticationPrincipal Usuario usuario) {
		BloqueEstudio bloqueEstudio = bloqueEstudioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de estudio no encontrado: " + id));
		if (!bloqueEstudio.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/bloquesEstudio";
		bloqueEstudioRepository.deleteById(id);
		return "redirect:/bloquesEstudio";
	}

	@GetMapping("/dia")
	public String verDia(@RequestParam(required = false) String fecha, Model model, 
						 @AuthenticationPrincipal Usuario usuario) {
		LocalDate dia = fecha == null ? LocalDate.now() : LocalDate.parse(fecha);
		List<BloqueEstudio> bloquesEstudio = bloqueEstudioRepository.findByFechaAndUsuarioId(dia, usuario.getId());
		bloquesEstudio.sort(Comparator.comparing(BloqueEstudio::getHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())));
		model.addAttribute("bloquesEstudio", bloquesEstudio);
		model.addAttribute("fecha", dia);
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
		return "bloquesEstudio/BloqueEstudioDayView";
	}

	@PostMapping("/{id}/mover")
	@ResponseBody
	public ResponseEntity<Void> moverBloque(@PathVariable Long id, 
										 @RequestBody Map<String, String> payload, 
										 @AuthenticationPrincipal Usuario usuario) {
		BloqueEstudio bloqueEstudio = bloqueEstudioRepository.findById(id).orElse(null);
		if (bloqueEstudio == null || !bloqueEstudio.getUsuario().getId().equals(usuario.getId()))
			return ResponseEntity.status(403).build();
		String start = payload.get("start");
		if (start == null)
			return ResponseEntity.badRequest().build();
		OffsetDateTime odt = OffsetDateTime.parse(start);
		ZonedDateTime z = odt.atZoneSameInstant(ZoneId.systemDefault());
		bloqueEstudio.setFecha(z.toLocalDate());
		bloqueEstudio.setHoraInicio(z.toLocalTime().withSecond(0).withNano(0));
		if (payload.get("end") != null) {
			OffsetDateTime odtEnd = OffsetDateTime.parse(payload.get("end"));
			bloqueEstudio.setHoraFin(odtEnd.atZoneSameInstant(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0));
		}
		if (bloqueEstudio.getAsignatura() != null)
			bloqueEstudio.setColor(bloqueEstudio.getAsignatura().getColor() != null
								   ? bloqueEstudio.getAsignatura().getColor() : COLOR_DEFECTO);
		else
			bloqueEstudio.setColor(COLOR_DEFECTO);
		bloqueEstudioRepository.save(bloqueEstudio);
		return ResponseEntity.ok().build();
	}

	private void validarSolapados(Long idExcluido, BloqueEstudio bloqueEstudio,
								  Long usuarioId, BindingResult result) {
		if (bloqueEstudio.getFecha() == null) {
			result.rejectValue("fecha", "error.bloqueEstudio", "Indica la fecha del bloque.");
			return;
		}
		if (bloqueEstudio.getHoraInicio() == null || bloqueEstudio.getHoraFin() == null) {
			if (bloqueEstudio.getHoraInicio() == null)
				result.rejectValue("horaInicio", "error.bloqueEstudio", "Indica la hora de incio.");
			else
				result.rejectValue("horaFin", "error.bloqueEstudio", "Indica la hora de fin.");
			return;
		}
			
		List<BloqueEstudio> solapados = bloqueEstudioRepository.findSolapadosPorUsuario(
			bloqueEstudio.getFecha(), usuarioId, bloqueEstudio.getHoraInicio(), bloqueEstudio.getHoraFin());
		boolean haySolape = idExcluido == null ? !solapados.isEmpty()
			: solapados.stream().anyMatch(s -> !s.getId().equals(idExcluido));
		if (haySolape)
			result.rejectValue("horaInicio", "error.bloqueEstudio", "Existe un bloque solapado en este horario.");
	}

	private void resolverAsignatura(BloqueEstudio bloqueEstudio, Usuario usuario) {
		if (bloqueEstudio.getAsignatura() != null && bloqueEstudio.getAsignatura().getId() != null) {
			Asignatura asignatura = asignaturaRepository.findById(bloqueEstudio.getAsignatura().getId())
				.filter(a -> a.getUsuario() != null && a.getUsuario().getId().equals(usuario.getId()))
				.orElse(null);
			bloqueEstudio.setAsignatura(asignatura);
		} else {
			bloqueEstudio.setAsignatura(null);
		}
	}

	private void resolverColor(BloqueEstudio bloqueEstudio) {
		if (bloqueEstudio.getAsignatura() != null && bloqueEstudio.getAsignatura().getColor() != null)
			bloqueEstudio.setColor(bloqueEstudio.getAsignatura().getColor());
		else
			bloqueEstudio.setColor(COLOR_DEFECTO);
	}

}
