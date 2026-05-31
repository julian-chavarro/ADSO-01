package com.example.adso_01.ui.common;

import androidx.annotation.Nullable;

/**
 * Evento de un solo consumo (navegación, Toast de éxito) para evitar repetición al rotar pantalla.
 */
public class Event<T> {

    private final T content;
    private boolean hasBeenHandled;

    public Event(T content) {
        this.content = content;
    }

    @Nullable
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        }
        hasBeenHandled = true;
        return content;
    }
}
