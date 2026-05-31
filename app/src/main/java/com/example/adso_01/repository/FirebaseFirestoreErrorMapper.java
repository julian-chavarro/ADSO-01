package com.example.adso_01.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.adso_01.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * Traduce excepciones de Firestore a mensajes para el usuario.
 */
public final class FirebaseFirestoreErrorMapper {

    private static final String SESSION_MARKER = "Sesión";

    private FirebaseFirestoreErrorMapper() {
    }

    @StringRes
    public static int toMessageRes(@Nullable Exception error) {
        if (error == null) {
            return R.string.error_profile_generic;
        }
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

    @NonNull
    public static String toMessage(@NonNull android.content.Context context, @Nullable Exception error) {
        return context.getString(toMessageRes(error));
    }
}
