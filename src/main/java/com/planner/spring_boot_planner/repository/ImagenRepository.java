package com.planner.spring_boot_planner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.planner.spring_boot_planner.entity.Imagen;

@RepositoryRestResource(path = "imagen", collectionResourceRel = "imagen")
public interface ImagenRepository extends JpaRepository<Imagen, Long> {

	@RestResource(path = "por-usuario", rel = "por-usuario")
	List<Imagen> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

	Optional<Imagen> findByIdAndUsuarioId(Long id, Long usuarioId);

	@RestResource(path = "count-por-usuario", rel = "count-por-usuario")
    long countByUsuarioId(Long usuarioId);

	@RestResource(path = "delete-por-usuario", rel = "delete-por-usuario")
    void deleteByIdAndUsuarioId(Long id, Long usuarioId);
}
