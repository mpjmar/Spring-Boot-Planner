package com.planner.spring_boot_planner;

public enum DiaSemana {
    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miércoles"),
	JUEVES("Jueves"),
	VIERNES("Viernes");

    private final String nombre;

    DiaSemana(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
