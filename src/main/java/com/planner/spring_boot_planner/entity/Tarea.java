package com.planner.spring_boot_planner.entity;

import java.sql.Time;
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
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tareas", schema = "public")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String titulo;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String descripcion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String prioridad;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String estado;

    @Column(nullable = false)
    private Time tiempoEstimado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignatura;

    public Tarea() {
    }

    public Tarea(String titulo, String descripcion, LocalDate fechaLimite, String prioridad, String estado, Time tiempoEstimado, Asignatura asignatura) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.prioridad = prioridad;
        this.estado = estado;
        this.tiempoEstimado = tiempoEstimado;
        this.asignatura = asignatura;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public Time getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(Time tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id = " + id +
                ", titulo = '" + titulo + '\'' +
                ", descripcion = '" + descripcion + '\'' +
                ", fecha limite = " + fechaLimite +
                ", prioridad = '" + prioridad + '\'' +
                ", estado = '" + estado + '\'' +
                ", tiempo estimado = " + tiempoEstimado +
                '}';
    }


}
