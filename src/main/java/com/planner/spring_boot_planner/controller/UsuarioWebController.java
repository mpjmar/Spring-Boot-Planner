package com.planner.spring_boot_planner.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.UsuarioRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuario")
public class UsuarioWebController {
	
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioWebController(UsuarioRepository usuarioRepository,
								PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/perfil")
	public String verPerfil(Model model, @AuthenticationPrincipal Usuario usuario) {
		model.addAttribute("usuario", usuario);
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
	public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model,
										  @AuthenticationPrincipal Usuario autenticado) {
		if (autenticado == null || !autenticado.getId().equals(id))
			return "redirect:/usuario/perfil";
		Usuario usuario = usuarioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("El usuario no existe: " + id));
		usuario.setPassword("");
		usuario.setRepitePassword("");
		model.addAttribute("usuario", usuario);
		model.addAttribute("accion", "Editar");
		return "usuarios/UsuarioFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEleccion(@PathVariable("id") Long id,
								@ModelAttribute("usuario") Usuario datosFormulario,
								@Valid BindingResult result, Model model,
								@AuthenticationPrincipal Usuario autenticado) {
		if (autenticado == null || !autenticado.getId().equals(id))
			return "redirect:/usuario/perfil";
		Usuario existente = usuarioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("El usuario no existe: " + id));
		if (datosFormulario.getNombre() == null || datosFormulario.getNombre().isBlank()
			|| datosFormulario.getApellidos() == null || datosFormulario.getApellidos().isBlank()
			|| datosFormulario.getEmail() == null || datosFormulario.getEmail().isBlank()) {
				model.addAttribute("accion", "Editar");
				model.addAttribute("error", "Los campos de nombre, apellidos y email son obligatorios.");
				model.addAttribute("usuario", datosFormulario);
				return "usuarios/UsuarioFormView";
		}
		existente.setNombre(datosFormulario.getNombre());
		existente.setApellidos(datosFormulario.getApellidos());
		existente.setEmail(datosFormulario.getEmail());
		if (datosFormulario.getPassword() != null && !datosFormulario.getPassword().isBlank()) {
			if (!datosFormulario.getPassword().equals(datosFormulario.getRepitePassword())) {
				model.addAttribute("accion", "Editar");
				model.addAttribute("error", "Las contraseñas no coinciden");
				datosFormulario.setPassword("");
				datosFormulario.setRepitePassword("");
				model.addAttribute("usuarios", datosFormulario);
				return "usuarios/UsuarioFormView";
			}
			existente.setPassword(passwordEncoder.encode(datosFormulario.getPassword()));
			existente.setRepitePassword(existente.getPassword());
		}
		usuarioRepository.save(existente);
		return "redirect:/usuario/perfil";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable("id") Long id, 
						   @AuthenticationPrincipal Usuario usuario,
						   Model model) {
		if (usuario == null || !usuario.getId().equals(id))
			return "redirect:/usuario/perfil";
		usuarioRepository.deleteById(id);
		model.addAttribute("mensaje", "Usuario eliminado correctamente");
		return "redirect:/login";
	}

	@GetMapping("/recuperar-password")
	public String mostrarFormularioRecuperar() {
		return "recuperarPassword";
	}

	@PostMapping("/restablecer-password")
	public String procesarRestablecerPassword(@RequestParam("token") String token, 
											  @RequestParam("password") String password, 
											  Model model) {
		// 1. Validar token
		// 2. Cambiar contraseña del usuario
		// 3. Eliminar/inutilizar el token
		// 4. Mostrar mensaje de éxito
		return "redirect:/login?resetSuccess";
	}
}
