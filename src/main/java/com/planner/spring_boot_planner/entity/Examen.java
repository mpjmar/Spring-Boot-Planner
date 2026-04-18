package com.planner.spring_boot_planner.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "examenes", schema = "public")
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignatura;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String descripcion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_examen")
    private LocalDate fecha;

    @Size(max = 20)
    @Column(length = 20)
    private String peso;

    @Min(1)
    @Column(nullable = false)
    private Double notaObjetivo;


    public Examen() {
    }

    public Examen(Asignatura asignatura, String descripcion, LocalDate fecha, String peso, Double notaObjetivo) {
        this.asignatura = asignatura;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.peso = peso;
        this.notaObjetivo = notaObjetivo;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public Double getNotaObjetivo() {
        return notaObjetivo;
    }

    public void setNotaObjetivo(Double notaObjetivo) {
        this.notaObjetivo = notaObjetivo;
    }

    @Override
    public String toString() {
        return "Examen{" +
                "id = " + id +
                ", descripcion = '" + descripcion + '\'' +
                ", fecha = " + fecha +
                ", peso = '" + peso + '\'' +
                ", notaObjetivo = " + notaObjetivo +
                '}';
    }



}
