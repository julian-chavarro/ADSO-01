package com.example.adso_01.model;

import java.io.Serializable;

public class Serie implements Serializable {
    private int numero;
    private int repeticiones;
    private double peso;
    private boolean completada;

    public Serie() {}

    public Serie(int numero, int repeticiones, double peso) {
        this.numero = numero;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.completada = false;
    }

    // Getters y Setters
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public int getRepeticiones() { return repeticiones; }
    public void setRepeticiones(int repeticiones) { this.repeticiones = repeticiones; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
}
