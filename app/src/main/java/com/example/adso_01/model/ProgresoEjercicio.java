package com.example.adso_01.model;

import java.io.Serializable;

public class ProgresoEjercicio implements Serializable {
    private String nombreEjercicio;
    private String grupoMuscular;
    private String fecha;
    private double pesoMaximo;
    private double volumenTotal;
    private int repeticionesTotales;
    private int seriesCompletadas;

    public ProgresoEjercicio() {}

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
