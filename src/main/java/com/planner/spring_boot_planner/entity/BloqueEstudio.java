package com.planner.spring_boot_planner.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "bloquesEstudio", schema = "public")
public class BloqueEstudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha")
    private LocalDate fecha;
	
	@DateTimeFormat(pattern = "HH:mm")
	@Column(name = "hora_inicio")
	private LocalTime horaInicio;

	@DateTimeFormat(pattern = "HH:mm")
	@Column(name = "hora_fin")
	private LocalTime horaFin;
		
	@NotBlank
	@Size(max = 20)
	@Column(nullable = false, length = 20)
	private String diaSemana;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignatura;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuadrante_id")
    private Cuadrante cuadrante;

    public BloqueEstudio() {
    }

    public BloqueEstudio(LocalDate fecha, String diaSemana, LocalTime horaInicio, LocalTime horaFin,
            Asignatura asignatura, Cuadrante cuadrante) {
        this.fecha = fecha;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.asignatura = asignatura;
		this.cuadrante = cuadrante;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

	public Cuadrante getCuadrante() {
		return cuadrante;
	}

	public void setCuadrante(Cuadrante cuadrante) {
		this.cuadrante = cuadrante;
	}

	@Override
	public String toString() {
		Long asignaturaId = (asignatura != null ? asignatura.getId() : null);
    	Long cuadranteId = (cuadrante != null ? cuadrante.getId() : null);


		return "BloqueEstudio [id=" + id + 
			   ", fecha = " + fecha + 
			   ", horaInicio = " + horaInicio + 
			   ", horaFin = " + horaFin + 
			   ", diaSemana = " + diaSemana + 
			   ", asignatura = " + asignaturaId + 
			   ", cuadrante = " + cuadranteId + "]";
	}

}
