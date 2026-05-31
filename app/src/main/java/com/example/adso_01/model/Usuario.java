package com.example.adso_01.model;

import java.io.Serializable;

/**
 * Modelo de dominio para el Usuario.
 * Sigue las reglas de AGENTS.md: objeto puro (POJO).
 */
public class Usuario implements Serializable {
    private String nombre;
    private String email;
    private int edad;
    private int peso;
    private int estatura;
    private String sexo;
    private String actividad;
    private String objetivo;
    private int calorias;

    public Usuario() {}

    // Constructor requerido para la lógica de negocio en el ViewModel
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

    // Getters y Setters
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
