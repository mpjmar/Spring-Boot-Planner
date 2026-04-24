package com.planner.spring_boot_planner.entity;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "horarioClase", schema = "public")
public class HorarioClase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String diaSemana;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignatura_id")
	@JsonIgnore
    private Asignatura asignatura;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", nullable = false)
	@JsonIgnore
    private Profesor profesor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public HorarioClase() {
    }

    public HorarioClase(String diaSemana, LocalTime horaInicio, LocalTime horaFin, 
		Asignatura asignatura, Profesor profesor, Usuario usuario) {
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.asignatura = asignatura;
        this.profesor = profesor;
		this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		Long idAsignatura = (asignatura != null ? asignatura.getId() : null);
		Long idProfesor = (profesor != null ? profesor.getId() : null);
		Long usuarioId = (usuario != null ? usuario.getId() : null);
		
		return "horarioClase {id = " + id + 
			   ", diaSemana = " + diaSemana + 
			   ", horaInicio = " + horaInicio + 
			   ", horaFin = " + horaFin + 
			   ", asignatura = " + idAsignatura + 
			   ", profesor = " + idProfesor + 
			 	", usuario = " + usuarioId +   
			   "}";
	}



}
