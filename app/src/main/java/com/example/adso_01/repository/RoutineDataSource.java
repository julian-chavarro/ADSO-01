package com.example.adso_01.repository;

import com.example.adso_01.model.Ejercicio;

import java.util.List;

/**
 * Catálogo maestro de rutinas de entrenamiento.
 * Define las operaciones de obtención de rutinas organizadas
 * por grupo muscular y por objetivo fitness.
 */
public interface RoutineDataSource {

    List<Ejercicio> obtenerRutinaPorGrupoMuscular(String grupoMuscular, String sexo, String objetivo);
}
