package com.example.adso_01;

public class Usuario {

    private int edad;
    private int peso;
    private int estatura;
    private String sexo;
    private String actividad;
    private String objetivo;
    private int calorias;

    public Usuario() {
    }

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

    public int getEdad() { return edad; }
    public int getPeso() { return peso; }
    public int getEstatura() { return estatura; }
    public String getSexo() { return sexo; }
    public String getActividad() { return actividad; }
    public String getObjetivo() { return objetivo; }
    public int getCalorias() { return calorias; }

    public void setEdad(int edad) { this.edad = edad; }
    public void setPeso(int peso) { this.peso = peso; }
    public void setEstatura(int estatura) { this.estatura = estatura; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setActividad(String actividad) { this.actividad = actividad; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public void setCalorias(int calorias) { this.calorias = calorias; }
}