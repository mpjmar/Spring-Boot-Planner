package com.planner.spring_boot_planner.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "examenes", schema = "public")
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String titulo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_examen")
    private LocalDate fecha;

    @Size(max = 20)
    @Column(length = 20)
    private String peso;

    @Min(1)
    @Column(nullable = false)
    private Double notaObjetivo;

    @ManyToOne
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignatura;



}
