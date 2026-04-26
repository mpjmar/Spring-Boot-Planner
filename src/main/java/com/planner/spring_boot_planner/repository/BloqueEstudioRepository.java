package com.planner.spring_boot_planner.repository;

import com.planner.spring_boot_planner.entity.BloqueEstudio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;


@RepositoryRestResource(path = "bloqueEstudios", collectionResourceRel = "bloqueEstudios")
public interface BloqueEstudioRepository extends JpaRepository<BloqueEstudio, Long> {
	
	@RestResource(path = "por-fecha", rel = "por-fecha")
    List<BloqueEstudio> findByFecha(LocalDate fecha);

	@RestResource(path = "por-usuario", rel = "por-usuario")
	List<BloqueEstudio> findByUsuarioId(Long usuarioId);

	@Query("SELECT b FROM BloqueEstudio b WHERE b.fecha = :fecha AND b.usuario.id = :usuarioId "
		+ "AND b.horaInicio < :horaFin AND b.horaFin > :horaInicio")
	List<BloqueEstudio> findSolapadosPorUsuario(
		@Param("fecha") LocalDate fecha,
		@Param("usuarioId") Long usuarioId,
		@Param("horaInicio") LocalTime horaInicio,
		@Param("horaFin") LocalTime horaFin);

	List<BloqueEstudio> findByFechaAndUsuarioId(LocalDate fecha, Long uid);

}

