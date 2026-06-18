package com.example.adso_01.model;

import java.io.Serializable;

/**
 * Modelo de dominio que representa al usuario de la aplicación.
 * <p>
 * Contiene todos los datos del perfil fitness del usuario: datos demográficos,
 * objetivos, nivel de actividad y calorías recomendadas.
 * Sigue el patrón POJO (Plain Old Java Object) como exige la arquitectura MVVM,
 * sin lógica de negocio ni dependencias de Firebase.
 * Implementa Serializable para permitir su paso entre Activities vía Intent.
 * </p>
 */
public class Usuario implements Serializable {

    /** Nombre completo del usuario registrado en Firebase Auth. */
    private String nombre;

    /** Correo electrónico asociado a la cuenta de Firebase. */
    private String email;

    /** Edad en años, usada en el cálculo de metabolismo basal. */
    private int edad;

    /** Peso en kilogramos, usado en el cálculo de calorías. */
    private int peso;

    /** Estatura en centímetros, usada en el cálculo de calorías. */
    private int estatura;

    /** Sexo biológico ("Masculino" / "Femenino") para fórmulas de TMB. */
    private String sexo;

    /** Nivel de actividad física ("Ligera" / "Moderada" / "Intensa"). */
    private String actividad;

    /** Objetivo fitness (valor exacto seleccionado por el usuario en el spinner). */
    private String objetivo;

    /** Calorías diarias recomendadas calculadas por Mifflin-St Jeor + ajuste. */
    private int calorias;

    /** Constructor vacío requerido por Firestore para deserialización. */
    public Usuario() {}

    /**
     * Constructor completo para crear un usuario con todos los datos del perfil.
     *
     * @param edad      Edad en años.
     * @param peso      Peso en kilogramos.
     * @param estatura  Estatura en centímetros.
     * @param sexo      Sexo ("Masculino" / "Femenino").
     * @param actividad Nivel de actividad física.
     * @param objetivo  Objetivo fitness seleccionado.
     * @param calorias  Calorías diarias recomendadas.
     */
    public Usuario(int edad, int peso, int estatura, String sexo,
                   String actividad, String objetivo, int calorias) {
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.sexo = sexo;
        this.actividad = actividad;
        this.objetivo = objetivo;
        this.calorias = calorias;
    }

    // ─── Getters y Setters ──────────────────────────────────────────────

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public int getPeso() { return peso; }
    public void setPeso(int peso) { this.peso = peso; }

    public int getEstatura() { return estatura; }
    public void setEstatura(int estatura) { this.estatura = estatura; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public int getCalorias() { return calorias; }
    public void setCalorias(int calorias) { this.calorias = calorias; }
}
