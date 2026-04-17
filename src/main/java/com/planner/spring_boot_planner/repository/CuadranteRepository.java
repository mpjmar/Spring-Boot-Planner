package com.planner.spring_boot_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.planner.spring_boot_planner.entity.Cuadrante;

@RepositoryRestResource(path = "cuadrante", collectionResourceRel = "cuadrante")
public interface CuadranteRepository extends JpaRepository<Cuadrante, Long> {
	
}

