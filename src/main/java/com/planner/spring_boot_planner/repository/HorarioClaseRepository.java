package com.planner.spring_boot_planner.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.planner.spring_boot_planner.DiaLectivo;
import com.planner.spring_boot_planner.entity.HorarioClase;

@RepositoryRestResource(path = "horarioClase", collectionResourceRel = "horarioClase")
public interface HorarioClaseRepository extends JpaRepository<HorarioClase, Long> {

	@RestResource(path = "por-diaLectivo", rel = "por-diaLectivo")
    List<HorarioClase> findByDiaLectivo(DiaLectivo diaLectivo);

	@RestResource(path = "por-usuario", rel = "por-usuario")
	List<HorarioClase> findByUsuarioId(Long usuarioId);

	@RestResource(path = "por-fecha-usuario", rel = "por-fecha-usuario")
	List<HorarioClase> findByDiaLectivoAndUsuarioId(DiaLectivo diaLectivo, Long usuarioId);

	@Query("""
		select count(h) > 0
		from HorarioClase h
		where h.usuario.id = :usuarioId
		and h.diaLectivo = :diaLectivo
		and h.horaInicio < :horaFin
		and h.horaFin > :horaInicio
	""")
	boolean existeSolapamiento(Long usuarioId,
							DiaLectivo diaLectivo,
							LocalTime horaInicio,
							LocalTime horaFin);

	@Query("""
		select count(h) > 0
		from HorarioClase h
		where h.usuario.id = :usuarioId
		and h.diaLectivo = :diaLectivo
		and h.horaInicio < :horaFin
		and h.horaFin > :horaInicio
		and h.id <> :id
	""")
	boolean existeSolapamientoEditando(Long id,
									Long usuarioId,
									DiaLectivo diaLectivo,
									LocalTime horaInicio,
									LocalTime horaFin);

}
