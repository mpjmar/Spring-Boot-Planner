package com.planner.spring_boot_planner.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
	public String listarBloquesEstudio(@AuthenticationPrincipal Usuario usuario,
									   @RequestParam(value = "weekStart", required = false) String weekStartParam,
									   Model model) {
		LocalDate hoy = LocalDate.now();
		LocalDate inicioSemana = resolverInicioSemana(weekStartParam, hoy);
		LocalDate finSemana = inicioSemana.plusDays(6);

		List<BloqueEstudio> bloques = bloqueEstudioRepository.findByUsuarioId(usuario.getId())
			.stream()
			.filter(bloque -> bloque.getFecha() != null
							&& !bloque.getFecha().isBefore(inicioSemana)
							&& !bloque.getFecha().isAfter(finSemana))
			.sorted(
				Comparator.comparing(BloqueEstudio::getFecha)
						.thenComparing(BloqueEstudio::getHoraInicio)
			)
			.toList();

		List<LocalDate> diasSemana = new ArrayList<>();
		List<DiaConBloques> diasConBloques = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			LocalDate dia = inicioSemana.plusDays(i);
			diasSemana.add(dia);
			List<BloqueEstudio> bloquesDia = bloques.stream()
				.filter(bloque -> dia.equals(bloque.getFecha()))
				.toList();
			diasConBloques.add(new DiaConBloques(dia, bloquesDia));
		}
		List<FilaBloqueSemana> filasSemana = construirFilasSemana(diasSemana, bloques);
		List<DiaBloqueSemana> diasCuadrante = construirDiasCuadrante(diasSemana, filasSemana);

		model.addAttribute("bloquesEstudio", bloques);
		model.addAttribute("diasSemana", diasSemana);
		model.addAttribute("diasConBloques", diasConBloques);
		model.addAttribute("filasSemana", filasSemana);
		model.addAttribute("diasCuadrante", diasCuadrante);
		model.addAttribute("inicioSemana", inicioSemana);
		model.addAttribute("finSemana", finSemana);
		model.addAttribute("semanaAnterior", inicioSemana.minusWeeks(1));
		model.addAttribute("semanaSiguiente", inicioSemana.plusWeeks(1));
		model.addAttribute("hoy", hoy);
		return "bloquesEstudio/BloqueEstudioListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(
			@RequestParam(value = "fecha",required = false) String fecha,
			@RequestParam(value = "horaInicio", required = false) String horaInicio,
			@RequestParam(value = "horaFin", required = false) String horaFin,
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
		return "redirect:/bloquesEstudio?weekStart=" + bloqueEstudio.getFecha();
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model, 
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
	public String guardarEdicion(@PathVariable("id") Long id,
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
		return "redirect:/bloquesEstudio?weekStart=" + bloqueEstudio.getFecha();
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable("id") Long id, 
						   @AuthenticationPrincipal Usuario usuario) {
		BloqueEstudio bloqueEstudio = bloqueEstudioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de estudio no encontrado: " + id));
		if (!bloqueEstudio.getUsuario().getId().equals(usuario.getId()))
			return "redirect:/bloquesEstudio";
		LocalDate fechaBloque = bloqueEstudio.getFecha();
		bloqueEstudioRepository.deleteById(id);
		return "redirect:/bloquesEstudio?weekStart=" + fechaBloque;
	}

	@GetMapping("/dia")
	public String verDia(@RequestParam(value = "fecha", required = false) String fecha, 
						 Model model, @AuthenticationPrincipal Usuario usuario) {
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
	public ResponseEntity<Void> moverBloque(@PathVariable("id") Long id, 
										 @RequestBody Map<String, String> payload, 
										 @AuthenticationPrincipal Usuario usuario) {
		BloqueEstudio bloqueEstudio = bloqueEstudioRepository.findById(id).orElse(null);
		if (bloqueEstudio == null || !bloqueEstudio.getUsuario().getId().equals(usuario.getId()))
			return ResponseEntity.status(403).build();
		String fechaDestino = payload.get("fecha");
		String start = payload.get("start");
		if (fechaDestino != null && !fechaDestino.isBlank()) {
			bloqueEstudio.setFecha(LocalDate.parse(fechaDestino));
		} else if (start != null && !start.isBlank()) {
			LocalDate nuevaFecha = LocalDate.parse(start.substring(0, 10));
			bloqueEstudio.setFecha(nuevaFecha);
			if (start.length() >= 16) {
				bloqueEstudio.setHoraInicio(LocalTime.parse(start.substring(11, 16)));
			}
			String end = payload.get("end");
			if (end != null && end.length() >= 16) {
				bloqueEstudio.setHoraFin(LocalTime.parse(end.substring(11, 16)));
			}
		} else {
			return ResponseEntity.badRequest().build();
		}

		List<BloqueEstudio> solapados = bloqueEstudioRepository.findSolapadosPorUsuario(
			bloqueEstudio.getFecha(), usuario.getId(), bloqueEstudio.getHoraInicio(), bloqueEstudio.getHoraFin());
		boolean haySolape = solapados.stream().anyMatch(s -> !s.getId().equals(bloqueEstudio.getId()));
		if (haySolape) {
			return ResponseEntity.badRequest().build();
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

	private LocalDate resolverInicioSemana(String weekStartParam, LocalDate fallback) {
		LocalDate fechaBase = fallback;
		if (weekStartParam != null && !weekStartParam.isBlank()) {
			try {
				fechaBase = LocalDate.parse(weekStartParam);
			} catch (DateTimeParseException ignored) {
				fechaBase = fallback;
			}
		}
		return fechaBase.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}

	private List<FilaBloqueSemana> construirFilasSemana(List<LocalDate> diasSemana, List<BloqueEstudio> bloques) {
		TreeSet<LocalTime> marcas = new TreeSet<>();
		for (int hora = 8; hora <= 22; hora++) {
			marcas.add(LocalTime.of(hora, 0));
		}
		for (BloqueEstudio bloque : bloques) {
			if (bloque.getHoraInicio() != null) {
				marcas.add(bloque.getHoraInicio());
			}
			if (bloque.getHoraFin() != null) {
				marcas.add(bloque.getHoraFin());
			}
		}

		List<LocalTime> listaMarcas = new ArrayList<>(marcas);
		List<FilaBloqueSemana> filas = new ArrayList<>();
		for (int i = 0; i < listaMarcas.size() - 1; i++) {
			LocalTime inicio = listaMarcas.get(i);
			LocalTime fin = listaMarcas.get(i + 1);
			List<CeldaBloqueSemana> celdas = new ArrayList<>();

			for (LocalDate dia : diasSemana) {
				BloqueEstudio bloque = bloques.stream()
					.filter(b -> dia.equals(b.getFecha()) && inicio.equals(b.getHoraInicio()))
					.findFirst()
					.orElse(null);
				celdas.add(new CeldaBloqueSemana(dia, inicio.toString(), fin.toString(), bloque));
			}
			filas.add(new FilaBloqueSemana(inicio.toString(), fin.toString(), celdas));
		}

		return filas;
	}

	private List<DiaBloqueSemana> construirDiasCuadrante(List<LocalDate> diasSemana, List<FilaBloqueSemana> filasSemana) {
		List<DiaBloqueSemana> diasCuadrante = new ArrayList<>();
		for (LocalDate dia : diasSemana) {
			List<CeldaBloqueSemana> celdasDia = new ArrayList<>();
			for (FilaBloqueSemana fila : filasSemana) {
				for (CeldaBloqueSemana celda : fila.getCeldas()) {
					if (dia.equals(celda.getDia())) {
						celdasDia.add(celda);
					}
				}
			}
			diasCuadrante.add(new DiaBloqueSemana(dia, celdasDia));
		}

		return diasCuadrante;
	}

	public static class FilaBloqueSemana {
		private final String horaInicio;
		private final String horaFin;
		private final List<CeldaBloqueSemana> celdas;

		public FilaBloqueSemana(String horaInicio, String horaFin, List<CeldaBloqueSemana> celdas) {
			this.horaInicio = horaInicio;
			this.horaFin = horaFin;
			this.celdas = celdas;
		}

		public String getHoraInicio() {
			return horaInicio;
		}

		public String getHoraFin() {
			return horaFin;
		}

		public List<CeldaBloqueSemana> getCeldas() {
			return celdas;
		}
	}

	public static class CeldaBloqueSemana {
		private final LocalDate dia;
		private final String horaInicio;
		private final String horaFin;
		private final BloqueEstudio bloque;

		public CeldaBloqueSemana(LocalDate dia, String horaInicio, String horaFin, BloqueEstudio bloque) {
			this.dia = dia;
			this.horaInicio = horaInicio;
			this.horaFin = horaFin;
			this.bloque = bloque;
		}

		public LocalDate getDia() {
			return dia;
		}

		public String getHoraInicio() {
			return horaInicio;
		}

		public String getHoraFin() {
			return horaFin;
		}

		public BloqueEstudio getBloque() {
			return bloque;
		}
	}

	public static class DiaBloqueSemana {
		private final LocalDate dia;
		private final List<CeldaBloqueSemana> celdas;

		public DiaBloqueSemana(LocalDate dia, List<CeldaBloqueSemana> celdas) {
			this.dia = dia;
			this.celdas = celdas;
		}

		public LocalDate getDia() {
			return dia;
		}

		public List<CeldaBloqueSemana> getCeldas() {
			return celdas;
		}
	}

	public static class DiaConBloques {
		private final LocalDate dia;
		private final List<BloqueEstudio> bloques;

		public DiaConBloques(LocalDate dia, List<BloqueEstudio> bloques) {
			this.dia = dia;
			this.bloques = bloques;
		}

		public LocalDate getDia() {
			return dia;
		}

		public List<BloqueEstudio> getBloques() {
			return bloques;
		}
	}

}
