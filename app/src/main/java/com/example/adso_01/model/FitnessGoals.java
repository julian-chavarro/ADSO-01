package com.example.adso_01.model;

/**
 * Valores de objetivo usados en Firestore, spinners y rutinas.
 * Almacena y muestra EXACTAMENTE lo que el usuario seleccionó.
 * El normalizador solo se usa para backward-compat con datos migrados.
 */
public final class FitnessGoals {

    public static final String GANAR_MASA_MUSCULAR = "Ganar masa muscular";
    public static final String PERDER_GRASA = "Perder grasa";
    public static final String RECOMPOSICION_CORPORAL = "Recomposición corporal";
    public static final String MANTENER_PESO = "Mantener peso";

    // Constantes legacy para compatibilidad con datos existentes en Firebase
    public static final String GANAR_MUSCULO = "Ganar músculo";

    public static final String[] OBJETIVOS = {
            GANAR_MASA_MUSCULAR,
            PERDER_GRASA,
            RECOMPOSICION_CORPORAL,
            MANTENER_PESO
    };

    /**
     * Resuelve el ajuste calórico según el objetivo.
     * Soporta tanto los nuevos valores exactos como los legacy.
     */
    public static int getAjusteCalorico(String objetivo) {
        if (objetivo == null) return 0;
        String lower = objetivo.toLowerCase();

        if (lower.contains("ganar") || lower.contains("masa") || lower.contains("musculo")) {
            return 300;
        } else if (lower.contains("perder") || lower.contains("grasa")) {
            return -300;
        }
        return 0; // Mantener peso, recomposición corporal, o cualquier otro
    }

    private FitnessGoals() {
    }
}
