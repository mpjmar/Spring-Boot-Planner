package com.planner.spring_boot_planner.repository;

import com.planner.spring_boot_planner.entity.BloqueEstudio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;


@RepositoryRestResource(path = "bloqueEstudios", collectionResourceRel = "bloqueEstudios")
public interface BloqueEstudioRepository extends JpaRepository<BloqueEstudio, Long> {
	
	@RestResource(path = "por-cuadrante-y-semana", rel = "por-cuadrante-y-semana")
    List<BloqueEstudio> findByCuadranteIdAndFechaBetweenOrderByFechaAscHoraInicioAsc(
            @Param("cuadranteId") Long cuadranteId,
            @Param("inicioSemana") LocalDate inicioSemana,
            @Param("finSemana") LocalDate finSemana
    );

    @RestResource(path = "por-usuario-y-semana", rel = "por-usuario-y-semana")
    List<BloqueEstudio> findByCuadranteUsuarioIdAndFechaBetweenOrderByFechaAscHoraInicioAsc(
            @Param("usuarioId") Long usuarioId,
            @Param("inicioSemana") LocalDate inicioSemana,
            @Param("finSemana") LocalDate finSemana
    );
}

