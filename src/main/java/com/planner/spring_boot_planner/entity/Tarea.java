package com.planner.spring_boot_planner.entity;

import java.time.Duration;
import java.time.LocalDate;

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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tareas", schema = "public")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignatura;

	@Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Color inválido")
	@Column(name = "color", length = 7)
	private String color;

	@NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String descripcion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @Column
    private String prioridad;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String estado;

    @Column(nullable = false)
    private Duration tiempoEstimado;

    // Campos transitorios para el formulario
    @jakarta.persistence.Transient
    private Integer horas = 0;

    @jakarta.persistence.Transient
    private Integer minutos = 0;

    public Tarea() {
    }

    public Tarea(Asignatura asignatura, String nombre, String descripcion, LocalDate fechaLimite, String prioridad, String estado, Duration tiempoEstimado) {
        this.asignatura = asignatura;
        this.color = asignatura.getColor();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.prioridad = prioridad;
        this.estado = estado;
        this.tiempoEstimado = tiempoEstimado;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Duration getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(Duration tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public Integer getHoras() {
        return horas;
    }

    public void setHoras(Integer horas) {
        this.horas = horas;
    }

    public Integer getMinutos() {
        return minutos;
    }

    public void setMinutos(Integer minutos) {
        this.minutos = minutos;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id = " + id +
                ", nombre = '" + nombre + '\'' +
                ", descripcion = '" + descripcion + '\'' +
                ", fecha limite = " + fechaLimite +
                ", prioridad = '" + prioridad + '\'' +
                ", estado = '" + estado + '\'' +
                ", tiempo estimado = " + tiempoEstimado +
                '}';
    }


    public String getTiempoFormateado() {
        if (tiempoEstimado == null) 
            return "";

        long totalMinutes = tiempoEstimado.toMinutes();
        long horas = totalMinutes / 60;
        long minutos = totalMinutes % 60;

        if (horas == 0) 
            return minutos + "min";

        return horas + "h" + (minutos > 0 ? " " + minutos + "min" : "");
    }
}
