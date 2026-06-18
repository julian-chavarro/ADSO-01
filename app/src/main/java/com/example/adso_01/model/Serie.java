package com.example.adso_01.model;

import java.io.Serializable;

/**
 * Modelo de dominio que representa una serie individual dentro de un ejercicio.
 * <p>
 * Cada serie tiene un número (1, 2, 3...), una cantidad de repeticiones,
 * un peso utilizado, y un estado de completada que indica si el usuario
 * ya la realizó durante la sesión activa.
 * Implementa Serializable para ser usada en el adaptador del RecyclerView
 * y persistir el progreso parcial de la sesión.
 * </p>
 */
public class Serie implements Serializable {

    /** Número de la serie dentro del ejercicio (1-indexed). */
    private int numero;

    /** Cantidad de repeticiones objetivo para esta serie. */
    private int repeticiones;

    /** Peso en kilogramos cargado para esta serie. */
    private double peso;

    /** Estado que indica si el usuario ya completó esta serie. */
    private boolean completada;

    /** Constructor vacío requerido para deserialización. */
    public Serie() {}

    /**
     * Constructor que crea una serie con valores iniciales.
     * Por defecto la serie se crea como no completada.
     *
     * @param numero       Número de la serie (1, 2, 3...).
     * @param repeticiones Repeticiones objetivo.
     * @param peso         Peso inicial en kilogramos.
     */
    public Serie(int numero, int repeticiones, double peso) {
        this.numero = numero;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.completada = false;
    }

    // ─── Getters y Setters ──────────────────────────────────────────────

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public int getRepeticiones() { return repeticiones; }
    public void setRepeticiones(int repeticiones) { this.repeticiones = repeticiones; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
}
