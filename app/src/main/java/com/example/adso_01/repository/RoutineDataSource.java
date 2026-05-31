package com.example.adso_01.repository;

import com.example.adso_01.model.Ejercicio;
import com.example.adso_01.model.FitnessGoals;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Catálogo maestro de rutinas.
 * Centraliza la lógica de ejercicios según el objetivo del usuario.
 */
public class RoutineDataSource {

    /**
     * Obtiene una rutina completa para el grupo muscular seleccionado,
     * independientemente del objetivo del usuario.
     */
    public List<Ejercicio> obtenerRutinaPorGrupoMuscular(String grupoMuscular) {
        String grupo = normalizar(grupoMuscular);
        List<Ejercicio> rutina = new ArrayList<>();

        if (grupo.contains("pecho")) {
            rutina.add(new Ejercicio("Press banca plano", 4, "Pecho", ""));
            rutina.add(new Ejercicio("Press inclinado mancuernas", 4, "Pecho", ""));
            rutina.add(new Ejercicio("Aperturas en polea", 3, "Pecho", ""));
            rutina.add(new Ejercicio("Fondos en paralelas", 3, "Tríceps", ""));
            rutina.add(new Ejercicio("Extensión tríceps polea", 4, "Tríceps", ""));
        } else if (grupo.contains("espalda")) {
            rutina.add(new Ejercicio("Dominadas", 4, "Espalda", ""));
            rutina.add(new Ejercicio("Remo con barra", 4, "Espalda", ""));
            rutina.add(new Ejercicio("Jalón al pecho", 4, "Espalda", ""));
            rutina.add(new Ejercicio("Curl barra Z", 4, "Bíceps", ""));
            rutina.add(new Ejercicio("Curl martillo", 3, "Bíceps", ""));
        } else if (grupo.contains("pierna")) {
            rutina.add(new Ejercicio("Sentadilla libre", 4, "Pierna", ""));
            rutina.add(new Ejercicio("Prensa de piernas", 4, "Pierna", ""));
            rutina.add(new Ejercicio("Peso muerto rumano", 4, "Pierna", ""));
            rutina.add(new Ejercicio("Extensiones cuádriceps", 4, "Pierna", ""));
            rutina.add(new Ejercicio("Gemelos de pie", 5, "Pierna", ""));
        } else if (grupo.contains("hombro")) {
            rutina.add(new Ejercicio("Press militar barra", 4, "Hombro", ""));
            rutina.add(new Ejercicio("Elevaciones laterales", 4, "Hombro", ""));
            rutina.add(new Ejercicio("Pájaros posterior", 3, "Hombro", ""));
            rutina.add(new Ejercicio("Crunch Abdominal", 4, "Core", ""));
            rutina.add(new Ejercicio("Plancha", 4, "Core", ""));
        } else if (grupo.contains("full") || grupo.contains("cuerpo")) {
            rutina.add(new Ejercicio("Sentadilla Goblet", 4, "Full Body", ""));
            rutina.add(new Ejercicio("Flexiones de pecho", 4, "Full Body", ""));
            rutina.add(new Ejercicio("Remo con mancuerna", 4, "Full Body", ""));
            rutina.add(new Ejercicio("Press hombro mancuerna", 3, "Full Body", ""));
            rutina.add(new Ejercicio("Plancha Abdominal", 3, "Core", ""));
        }

        // Fallback: si no hay match exacto, devolver rutina de movilidad
        if (rutina.isEmpty()) {
            rutina.add(new Ejercicio("Movilidad Articular Dinámica", 3, "Calentamiento", ""));
            rutina.add(new Ejercicio("Estiramiento de Cadenas Posteriores", 3, "Movilidad", ""));
            rutina.add(new Ejercicio("Caminata de Recuperación (20 min)", 1, "Cardio", ""));
        }

        return rutina;
    }

    public List<Ejercicio> obtenerRutinaSugerida(String objetivo, String diaForzado) {
        String dia = (diaForzado != null) ? diaForzado : getDiaSemanaActual();
        List<Ejercicio> rutina = new ArrayList<>();

        String objNorm = normalizar(objetivo);
        String diaNorm = normalizar(dia);

        // Selección robusta de planes
        if (objNorm.contains("grasa") || (objNorm.contains("perder") && objNorm.contains("peso"))) {
            agregarPlanPerderGrasa(rutina, diaNorm);
        } else if (objNorm.contains("musculo") || objNorm.contains("masa")) {
            agregarPlanGanarMusculo(rutina, diaNorm);
        } else {
            agregarPlanMantenimiento(rutina, diaNorm);
        }

        // Fallback: Si es fin de semana o no hay plan, cargar rutina de movilidad completa
        if (rutina.isEmpty()) {
            rutina.add(new Ejercicio("Movilidad Articular Dinámica", 3, "Calentamiento", ""));
            rutina.add(new Ejercicio("Estiramiento de Cadenas Posteriores", 3, "Movilidad", ""));
            rutina.add(new Ejercicio("Caminata de Recuperación (20 min)", 1, "Cardio", ""));
        }

        return rutina;
    }

