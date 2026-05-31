package com.example.adso_01.repository;

import com.example.adso_01.model.Serie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Repositorio encargado de gestionar la persistencia de las rutinas y series en Cloud Firestore.
 * Sigue el patrón Repository definido en AGENTS.md.
 */
public class RoutineRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public RoutineRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Registra una serie completada en Cloud Firestore.
     * Estructura: usuarios/{uid}/sesiones/{fecha}/ejercicios/{nombre_ejercicio}/series/serie_{n}
     */
    public void registrarSerie(String nombreEjercicio, Serie serie, AuthOperationCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onComplete(false, new Exception("Sesión de usuario no encontrada."));
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        // Sanitización del ID del ejercicio para evitar inyección de rutas en Firestore
        String ejercicioId = nombreEjercicio.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

        Map<String, Object> serieMap = new HashMap<>();
        serieMap.put("numero", serie.getNumero());
        serieMap.put("repeticiones", serie.getRepeticiones());
        serieMap.put("peso", serie.getPeso());
        serieMap.put("completada", true);
        serieMap.put("timestamp", FieldValue.serverTimestamp());

        db.collection("usuarios").document(uid)
                .collection("sesiones").document(fechaHoy)
                .collection("ejercicios").document(ejercicioId)
                .collection("series").document("serie_" + serie.getNumero())
                .set(serieMap, SetOptions.merge())
                .addOnCompleteListener(task ->
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }

    /**
     * Crea o actualiza el documento de sesión del día con la hora de inicio.
     */
    public void iniciarSesion(String fecha, int totalEjercicios, String diaNombre, AuthOperationCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onComplete(false, new Exception("Sesión de usuario no encontrada."));
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> sesionMap = new HashMap<>();
        sesionMap.put("fecha", fecha);
        sesionMap.put("horaInicio", FieldValue.serverTimestamp());
        sesionMap.put("totalEjercicios", totalEjercicios);
        sesionMap.put("completada", false);
        if (diaNombre != null) {
            sesionMap.put("diaNombre", diaNombre);
        }

        db.collection("usuarios").document(uid)
                .collection("sesiones").document(fecha)
                .set(sesionMap, SetOptions.merge())
                .addOnCompleteListener(task ->
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }

    /**
     * Marca la sesión del día como completada con hora de fin.
     */
    public void finalizarSesion(String fecha, AuthOperationCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onComplete(false, new Exception("Sesión de usuario no encontrada."));
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> updates = new HashMap<>();
        updates.put("horaFin", FieldValue.serverTimestamp());
        updates.put("completada", true);

        db.collection("usuarios").document(uid)
                .collection("sesiones").document(fecha)
                .update(updates)
                .addOnCompleteListener(task ->
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }

    /**
     * Guarda un resumen del progreso de un ejercicio en una sesión.
     * Colección plana: usuarios/{uid}/progreso_ejercicios/
     */
    public void guardarProgresoEjercicio(String nombreEjercicio, String grupoMuscular, String fecha,
                                          double pesoMaximo, double volumenTotal,
                                          int repeticionesTotales, int seriesCompletadas,
                                          AuthOperationCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onComplete(false, new Exception("Sesión de usuario no encontrada."));
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> progresoMap = new HashMap<>();
        progresoMap.put("nombreEjercicio", nombreEjercicio);
        progresoMap.put("grupoMuscular", grupoMuscular);
        progresoMap.put("fecha", fecha);
        progresoMap.put("pesoMaximo", pesoMaximo);
        progresoMap.put("volumenTotal", volumenTotal);
        progresoMap.put("repeticionesTotales", repeticionesTotales);
        progresoMap.put("seriesCompletadas", seriesCompletadas);
        progresoMap.put("timestamp", FieldValue.serverTimestamp());

        db.collection("usuarios").document(uid)
                .collection("progreso_ejercicios")
                .add(progresoMap)
                .addOnCompleteListener(task ->
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }
}
