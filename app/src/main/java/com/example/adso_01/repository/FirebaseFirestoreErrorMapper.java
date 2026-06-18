package com.example.adso_01.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.adso_01.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * Traduce excepciones de Cloud Firestore a mensajes de error
 * legibles para el usuario final.
 * <p>
 * Detecta el tipo de excepción y su código específico (permisos,
 * red, etc.) y retorna el string resource correspondiente.
 * También maneja errores de sesión mediante detección textual
 * en el mensaje de la excepción.
 * </p>
 */
public final class FirebaseFirestoreErrorMapper {

    /** Marcador textual para detectar errores de sesión en los mensajes. */
    private static final String SESSION_MARKER = "Sesión";

    /** Constructor privado para evitar instanciación de clase utilitaria. */
    private FirebaseFirestoreErrorMapper() {
    }

    /**
     * Retorna el ID del recurso string correspondiente al error.
     *
     * @param error Excepción de Firestore, o null.
     * @return ID del string resource con el mensaje de error.
     */
    @StringRes
    public static int toMessageRes(@Nullable Exception error) {
        if (error == null) {
            return R.string.error_profile_generic;
        }
        // Detecta errores de sesión por contenido del mensaje
        if (error.getMessage() != null && error.getMessage().contains(SESSION_MARKER)) {
            return R.string.error_session;
        }
        if (error instanceof FirebaseNetworkException) {
            return R.string.error_network;
        }
        if (error instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException firestoreError = (FirebaseFirestoreException) error;
            switch (firestoreError.getCode()) {
                case PERMISSION_DENIED:
                    return R.string.error_profile_permission;
                case UNAVAILABLE:
                    return R.string.error_network;
                default:
                    return R.string.error_profile_generic;
            }
        }
        return R.string.error_profile_generic;
    }

    /**
     * Convierte una excepción de Firestore en un mensaje de texto legible.
     *
     * @param context Contexto de la aplicación para acceder a recursos.
     * @param error   Excepción de Firestore, o null.
     * @return Mensaje de error legible para el usuario.
     */
    @NonNull
    public static String toMessage(@NonNull android.content.Context context, @Nullable Exception error) {
        return context.getString(toMessageRes(error));
    }
}
