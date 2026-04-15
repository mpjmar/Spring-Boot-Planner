package com.planner.spring_boot_planner.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "bloquesEstudio", schema = "public")
public class BloqueEstudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String diaSemana;

	@DateTimeFormat(pattern = "HH:mm")
	@Column(name = "hora_inicio")
	private LocalTime horaInicio;

	@DateTimeFormat(pattern = "HH:mm")
	@Column(name = "hora_fin")
	private LocalTime horaFin;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String tipoSesion;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String completado;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignatura;

}
