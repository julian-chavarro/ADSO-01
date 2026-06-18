package com.example.adso_01.repository;

/**
 * Repositorio encargado de la autenticación de usuarios.
 * Define las operaciones de registro, inicio de sesión y restablecimiento
 * de contraseña.
 */
public interface AuthRepository {

    void login(String email, String password, AuthOperationCallback callback);

    void register(String nombre, String email, String password, AuthOperationCallback callback);

    void resetPassword(String email, AuthOperationCallback callback);
}
