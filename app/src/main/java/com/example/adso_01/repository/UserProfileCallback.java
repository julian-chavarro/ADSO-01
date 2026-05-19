package com.example.adso_01.repository;

import androidx.annotation.Nullable;

import com.example.adso_01.model.Usuario;

/**
 * Notifica el resultado de la carga del perfil.
 * {@code usuario} no nulo: perfil completo.
 * {@code error} no nulo: fallo de red o Firestore.
 * Ambos nulos: sin sesión o perfil incompleto / inexistente.
 */
public interface UserProfileCallback {

    void onResult(@Nullable Usuario usuario, @Nullable Exception error);
}
