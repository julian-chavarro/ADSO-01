package com.example.adso_01.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.adso_01.model.Ejercicio;
import com.example.adso_01.model.Serie;
import com.example.adso_01.repository.AuthOperationCallback;
import com.example.adso_01.repository.RoutineRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RoutineViewModel extends ViewModel {

    private final RoutineRepository repository;

    public RoutineViewModel() {
        repository = new RoutineRepository();
    }

    /**
     * Obtiene la rutina detallada según el objetivo y el día especificado.
     * Si el día es nulo, usa el día actual del sistema.
     */
    public List<Ejercicio> obtenerRutinaSugerida(String objetivo, String diaForzado) {
        String dia = (diaForzado != null) ? diaForzado : getDiaSemanaActual();
        List<Ejercicio> rutina = new ArrayList<>();

        if ("Perder grasa".equals(objetivo)) {
            switch (dia) {
                case "Lunes":
                    rutina.add(new Ejercicio("Press banca plano", 4, "Pecho", ""));
                    rutina.add(new Ejercicio("Press inclinado mancuernas", 4, "Pecho", ""));
                    rutina.add(new Ejercicio("Aperturas en polea", 3, "Pecho", ""));
                    rutina.add(new Ejercicio("Fondos", 3, "Tríceps", ""));
                    rutina.add(new Ejercicio("Extensión tríceps polea", 4, "Tríceps", ""));
                    rutina.add(new Ejercicio("Rompecráneos", 3, "Tríceps", ""));
                    rutina.add(new Ejercicio("Cardio: HIIT", 1, "Cardio", ""));
                    break;
                case "Martes":
                    rutina.add(new Ejercicio("Dominadas", 4, "Espalda", ""));
                    rutina.add(new Ejercicio("Remo con barra", 4, "Espalda", ""));
                    rutina.add(new Ejercicio("Jalón al pecho", 4, "Espalda", ""));
                    rutina.add(new Ejercicio("Remo sentado", 3, "Espalda", ""));
                    rutina.add(new Ejercicio("Curl barra Z", 4, "Bíceps", ""));
                    rutina.add(new Ejercicio("Curl martillo", 3, "Bíceps", ""));
                    break;
                case "Miércoles":
                    rutina.add(new Ejercicio("Sentadilla libre", 4, "Pierna", ""));
                    rutina.add(new Ejercicio("Prensa", 4, "Pierna", ""));
                    rutina.add(new Ejercicio("Peso muerto rumano", 4, "Pierna", ""));
                    rutina.add(new Ejercicio("Extensión cuádriceps", 3, "Pierna", ""));
                    rutina.add(new Ejercicio("Curl femoral", 3, "Pierna", ""));
                    rutina.add(new Ejercicio("Gemelos", 5, "Pierna", ""));
                    rutina.add(new Ejercicio("Caminata inclinada", 1, "Cardio", ""));
                    break;
                case "Jueves":
                    rutina.add(new Ejercicio("Press militar", 4, "Hombro", ""));
                    rutina.add(new Ejercicio("Elevaciones laterales", 4, "Hombro", ""));
                    rutina.add(new Ejercicio("Elevaciones frontales", 3, "Hombro", ""));
                    rutina.add(new Ejercicio("Pájaros", 3, "Hombro", ""));
                    rutina.add(new Ejercicio("Encogimientos trapecio", 4, "Hombro", ""));
                    rutina.add(new Ejercicio("Crunch", 4, "Abdomen", ""));
                    rutina.add(new Ejercicio("Elevaciones piernas", 4, "Abdomen", ""));
                    rutina.add(new Ejercicio("Plancha", 4, "Abdomen", ""));
                    break;
                case "Viernes":
                    rutina.add(new Ejercicio("Burpees (5 rondas)", 1, "Metabólico", ""));
                    rutina.add(new Ejercicio("Sentadilla mancuerna", 1, "Metabólico", ""));
                    rutina.add(new Ejercicio("Flexiones", 1, "Metabólico", ""));
                    rutina.add(new Ejercicio("Remo mancuerna", 1, "Metabólico", ""));
                    rutina.add(new Ejercicio("Swing kettlebell", 1, "Metabólico", ""));
                    rutina.add(new Ejercicio("Mountain climbers", 1, "Metabólico", ""));
                    break;
                default:
                    rutina.add(new Ejercicio("Descanso Activo", 1, "Recuperación", ""));
                    break;
            }
        } else if ("Ganar músculo".equals(objetivo)) {
            switch (dia) {
                case "Lunes":
                    rutina.add(new Ejercicio("Press banca", 5, "Pecho", ""));
                    rutina.add(new Ejercicio("Press inclinado", 4, "Pecho", ""));
                    rutina.add(new Ejercicio("Press declinado", 3, "Pecho", ""));
                    rutina.add(new Ejercicio("Aperturas", 3, "Pecho", ""));
                    rutina.add(new Ejercicio("Pullover", 3, "Pecho", ""));
                    break;
                case "Martes":
                    rutina.add(new Ejercicio("Peso muerto", 5, "Espalda", ""));
                    rutina.add(new Ejercicio("Dominadas lastradas", 4, "Espalda", ""));
                    rutina.add(new Ejercicio("Remo barra", 4, "Espalda", ""));
                    rutina.add(new Ejercicio("Jalón cerrado", 3, "Espalda", ""));
                    rutina.add(new Ejercicio("Remo mancuerna", 3, "Espalda", ""));
                    break;
                case "Miércoles":
                    rutina.add(new Ejercicio("Sentadilla profunda", 5, "Pierna", ""));
                    rutina.add(new Ejercicio("Prensa pesada", 4, "Pierna", ""));
                    rutina.add(new Ejercicio("Peso muerto rumano", 4, "Pierna", ""));
                    rutina.add(new Ejercicio("Curl femoral", 4, "Pierna", ""));
                    rutina.add(new Ejercicio("Extensiones", 4, "Pierna", ""));
                    rutina.add(new Ejercicio("Gemelos", 5, "Pierna", ""));
                    break;
                case "Jueves":
                    rutina.add(new Ejercicio("Press militar", 5, "Hombro", ""));
                    rutina.add(new Ejercicio("Elevaciones laterales", 4, "Hombro", ""));
                    rutina.add(new Ejercicio("Press Arnold", 4, "Hombro", ""));
                    rutina.add(new Ejercicio("Pájaros", 4, "Hombro", ""));
                    rutina.add(new Ejercicio("Trapecio barra", 4, "Hombro", ""));
                    break;
                case "Viernes":
                    rutina.add(new Ejercicio("Curl barra", 4, "Bíceps", ""));
                    rutina.add(new Ejercicio("Curl inclinado", 4, "Bíceps", ""));
                    rutina.add(new Ejercicio("Curl martillo", 3, "Bíceps", ""));
                    rutina.add(new Ejercicio("Press cerrado", 4, "Tríceps", ""));
                    rutina.add(new Ejercicio("Fondos", 4, "Tríceps", ""));
                    rutina.add(new Ejercicio("Extensión cuerda", 3, "Tríceps", ""));
                    rutina.add(new Ejercicio("Crunch polea", 4, "Abdomen", ""));
                    rutina.add(new Ejercicio("Plancha", 4, "Abdomen", ""));
                    break;
                default:
                    rutina.add(new Ejercicio("Descanso / Estiramiento", 1, "Recuperación", ""));
                    break;
            }
        } else { // Mantenimiento Físico
            switch (dia) {
                case "Lunes":
                    rutina.add(new Ejercicio("Press banca", 4, "Superior", ""));
                    rutina.add(new Ejercicio("Remo barra", 4, "Superior", ""));
                    rutina.add(new Ejercicio("Press militar", 3, "Superior", ""));
                    rutina.add(new Ejercicio("Dominadas", 3, "Superior", ""));
                    rutina.add(new Ejercicio("Curl bíceps", 3, "Superior", ""));
                    rutina.add(new Ejercicio("Tríceps polea", 3, "Superior", ""));
                    break;
                case "Martes":
                    rutina.add(new Ejercicio("Trote 30 min", 1, "Cardio", ""));
                    rutina.add(new Ejercicio("Bicicleta 20 min", 1, "Cardio", ""));
                    rutina.add(new Ejercicio("Crunch", 4, "Abdomen", ""));
                    rutina.add(new Ejercicio("Plancha", 4, "Abdomen", ""));
                    rutina.add(new Ejercicio("Russian twist", 4, "Abdomen", ""));
                    break;
                case "Miércoles":
                    rutina.add(new Ejercicio("Sentadilla", 4, "Pierna", ""));
                    rutina.add(new Ejercicio("Prensa", 4, "Pierna", ""));
                    rutina.add(new Ejercicio("Peso muerto rumano", 4, "Pierna", ""));
                    rutina.add(new Ejercicio("Curl femoral", 3, "Pierna", ""));
                    rutina.add(new Ejercicio("Gemelos", 4, "Pierna", ""));
                    break;
                case "Jueves":
                    rutina.add(new Ejercicio("Estiramientos dinámicos", 1, "Movilidad", ""));
                    rutina.add(new Ejercicio("Caminata rápida 40 min", 1, "Cardio", ""));
                    rutina.add(new Ejercicio("Trabajo movilidad hombro/cadera", 1, "Movilidad", ""));
                    break;
                case "Viernes":
                    rutina.add(new Ejercicio("Dominadas", 3, "Full Body", ""));
                    rutina.add(new Ejercicio("Flexiones", 3, "Full Body", ""));
                    rutina.add(new Ejercicio("Sentadilla goblet", 3, "Full Body", ""));
                    rutina.add(new Ejercicio("Peso muerto mancuerna", 3, "Full Body", ""));
                    rutina.add(new Ejercicio("Press hombro", 3, "Full Body", ""));
                    rutina.add(new Ejercicio("Plancha", 3, "Full Body", ""));
                    break;
                default:
                    rutina.add(new Ejercicio("Descanso", 1, "Recuperación", ""));
                    break;
            }
        }

        return rutina;
    }

    private String getDiaSemanaActual() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY: return "Lunes";
            case Calendar.TUESDAY: return "Martes";
            case Calendar.WEDNESDAY: return "Miércoles";
            case Calendar.THURSDAY: return "Jueves";
            case Calendar.FRIDAY: return "Viernes";
            case Calendar.SATURDAY: return "Sábado";
            case Calendar.SUNDAY: return "Domingo";
            default: return "Lunes";
        }
    }

    public void registrarSerie(String nombreEjercicio, Serie serie, AuthOperationCallback callback) {
        repository.registrarSerie(nombreEjercicio, serie, callback);
    }
}
