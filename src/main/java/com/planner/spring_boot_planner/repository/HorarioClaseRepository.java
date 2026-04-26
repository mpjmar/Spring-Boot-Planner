package com.planner.spring_boot_planner.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.planner.spring_boot_planner.entity.HorarioClase;

@RepositoryRestResource(path = "horarioClase", collectionResourceRel = "horarioClase")
public interface HorarioClaseRepository extends JpaRepository<HorarioClase, Long> {

	@RestResource(path = "por-fecha", rel = "por-fecha")
    List<HorarioClase> findByFecha(LocalDate fecha);

	@RestResource(path = "por-usuario", rel = "por-usuario")
	List<HorarioClase> findByUsuarioId(Long usuarioId);

	@RestResource(path = "por-fecha-usuario", rel = "por-fecha-usuario")
	List<HorarioClase> findByFechaAndUsuarioId(LocalDate fecha, Long usuarioId);
}
