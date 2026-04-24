package com.planner.spring_boot_planner.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.planner.spring_boot_planner.entity.HorarioClase;

@RepositoryRestResource(path = "horarioClase", collectionResourceRel = "horarioClase")
public interface HorarioClaseRepository extends JpaRepository<HorarioClase, Long> {

	@RestResource(path = "por-fecha", rel = "por-fecha")
    List<HorarioClase> findByFecha(@Param("fecha") LocalDate fecha);

	@RestResource(path = "por-usuario", rel = "por-usuario")
	List<HorarioClase> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
