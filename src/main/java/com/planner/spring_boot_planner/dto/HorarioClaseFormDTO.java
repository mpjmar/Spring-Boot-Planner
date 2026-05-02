package com.planner.spring_boot_planner.dto;

import com.planner.spring_boot_planner.DiaLectivo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HorarioClaseFormDTO {

    private Long id;

    @NotNull(message = "El día es obligatorio")
    private DiaLectivo diaLectivo;

    @NotBlank(message = "La hora de inicio es obligatoria")
    private String horaInicio;
	
    @NotBlank(message = "La hora de fin es obligatoria")
    private String horaFin;

    @NotNull(message = "La asignatura es obligatoria")
    private Long asignaturaId;

	public HorarioClaseFormDTO() {
	}

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DiaLectivo getDiaLectivo() {
		return diaLectivo;
	}

	public void setDiaLectivo(DiaLectivo diaLectivo) {
		this.diaLectivo = diaLectivo;
	}

	public String getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	public String getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}

	public Long getAsignaturaId() {
		return asignaturaId;
	}

	public void setAsignaturaId(Long asignaturaId) {
		this.asignaturaId = asignaturaId;
	}

	
}