package com.example.adso_01.repository;

import androidx.annotation.Nullable;

/**
 * Callback genérico para operaciones de autenticación.
 * <p>
 * Abstrae el resultado de las operaciones de Firebase Auth para que los
 * ViewModels y Activities no dependan directamente de tipos de Firebase.
 * Proporciona un resultado booleano de éxito y un error opcional.
 * </p>
 */
public interface AuthOperationCallback {

    /**
     * Notifica el resultado de una operación de autenticación.
     *
     * @param successful true si la operación se completó con éxito.
     * @param error      Excepción asociada si la operación falló, o null si fue exitosa.
     */
    void onComplete(boolean successful, @Nullable Exception error);
}
