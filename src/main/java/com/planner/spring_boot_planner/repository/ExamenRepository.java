package com.planner.spring_boot_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.planner.spring_boot_planner.entity.Examen;

@RepositoryRestResource(path = "examen", collectionResourceRel = "examen")
public interface ExamenRepository extends JpaRepository<Examen, Long> {

	@RestResource(path = "por-usuario", rel = "por-usuario")
	List<Examen> findByUsuarioId(Long usuarioId);
}
