package com.example.adso_01.repository;

import com.example.adso_01.model.ProgresoEjercicio;

import java.util.List;

/**
 * Callback para recibir el resultado de la consulta de progreso por ejercicios.
 * <p>
 * Abstrae la respuesta de Firestore para que los ViewModels no dependan
 * de las APIs de Firebase. La lista contendrá el historial completo de
 * rendimiento del usuario organizado por ejercicio y fecha.
 * </p>
 */
public interface ProgressEjerciciosCallback {

    /**
     * Notifica el resultado de la consulta de progreso por ejercicios.
     *
     * @param progreso Lista de progreso de ejercicios, o null si hubo error.
     * @param error    Excepción si ocurrió un fallo, o null si fue exitoso.
     */
    void onResult(List<ProgresoEjercicio> progreso, Exception error);
}
