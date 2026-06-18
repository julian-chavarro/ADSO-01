package com.example.adso_01.ui.common;

import androidx.annotation.Nullable;

/**
 * Contenedor para eventos de un solo consumo en la UI.
 * <p>
 * Resuelve el problema clásico de LiveData: cuando se rotala pantalla,
 * el evento se re-emite. Con {@link Event}, el contenido solo se entrega
 * una vez (la primera llamada a {@link #getContentIfNotHandled()}).
 * Las llamadas posteriores retornan null.
 * </p>
 * <p>
 * Útil para navegación, Toasts, Snackbars — cualquier acción que solo
 * deba ejecutarse una vez por ocurrencia.
 * </p>
 *
 * @param <T> Tipo del contenido del evento.
 */
public class Event<T> {

    /** Contenido del evento. */
    private final T content;

    /** Indica si el contenido ya fue consumido. */
    private boolean hasBeenHandled;

    /**
     * Crea un nuevo evento con el contenido proporcionado.
     *
     * @param content Contenido del evento.
     */
    public Event(T content) {
        this.content = content;
    }

    /**
     * Retorna el contenido si es la primera vez que se consume, o null en adelante.
     *
     * @return Contenido del evento, o null si ya fue consumido.
     */
    @Nullable
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        }
        hasBeenHandled = true;
        return content;
    }
}
