package com.planner.spring_boot_planner.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "cuadrantes", schema = "public")
public class Cuadrante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "semana_inicio")
    private LocalDate semanaInicio;

    @ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "cuadrante", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
    private List<BloqueEstudio> bloquesEstudio = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getSemanaInicio() {
		return semanaInicio;
	}

	public void setSemanaInicio(LocalDate semanaInicio) {
		this.semanaInicio = semanaInicio;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<BloqueEstudio> getBloquesEstudio() {
		return bloquesEstudio;
	}

	public void setBloquesEstudio(List<BloqueEstudio> bloquesEstudio) {
		this.bloquesEstudio = bloquesEstudio;
	}

	@Override
	public String toString() {
		Long idUsuario = (usuario != null ? usuario.getId() : null);

		return "Cuadrante [id = " + id + 
			   ", semanaInicio = " + semanaInicio + 
			   ", usuario = " + idUsuario + "]";
	}

	
}
