package com.example.adso_01.repository;

/**
 * Repositorio para consultar el historial de progreso del usuario.
 * Define las operaciones de consulta de datos históricos de entrenamiento
 * almacenados en Firestore.
 */
public interface ProgressRepository {

    void obtenerSesiones(ProgressSesionesCallback callback);

    void obtenerProgresoEjercicios(ProgressEjerciciosCallback callback);
}
