package com.example.adso_01.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.adso_01.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

/**
 * Traduce errores técnicos de Firebase Authentication a mensajes
 * legibles y amigables para el usuario final.
 * <p>
 * Cada código de error de Firebase Auth se asigna a un string resource
 * definido en strings.xml, permitiendo la internacionalización y
 * evitando que el usuario vea mensajes técnicos crípticos.
 * Sigue el principio de separación de capas de AGENTS.md.
 * </p>
 */
public class FirebaseAuthErrorMapper {

    /**
     * Convierte una excepción de Firebase Auth en un mensaje para el usuario.
     * <p>
     * Detecta el tipo específico de excepción y retorna el mensaje
     * correspondiente. Si no reconoce el error, retorna un mensaje genérico.
     * </p>
     *
     * @param context Contexto de la aplicación para acceder a recursos.
     * @param e       Excepción de Firebase Auth, o null.
     * @return Mensaje de error legible para el usuario.
     */
    public static String toMessage(@NonNull Context context, @Nullable Exception e) {
        if (e == null) return context.getString(R.string.error_auth_generic);

        // Error de red: sin conexión a Internet
        if (e instanceof FirebaseNetworkException) {
            return context.getString(R.string.error_network);
        }

        if (e instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) e).getErrorCode();

            switch (errorCode) {
                case "ERROR_INVALID_EMAIL":
                    return context.getString(R.string.error_invalid_email);
                case "ERROR_USER_NOT_FOUND":
                    return context.getString(R.string.error_user_not_found);
                case "ERROR_WRONG_PASSWORD":
                    return context.getString(R.string.error_login_invalid);
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return context.getString(R.string.error_email_already_in_use);
                case "ERROR_WEAK_PASSWORD":
                    return context.getString(R.string.error_weak_password);
                case "ERROR_USER_DISABLED":
                    return context.getString(R.string.error_user_disabled);
                case "ERROR_TOO_MANY_REQUESTS":
                    return context.getString(R.string.error_too_many_requests);
                default:
                    return context.getString(R.string.error_auth_generic);
            }
        }

        return e.getLocalizedMessage() != null ? e.getLocalizedMessage() : context.getString(R.string.error_auth_generic);
    }
}
