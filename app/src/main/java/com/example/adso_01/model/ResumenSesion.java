package com.example.adso_01.model;

import java.io.Serializable;

/**
 * Modelo de dominio que representa el resumen de una sesión de entrenamiento.
 * <p>
 * Almacena metadatos de la sesión: fecha, hora de inicio y fin,
 * total de ejercicios realizados y si la sesión fue completada.
 * Se utiliza en la pantalla de Progreso para mostrar el historial
 * de entrenamientos del usuario y en las estadísticas del Dashboard.
 * </p>
 */
public class ResumenSesion implements Serializable {

    /** Fecha de la sesión en formato ISO o locale (ej: "2026-05-30"). */
    private String fecha;

    /** Timestamp en milisegundos del inicio de la sesión. */
    private long horaInicio;

    /** Timestamp en milisegundos del fin de la sesión. */
    private long horaFin;

    /** Cantidad total de ejercicios incluidos en la sesión. */
    private int totalEjercicios;

    /** Indica si el usuario completó todos los ejercicios de la sesión. */
    private boolean completada;

    /** Constructor vacío requerido para deserialización desde Firestore. */
    public ResumenSesion() {}

    /**
     * Constructor para iniciar un resumen de sesión.
     * Por defecto la sesión se marca como no completada.
     *
     * @param fecha           Fecha del entrenamiento.
     * @param horaInicio      Timestamp de inicio en milisegundos.
     * @param totalEjercicios Cantidad de ejercicios en la rutina.
     */
    public ResumenSesion(String fecha, long horaInicio, int totalEjercicios) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.totalEjercicios = totalEjercicios;
        this.completada = false;
    }

    // ─── Getters y Setters ──────────────────────────────────────────────

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public long getHoraInicio() { return horaInicio; }
    public void setHoraInicio(long horaInicio) { this.horaInicio = horaInicio; }

    public long getHoraFin() { return horaFin; }
    public void setHoraFin(long horaFin) { this.horaFin = horaFin; }

    public int getTotalEjercicios() { return totalEjercicios; }
    public void setTotalEjercicios(int totalEjercicios) { this.totalEjercicios = totalEjercicios; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
}