    private void agregarPlanPerderGrasa(List<Ejercicio> rutina, String dia) {
        if (dia.contains("lunes")) {
            rutina.add(new Ejercicio("Press banca plano", 4, "Pecho", ""));
            rutina.add(new Ejercicio("Press inclinado mancuernas", 4, "Pecho", ""));
            rutina.add(new Ejercicio("Aperturas en polea", 3, "Pecho", ""));
            rutina.add(new Ejercicio("Fondos", 3, "Tríceps", ""));
            rutina.add(new Ejercicio("Extensión tríceps polea", 4, "Tríceps", ""));
            rutina.add(new Ejercicio("HIIT en Cinta (20 min)", 1, "Cardio", ""));
        } else if (dia.contains("martes")) {
            rutina.add(new Ejercicio("Dominadas", 4, "Espalda", ""));
            rutina.add(new Ejercicio("Remo con barra", 4, "Espalda", ""));
            rutina.add(new Ejercicio("Jalón al pecho", 4, "Espalda", ""));
            rutina.add(new Ejercicio("Curl barra Z", 4, "Bíceps", ""));
            rutina.add(new Ejercicio("Curl martillo", 3, "Bíceps", ""));
        } else if (dia.contains("miercoles")) {
            rutina.add(new Ejercicio("Sentadilla libre", 4, "Pierna", ""));
            rutina.add(new Ejercicio("Prensa de piernas", 4, "Pierna", ""));
            rutina.add(new Ejercicio("Peso muerto rumano", 4, "Pierna", ""));
            rutina.add(new Ejercicio("Gemelos", 5, "Pierna", ""));
            rutina.add(new Ejercicio("Caminata inclinada", 1, "Cardio", ""));
        } else if (dia.contains("jueves")) {
            rutina.add(new Ejercicio("Press militar", 4, "Hombro", ""));
            rutina.add(new Ejercicio("Elevaciones laterales", 4, "Hombro", ""));
            rutina.add(new Ejercicio("Pájaros posterior", 3, "Hombro", ""));
            rutina.add(new Ejercicio("Crunch Abdominal", 4, "Core", ""));
            rutina.add(new Ejercicio("Plancha", 4, "Core", ""));
        } else if (dia.contains("viernes")) {
            rutina.add(new Ejercicio("Burpees", 5, "Metabólico", ""));
            rutina.add(new Ejercicio("Sentadilla con mancuerna", 5, "Metabólico", ""));
            rutina.add(new Ejercicio("Flexiones de pecho", 5, "Metabólico", ""));
            rutina.add(new Ejercicio("Mountain Climbers", 5, "Metabólico", ""));
        }
    }

    private void agregarPlanGanarMusculo(List<Ejercicio> rutina, String dia) {
        if (dia.contains("lunes")) {
            rutina.add(new Ejercicio("Press banca", 5, "Pecho", ""));
            rutina.add(new Ejercicio("Press inclinado", 4, "Pecho", ""));
            rutina.add(new Ejercicio("Pullover con mancuerna", 3, "Pecho", ""));
            rutina.add(new Ejercicio("Aperturas planas", 3, "Pecho", ""));
        } else if (dia.contains("martes")) {
            rutina.add(new Ejercicio("Peso muerto", 5, "Espalda", ""));
            rutina.add(new Ejercicio("Dominadas lastradas", 4, "Espalda", ""));
            rutina.add(new Ejercicio("Remo con barra", 4, "Espalda", ""));
            rutina.add(new Ejercicio("Remo con mancuerna", 3, "Espalda", ""));
        } else if (dia.contains("miercoles")) {
            rutina.add(new Ejercicio("Sentadilla profunda", 5, "Pierna", ""));
            rutina.add(new Ejercicio("Prensa pesada", 4, "Pierna", ""));
            rutina.add(new Ejercicio("Curl femoral", 4, "Pierna", ""));
            rutina.add(new Ejercicio("Extensiones cuádriceps", 4, "Pierna", ""));
        } else if (dia.contains("jueves")) {
            rutina.add(new Ejercicio("Press militar barra", 5, "Hombro", ""));
            rutina.add(new Ejercicio("Elevaciones laterales", 4, "Hombro", ""));
            rutina.add(new Ejercicio("Press Arnold", 4, "Hombro", ""));
            rutina.add(new Ejercicio("Trapecio con barra", 4, "Hombro", ""));
        } else if (dia.contains("viernes")) {
            rutina.add(new Ejercicio("Curl barra Z", 4, "Brazos", ""));
            rutina.add(new Ejercicio("Curl inclinado", 4, "Brazos", ""));
            rutina.add(new Ejercicio("Press francés", 4, "Brazos", ""));
            rutina.add(new Ejercicio("Fondos en paralelas", 4, "Brazos", ""));
        }
    }

    private void agregarPlanMantenimiento(List<Ejercicio> rutina, String dia) {
        if (dia.contains("lunes") || dia.contains("miercoles") || dia.contains("viernes")) {
            rutina.add(new Ejercicio("Sentadilla Goblet", 4, "Full Body", ""));
            rutina.add(new Ejercicio("Flexiones de pecho", 4, "Full Body", ""));
            rutina.add(new Ejercicio("Remo con mancuerna", 4, "Full Body", ""));
            rutina.add(new Ejercicio("Press hombro mancuerna", 3, "Full Body", ""));
            rutina.add(new Ejercicio("Plancha Abdominal", 3, "Core", ""));
        }
    }

    private String getDiaSemanaActual() {
        String[] dias = {"domingo", "lunes", "martes", "miercoles", "jueves", "viernes", "sabado"};
        return dias[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
    }

    private String normalizar(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase().trim();
    }
}
