package com.example.adso_01.model;

import java.io.Serializable;

public class ResumenSesion implements Serializable {
    private String fecha;
    private long horaInicio;
    private long horaFin;
    private int totalEjercicios;
    private boolean completada;

    public ResumenSesion() {}

    public ResumenSesion(String fecha, long horaInicio, int totalEjercicios) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.totalEjercicios = totalEjercicios;
        this.completada = false;
    }

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
