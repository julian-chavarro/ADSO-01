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
 * Repositorio encargado de gestionar la persistencia de las rutinas y series en Firestore.
 * Sigue el patrón Repository para desacoplar la lógica de datos de la UI.
 */
public class RoutineRepository {

    // Constantes para nombres de colecciones (Evita errores de dedo)
    private static final String COL_USUARIOS = "usuarios";
    private static final String COL_SESIONES = "sesiones";
    private static final String COL_EJERCICIOS = "ejercicios";
    private static final String COL_SERIES = "series";

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    /**
     * Constructor estándar que inicializa las instancias de Firebase.
     */
    public RoutineRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Constructor para Inyección de Dependencias (útil para Unit Testing).
     */
    public RoutineRepository(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
    }

    /**
     * Registra una serie completada en Firestore.
     * Estructura jerárquica segura: usuarios/{uid}/sesiones/{fecha}/ejercicios/{nombre_sanitizado}/series/serie_{n}
     *
     * @param nombreEjercicio Nombre del ejercicio a registrar.
     * @param serie Objeto con los datos de repeticiones y peso.
     * @param callback Callback para notificar el éxito o error de la operación.
     */
    public void registrarSerie(String nombreEjercicio, Serie serie, AuthOperationCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onComplete(false, new Exception("Sesión de usuario no encontrada."));
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        
        // Sanitización del ID del ejercicio: reemplaza caracteres no permitidos por guiones bajos
        String ejercicioId = nombreEjercicio.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

        Map<String, Object> serieMap = new HashMap<>();
        serieMap.put("numero", serie.getNumero());
        serieMap.put("repeticiones", serie.getRepeticiones());
        serieMap.put("peso", serie.getPeso());
        serieMap.put("completada", true);
        serieMap.put("timestamp", FieldValue.serverTimestamp()); // Usa la hora del servidor de Firebase

        db.collection(COL_USUARIOS).document(uid)
                .collection(COL_SESIONES).document(fechaHoy)
                .collection(COL_EJERCICIOS).document(ejercicioId)
                .collection(COL_SERIES).document("serie_" + serie.getNumero())
                .set(serieMap, SetOptions.merge())
                .addOnCompleteListener(task -> 
                        callback.onComplete(task.isSuccessful(), task.getException()));
    }
}
