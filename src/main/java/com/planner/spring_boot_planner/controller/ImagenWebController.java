package com.planner.spring_boot_planner.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.planner.spring_boot_planner.entity.Imagen;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.ImagenRepository;
import com.planner.spring_boot_planner.service.CloudinaryService;

@Controller
public class ImagenWebController {

	private final ImagenRepository imagenRepository;
	private final CloudinaryService cloudinaryService;

	public ImagenWebController(ImagenRepository imagenRepository, 
							   CloudinaryService cloudinaryService) {
		this.imagenRepository = imagenRepository;
		this.cloudinaryService = cloudinaryService;
	}

	@GetMapping("/imagenesInspiradoras")
	public String listarImagenes(@AuthenticationPrincipal Usuario usuario, Model model) {
		List<Imagen> imagenes = imagenRepository.findByUsuarioIdOrderByCreatedAtDesc(usuario.getId());
        model.addAttribute("imagenes", imagenes);
        return "imagenes/ImagenListingView";
	}

	@PostMapping("/imagenesInspiradoras")
	public String subir(@AuthenticationPrincipal Usuario usuario,
						@RequestParam("archivo") MultipartFile archivo,
						RedirectAttributes redirectAttributes) {
		if (archivo == null || archivo.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Selecciona una imagen para subir.");
			return "redirect:/imagenesInspiradoras";
		}
		try {
			CloudinaryService.UploadResult result =
					cloudinaryService.subirImagen(archivo, "planner/" + usuario.getId());

			Imagen imagen = new Imagen(usuario, result.url(), result.publicId());
			imagenRepository.save(imagen);
			redirectAttributes.addFlashAttribute("ok", "Imagen subida correctamente.");
		} catch(IOException e) {
			redirectAttributes.addFlashAttribute("error", "No se pudo subir la imagen.");
		}
		return "redirect:/imagenesInspiradoras";
	}

	@PostMapping("/imagenesInspiradoras/{id}/eliminar")
	public String eliminar(@AuthenticationPrincipal Usuario usuario,
						   @PathVariable Long id,
						   RedirectAttributes redirectAttributes) {
		return imagenRepository.findByIdAndUsuarioId(id, usuario.getId())
					.map(imagen -> {
						try {
							cloudinaryService.eliminarImagen(imagen.getPublicId());
						} catch (IOException e) {
							redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la imagen en Cloudinary.");
							return "redirect:/imagenesInspiradoras";
						}
					
						imagenRepository.delete(imagen);
						redirectAttributes.addFlashAttribute("ok", "Imagen eliminada.");
						return "redirect:/imagenesInspiradoras";
					})
					.orElseGet(() -> {
						redirectAttributes.addFlashAttribute("error", "Imagen no encontrada.");
						return "redirect:/imagenesInspiradoras";
					});

	}
}
