package com.example.adso_01.repository;

import com.example.adso_01.model.Serie;

/**
 * Repositorio encargado de la persistencia de rutinas y series en Cloud Firestore.
 * Define las operaciones del ciclo de vida completo de una sesión de entrenamiento.
 */
public interface RoutineRepository {

    void registrarSerie(String nombreEjercicio, Serie serie, AuthOperationCallback callback);

    void iniciarSesion(String fecha, int totalEjercicios, String diaNombre, AuthOperationCallback callback);

    void finalizarSesion(String fecha, AuthOperationCallback callback);

    void guardarProgresoEjercicio(String nombreEjercicio, String grupoMuscular, String fecha,
                                  double pesoMaximo, double volumenTotal,
                                  int repeticionesTotales, int seriesCompletadas,
                                  AuthOperationCallback callback);
}
