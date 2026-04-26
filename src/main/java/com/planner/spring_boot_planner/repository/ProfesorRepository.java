package com.planner.spring_boot_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.planner.spring_boot_planner.entity.Profesor;

@RepositoryRestResource(path = "profesor", collectionResourceRel = "profesor")
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

	@RestResource(path = "por-usuario", rel = "por-usuario")
	List<Profesor> findByUsuarioId(Long usuarioId);
}
