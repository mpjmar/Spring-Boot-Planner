package com.planner.spring_boot_planner.entity;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.planner.spring_boot_planner.DiaSemana;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "horarioClase", schema = "public")
public class HorarioClase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana diaSemana;

	@NotNull
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

	@NotNull
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignatura_id")
	@JsonIgnore
    private Asignatura asignatura;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public HorarioClase() {
    }

    public HorarioClase(DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFin, 
		Asignatura asignatura, Usuario usuario) {
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.asignatura = asignatura;
		this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		Long idAsignatura = (asignatura != null ? asignatura.getId() : null);
		Long usuarioId = (usuario != null ? usuario.getId() : null);
		
		return "horarioClase {id = " + id + 
			", diaSemana = " + diaSemana + 
			", horaInicio = " + horaInicio + 
			", horaFin = " + horaFin + 
			", asignatura = " + idAsignatura + 
			", usuario = " + usuarioId +   
			"}";
	}



}
