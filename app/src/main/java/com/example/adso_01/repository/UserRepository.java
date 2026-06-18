package com.example.adso_01.repository;

import com.example.adso_01.model.Usuario;

/**
 * Repositorio para la gestión del perfil del usuario en Firestore.
 * Define las operaciones CRUD sobre los datos del usuario almacenados
 * en la colección "usuarios" de Cloud Firestore.
 */
public interface UserRepository {

    void guardarUsuario(Usuario usuario, AuthOperationCallback callback);

    void obtenerUsuario(UserProfileCallback callback);
}
