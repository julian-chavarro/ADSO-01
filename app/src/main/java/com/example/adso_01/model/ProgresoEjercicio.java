package com.example.adso_01.model;

import java.io.Serializable;

/**
 * Modelo de dominio que representa el progreso histórico de un ejercicio específico.
 * <p>
 * Almacena métricas de rendimiento como el peso máximo levantado,
 * el volumen total (peso × repeticiones), repeticiones totales acumuladas
 * y series completadas, todo asociado a una fecha de entrenamiento.
 * Se usa en la pantalla de Progreso para mostrar la evolución del usuario
 * ejercicio por ejercicio a lo largo del tiempo.
 * </p>
 */
public class ProgresoEjercicio implements Serializable {

    /** Nombre del ejercicio al que corresponde este progreso. */
    private String nombreEjercicio;

    /** Grupo muscular del ejercicio. */
    private String grupoMuscular;

    /** Fecha en que se registró este progreso (formato ISO o locale). */
    private String fecha;

    /** Peso máximo levantado en ese ejercicio durante la sesión. */
    private double pesoMaximo;

    /** Volumen total calculado como suma de (peso × repeticiones) de todas las series. */
    private double volumenTotal;

    /** Suma total de repeticiones realizadas en todas las series. */
    private int repeticionesTotales;

    /** Cantidad de series que el usuario completó. */
    private int seriesCompletadas;

    /** Constructor vacío requerido para deserialización desde Firestore. */
    public ProgresoEjercicio() {}

    /**
     * Constructor completo para registrar el progreso de un ejercicio.
     *
     * @param nombreEjercicio   Nombre del ejercicio.
     * @param grupoMuscular     Grupo muscular trabajado.
     * @param fecha             Fecha del entrenamiento.
     * @param pesoMaximo        Peso máximo alcanzado.
     * @param volumenTotal      Volumen total (peso × repeticiones).
     * @param repeticionesTotales Total de repeticiones realizadas.
     * @param seriesCompletadas Cantidad de series completadas.
     */
    public ProgresoEjercicio(String nombreEjercicio, String grupoMuscular, String fecha,
                             double pesoMaximo, double volumenTotal,
                             int repeticionesTotales, int seriesCompletadas) {
        this.nombreEjercicio = nombreEjercicio;
        this.grupoMuscular = grupoMuscular;
        this.fecha = fecha;
        this.pesoMaximo = pesoMaximo;
        this.volumenTotal = volumenTotal;
        this.repeticionesTotales = repeticionesTotales;
        this.seriesCompletadas = seriesCompletadas;
    }

    // ─── Getters y Setters ──────────────────────────────────────────────

    public String getNombreEjercicio() { return nombreEjercicio; }
    public void setNombreEjercicio(String nombreEjercicio) { this.nombreEjercicio = nombreEjercicio; }

    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public double getPesoMaximo() { return pesoMaximo; }
    public void setPesoMaximo(double pesoMaximo) { this.pesoMaximo = pesoMaximo; }

    public double getVolumenTotal() { return volumenTotal; }
    public void setVolumenTotal(double volumenTotal) { this.volumenTotal = volumenTotal; }

    public int getRepeticionesTotales() { return repeticionesTotales; }
    public void setRepeticionesTotales(int repeticionesTotales) { this.repeticionesTotales = repeticionesTotales; }

    public int getSeriesCompletadas() { return seriesCompletadas; }
    public void setSeriesCompletadas(int seriesCompletadas) { this.seriesCompletadas = seriesCompletadas; }
}
