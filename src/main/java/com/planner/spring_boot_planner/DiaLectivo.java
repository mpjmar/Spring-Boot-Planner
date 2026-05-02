package com.planner.spring_boot_planner;

public enum DiaLectivo {
    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miércoles"),
	JUEVES("Jueves"),
	VIERNES("Viernes");

    private final String nombre;

    DiaLectivo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
