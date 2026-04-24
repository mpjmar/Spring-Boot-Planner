package com.planner.spring_boot_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.entity.BloqueEstudio;

@RepositoryRestResource(path = "asignatura", collectionResourceRel = "asignatura")
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {

	@RestResource(path = "por-usuario", rel = "por-usuario")
	List<BloqueEstudio> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
