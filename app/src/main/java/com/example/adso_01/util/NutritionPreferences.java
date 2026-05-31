package com.example.adso_01.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Persistencia ligera del progreso nutricional diario.
 * Cada día almacena qué comidas (desayuno, almuerzo, cena) fueron completadas.
 * Usa zona horaria Colombia (America/Bogota) para el reinicio diario automático.
 * No depende de Firebase — usa SharedPreferences para mantener la arquitectura MVVM limpia.
 */
public class NutritionPreferences {

    private static final String PREFS_NAME = "nutrition_progress";
    private static final String KEY_BREAKFAST = "breakfast_";
    private static final String KEY_LUNCH = "lunch_";
    private static final String KEY_DINNER = "dinner_";

    private static final TimeZone TZ_BOGOTA = TimeZone.getTimeZone("America/Bogota");
    private static final Locale LOCALE_CO = new Locale("es", "CO");

    private NutritionPreferences() {}

    /**
     * Marca una comida como completada para la fecha de hoy (zona Colombia).
     * @param tipo "desayuno", "almuerzo", o "cena"
     */
    public static void marcarComida(Context context, String tipo) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String today = getTodayColombia();
        prefs.edit().putBoolean(getKey(tipo, today), true).apply();
    }

    /**
     * Devuelve el estado de las 3 comidas para la fecha actual (zona Colombia).
     * @return boolean[3] = {desayuno, almuerzo, cena}
     */
    public static boolean[] getEstadoHoy(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String today = getTodayColombia();
        return new boolean[]{
                prefs.getBoolean(KEY_BREAKFAST + today, false),
                prefs.getBoolean(KEY_LUNCH + today, false),
                prefs.getBoolean(KEY_DINNER + today, false)
        };
    }

    /**
     * Calcula el porcentaje de progreso nutricional del día de hoy (0-100).
     */
    public static int getProgresoHoy(Context context) {
        boolean[] estado = getEstadoHoy(context);
        int count = 0;
        for (boolean b : estado) {
            if (b) count++;
        }
        return (count * 100) / 3;
    }

    private static String getKey(String tipo, String date) {
        switch (tipo) {
            case "desayuno": return KEY_BREAKFAST + date;
            case "almuerzo": return KEY_LUNCH + date;
            case "cena":     return KEY_DINNER + date;
            default: throw new IllegalArgumentException("Tipo inválido: " + tipo);
        }
    }

    /**
     * Obtiene la fecha actual en zona horaria Colombia.
     * Esto asegura que el progreso nutricional se reinicie automáticamente
     * al cambiar de día según la hora local colombiana.
     */
    private static String getTodayColombia() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setTimeZone(TZ_BOGOTA);
        return sdf.format(new Date());
    }
}
