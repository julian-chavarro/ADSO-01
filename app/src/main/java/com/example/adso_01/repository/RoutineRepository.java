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
        
        // Sanitización del ID del ejercicio para Firestore
        String ejercicioId = nombreEjercicio.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

        Map<String, Object> serieMap = new HashMap<>();
        serieMap.put("numero", serie.getNumero());
        serieMap.put("repeticiones", serie.getRepeticiones());
        serieMap.put("peso", serie.getPeso());
        serieMap.put("completada", true);
        serieMap.put("timestamp", FieldValue.serverTimestamp()); // Usa el timestamp del servidor de Firestore

        db.collection("usuarios").document(uid)
                .collection("sesiones").document(fechaHoy)
                .collection("ejercicios").document(ejercicioId)
                .collection("series").document("serie_" + serie.getNumero())
                .set(serieMap, SetOptions.merge())
                .addOnCompleteListener(task -> 
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }
}
