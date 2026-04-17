package com.planner.spring_boot_planner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.planner.spring_boot_planner.entity.Examen;

@RepositoryRestResource(path = "examen", collectionResourceRel = "examen")
public interface ExamenRepository extends JpaRepository<Examen, Long> {

}
