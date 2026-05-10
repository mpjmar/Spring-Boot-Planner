package com.planner.spring_boot_planner.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

	public record UploadResult(String url, String publicId) {}

    public UploadResult subirImagen(MultipartFile archivo, String carpeta) throws IOException {
        HashMap<String, Object> options = new HashMap<>();
        options.put("folder", carpeta);

		@SuppressWarnings("unchecked")
        Map<String, Object> uploadedFile = cloudinary.uploader().upload(archivo.getBytes(), options);
        String publicId = (String) uploadedFile.get("public_id");
		String url = (String) uploadedFile.get("secure_url");
        return new UploadResult(url, publicId);
    }

	public void eliminarImagen(String publicId) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> result = cloudinary.uploader().destroy(publicId, Map.of());
		Object status = result.get("result");
	
		// Cloudinary puede devolver "ok" o "not found".
		// "not found" lo tratamos como borrado efectivo para mantener la app consistente.
		if (status == null) {
			throw new IOException("Respuesta inválida al eliminar imagen en Cloudinary.");
		}
	
		String value = status.toString();
		if (!"ok".equals(value) && !"not found".equals(value)) {
			throw new IOException("No se pudo eliminar imagen en Cloudinary. Resultado: " + value);
		}
	}
}
