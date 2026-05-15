package com.example.adso_01.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ejercicio implements Serializable {
    private String nombre;
    private int seriesObjetivo;
    private String grupoMuscular;
    private String imagenUrl;
    private List<Serie> series;

    public Ejercicio() {
        this.series = new ArrayList<>();
    }

    public Ejercicio(String nombre, int seriesObjetivo, String grupoMuscular, String imagenUrl) {
        this.nombre = nombre;
        this.seriesObjetivo = seriesObjetivo;
        this.grupoMuscular = grupoMuscular;
        this.imagenUrl = imagenUrl;
        this.series = new ArrayList<>();
        
        // Inicializar series vacías según el objetivo
        for (int i = 1; i <= seriesObjetivo; i++) {
            this.series.add(new Serie(i, 10, 0)); // 10 reps por defecto
        }
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getSeriesObjetivo() { return seriesObjetivo; }
    public void setSeriesObjetivo(int seriesObjetivo) { this.seriesObjetivo = seriesObjetivo; }

    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public List<Serie> getSeries() { return series; }
    public void setSeries(List<Serie> series) { this.series = series; }
}
