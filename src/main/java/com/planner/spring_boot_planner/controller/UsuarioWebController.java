package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.entity.Cuadrante;
import com.planner.spring_boot_planner.entity.BloqueEstudio;
import com.planner.spring_boot_planner.repository.UsuarioRepository;
import com.planner.spring_boot_planner.repository.CuadranteRepository;
import com.planner.spring_boot_planner.repository.BloqueEstudioRepository;
import jakarta.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
public class UsuarioWebController {
	
	private final UsuarioRepository usuarioRepository;
	private final CuadranteRepository cuadranteRepository;
	private final BloqueEstudioRepository bloqueEstudioRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioWebController(UsuarioRepository usuarioRepository,
								CuadranteRepository cuadranteRepository,
								BloqueEstudioRepository bloqueEstudioRepository,
								PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.cuadranteRepository = cuadranteRepository;
		this.bloqueEstudioRepository = bloqueEstudioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/perfil")
	public String verPerfil(Model model, @AuthenticationPrincipal Usuario usuarioAutenticado) {
		model.addAttribute("usuario", usuarioAutenticado);
		return "usuarios/UsuarioPerfilView";
	}

	/* @GetMapping
	public String listarUsuarios(Model model) {
		model.addAttribute("usuarios", usuarioRepository.findAll());
		return "usuarios/usuarioListingView";
	} */

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("accion", "Añadir");
		return "usuarios/UsuarioFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("usuario") Usuario usuario,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Añadir");
			return "usuarios/UsuarioFormView";
		}
		if (!usuario.getPassword().equals(usuario.getRepitePassword())) {
			model.addAttribute("accion", "Añadir");
			model.addAttribute("error", "Las contraseñas no coinciden.");
			return "usuarios/UsuarioFormView";
		}
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuarioRepository.save(usuario);
		return "redirect:/login";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
		Usuario usuario = usuarioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("El usuario no existe: " + id));
		model.addAttribute("usuario", usuario);
		model.addAttribute("accion", "Editar");
		return "usuarios/UsuarioFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEleccion(@PathVariable Long id,
								@Valid @ModelAttribute("usuario") Usuario usuario,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "usuarios/usuarioFormView";
		}
		usuario.setId(id);
		usuarioRepository.save(usuario);
		return "redirect:/usuario/perfil";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id) {
		usuarioRepository.deleteById(id);
		return "redirect:/login";
	}

	@GetMapping("/recuperar-password")
	public String mostrarFormularioRecuperar() {
		return "recuperarPassword";
	}

	@PostMapping("/recuperar-password")
	public String procesarRecuperarPassword(@RequestParam String email, Model model) {
		// 1. Buscar usuario por email
		// 2. Generar token único y guardarlo (en la entidad Usuario o en una tabla aparte)
		// 3. Enviar email con enlace: /restablecer-password?token=XYZ
		// 4. Mostrar mensaje de éxito
		model.addAttribute("mensaje", "Si el email existe, recibirás un enlace para restablecer tu contraseña.");
		return "recuperarPassword";
	}

	@GetMapping("/restablecer-password")
	public String mostrarFormularioRestablecer(@RequestParam String token, Model model) {
		// 1. Validar token
		// 2. Si es válido, mostrar formulario
		model.addAttribute("token", token);
		return "restablecerPassword";
	}

	@PostMapping("/restablecer-password")
	public String procesarRestablecerPassword(@RequestParam String token, @RequestParam String password, Model model) {
		// 1. Validar token
		// 2. Cambiar contraseña del usuario
		// 3. Eliminar/inutilizar el token
		// 4. Mostrar mensaje de éxito
		return "redirect:/login?resetSuccess";
	}
}
