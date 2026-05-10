package com.planner.spring_boot_planner.entity;

import java.time.LocalDate;
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
import jakarta.validation.constraints.Pattern;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignatura;

	@Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Color inválido")
	@Column(name = "color", length = 7)
	private String color;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public BloqueEstudio() {
    }

    public BloqueEstudio(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
            Asignatura asignatura, Usuario usuario) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.asignatura = asignatura;
		this.color = asignatura.getColor();
		this.usuario = usuario;
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

    public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		Long asignaturaId = (asignatura != null ? asignatura.getId() : null);
    	Long usuarioId = (usuario != null ? usuario.getId() : null);


		return "BloqueEstudio {id=" + id + 
			   ", fecha = " + fecha + 
			   ", horaInicio = " + horaInicio + 
			   ", horaFin = " + horaFin + 
			   ", asignatura = " + asignaturaId + 
			   ", usuario = " + usuarioId + 
			   "}";
	}

}
