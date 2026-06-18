package com.example.adso_01.util;

import androidx.annotation.Nullable;

/**
 * Clase genérica que encapsula el estado de una operación asíncrona.
 * <p>
 * Se utiliza como contenedor de datos en la comunicación entre
 * los repositorios (Firebase) y los ViewModels. Permite que la UI
 * reaccione de forma predecible a los diferentes estados de una
 * operación: carga, éxito, error, o inactivo.
 * </p>
 *
 * @param <T> Tipo del dato contenido en caso de éxito.
 */
public class Resource<T> {

    /** Posibles estados de una operación asíncrona. */
    public enum Status {
        /** Estado inicial, no se ha realizado ninguna operación. */
        IDLE,
        /** La operación está en curso, mostrar indicador de carga. */
        LOADING,
        /** La operación finalizó correctamente con datos disponibles. */
        SUCCESS,
        /** La operación falló con un mensaje de error. */
        ERROR
    }

    /** Estado actual de la operación. */
    private final Status status;

    /** Datos devueltos por la operación (null si es IDLE, LOADING o ERROR). */
    @Nullable
    private final T data;

    /** Mensaje descriptivo del resultado (útil en caso de error). */
    @Nullable
    private final String message;

    /**
     * Constructor privado — usar los métodos estáticos factory.
     *
     * @param status  Estado de la operación.
     * @param data    Datos opcionales.
     * @param message Mensaje opcional.
     */
    private Resource(Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    /** Crea un Resource en estado IDLE (sin operación en curso). */
    public static <T> Resource<T> idle() {
        return new Resource<>(Status.IDLE, null, null);
    }

    /** Crea un Resource en estado LOADING (operación en progreso). */
    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    /** Crea un Resource en estado SUCCESS con los datos proporcionados. */
    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    /** Crea un Resource en estado ERROR con un mensaje descriptivo. */
    public static <T> Resource<T> error(String message) {
        return new Resource<>(Status.ERROR, null, message);
    }

    // ─── Getters ─────────────────────────────────────────────────────────

    /** @return Estado actual de la operación. */
    public Status getStatus() { return status; }

    /** @return Datos de la operación, o null si no hay. */
    @Nullable
    public T getData() { return data; }

    /** @return Mensaje descriptivo del resultado o error. */
    @Nullable
    public String getMessage() { return message; }

    // ─── Métodos de conveniencia ─────────────────────────────────────────

    /** @return true si la operación está en progreso. */
    public boolean isLoading() { return status == Status.LOADING; }

    /** @return true si la operación finalizó con éxito. */
    public boolean isSuccess() { return status == Status.SUCCESS; }

    /** @return true si la operación falló. */
    public boolean isError() { return status == Status.ERROR; }
}
