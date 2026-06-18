package com.example.adso_01.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de dominio que representa un ejercicio dentro de una rutina.
 * <p>
 * Cada ejercicio contiene un nombre, el grupo muscular que trabaja,
 * un número objetivo de series, y una lista de {@link Serie} que el
 * usuario debe completar durante el entrenamiento.
 * Implementa Serializable para navegar entre Activities.
 * </p>
 */
public class Ejercicio implements Serializable {

    /** Nombre del ejercicio (ej: "Press de banca", "Sentadilla"). */
    private String nombre;

    /** Cantidad de series que el usuario debe realizar. */
    private int seriesObjetivo;

    /** Grupo muscular al que pertenece el ejercicio (ej: "Pecho", "Pierna"). */
    private String grupoMuscular;

    /** URL de imagen o video demostrativo del ejercicio (reservado para uso futuro). */
    private String imagenUrl;

    /** Consejos técnicos para el usuario (tips de ejecución mostrados junto al video). */
    private String consejos;

    /** Lista de series que componen el ejercicio, inicializadas al crear el objeto. */
    private List<Serie> series;

    /** Constructor vacío requerido para deserialización. */
    public Ejercicio() {
        this.series = new ArrayList<>();
    }

    /**
     * Constructor que crea un ejercicio e inicializa automáticamente
     * las series vacías con 10 repeticiones por defecto.
     *
     * @param nombre        Nombre del ejercicio.
     * @param seriesObjetivo Número de series a realizar.
     * @param grupoMuscular Grupo muscular objetivo.
     * @param imagenUrl     URL de imagen demostrativa (puede ser null).
     */
    public Ejercicio(String nombre, int seriesObjetivo, String grupoMuscular, String imagenUrl) {
        this.nombre = nombre;
        this.seriesObjetivo = seriesObjetivo;
        this.grupoMuscular = grupoMuscular;
        this.imagenUrl = imagenUrl;
        this.series = new ArrayList<>();

        // Inicializa cada serie con 10 repeticiones y peso 0 como valor por defecto
        for (int i = 1; i <= seriesObjetivo; i++) {
            this.series.add(new Serie(i, 10, 0));
        }
    }

    // ─── Getters y Setters ──────────────────────────────────────────────

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getSeriesObjetivo() { return seriesObjetivo; }
    public void setSeriesObjetivo(int seriesObjetivo) { this.seriesObjetivo = seriesObjetivo; }

    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getConsejos() { return consejos; }
    public Ejercicio setConsejos(String consejos) { this.consejos = consejos; return this; }

    public List<Serie> getSeries() { return series; }
    public void setSeries(List<Serie> series) { this.series = series; }
}
