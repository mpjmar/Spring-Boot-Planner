package com.planner.spring_boot_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.planner.spring_boot_planner.entity.Asignatura;

@RepositoryRestResource(path = "asignatura", collectionResourceRel = "asignatura")
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {

}
