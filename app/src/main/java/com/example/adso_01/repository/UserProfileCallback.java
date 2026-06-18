package com.example.adso_01.repository;

import androidx.annotation.Nullable;

import com.example.adso_01.model.Usuario;

/**
 * Callback para notificar el resultado de la carga del perfil del usuario.
 * <p>
 * Contiene un método único {@link #onResult(Usuario, Exception)} que sigue
 * estas reglas:
 * <ul>
 *   <li>{@code usuario} no nulo: el perfil se cargó correctamente.</li>
 *   <li>{@code error} no nulo: ocurrió un fallo de red o de Firestore.</li>
 *   <li>Ambos nulos: no hay sesión iniciada o el perfil no existe.</li>
 * </ul>
 * </p>
 */
public interface UserProfileCallback {

    /**
     * Notifica el resultado de la carga del perfil.
     *
     * @param usuario Perfil del usuario, o null si no existe o hubo error.
     * @param error   Excepción si ocurrió un fallo, o null si fue exitoso.
     */
    void onResult(@Nullable Usuario usuario, @Nullable Exception error);
}
