package com.example.adso_01.repository;

import com.example.adso_01.model.ResumenSesion;

import java.util.List;

/**
 * Callback para recibir el resultado de la consulta de sesiones de entrenamiento.
 * <p>
 * Abstrae la respuesta de Firestore para que los ViewModels no dependan
 * de las APIs de Firebase. La lista contendrá el historial de sesiones
 * completadas por el usuario, ordenadas por fecha descendente.
 * </p>
 */
public interface ProgressSesionesCallback {

    /**
     * Notifica el resultado de la consulta de sesiones.
     *
     * @param sesiones Lista de resúmenes de sesión, o null si hubo error.
     * @param error    Excepción si ocurrió un fallo, o null si fue exitoso.
     */
    void onResult(List<ResumenSesion> sesiones, Exception error);
}
