package com.example.adso_01.repository;

import androidx.annotation.Nullable;

/**
 * Resultado de operaciones de autenticación sin exponer tipos de Firebase.
 */
public interface AuthOperationCallback {

    void onComplete(boolean successful, @Nullable Exception error);
}
