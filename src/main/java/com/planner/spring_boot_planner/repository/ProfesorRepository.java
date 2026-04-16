package com.planner.spring_boot_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.planner.spring_boot_planner.entity.Profesor;

@RepositoryRestResource(path = "profesor", collectionResourceRel = "profesor")
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

}
