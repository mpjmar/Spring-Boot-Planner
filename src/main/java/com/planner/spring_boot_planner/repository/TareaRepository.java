package com.planner.spring_boot_planner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.planner.spring_boot_planner.entity.BloqueEstudio;
import com.planner.spring_boot_planner.entity.Tarea;

@RepositoryRestResource(path = "tarea", collectionResourceRel = "tarea")
public interface TareaRepository extends JpaRepository<Tarea, Long> {

	@RestResource(path = "por-usuario", rel = "por-usuario")
	List<BloqueEstudio> findByUsuarioId(Long usuarioId);
}
