package com.planner.spring_boot_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.planner.spring_boot_planner.entity.Horario;

@RepositoryRestResource(path = "horario", collectionResourceRel = "horario")
public interface HorarioRepository extends JpaRepository<Horario, Long> {

}
