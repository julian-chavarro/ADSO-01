package com.example.adso_01.repository;

import com.example.adso_01.model.ProgresoEjercicio;
import com.example.adso_01.model.ResumenSesion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProgressRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public ProgressRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void obtenerSesiones(ProgressSesionesCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onResult(null, new Exception("Sesión de usuario no encontrada."));
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("usuarios").document(uid)
                .collection("sesiones")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ResumenSesion> sesiones = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            ResumenSesion s = new ResumenSesion();
                            s.setFecha(doc.getString("fecha"));
                            Object horaInicio = doc.get("horaInicio");
                            if (horaInicio instanceof com.google.firebase.Timestamp) {
                                s.setHoraInicio(((com.google.firebase.Timestamp) horaInicio).toDate().getTime());
                            }
                            Object horaFin = doc.get("horaFin");
                            if (horaFin instanceof com.google.firebase.Timestamp) {
                                s.setHoraFin(((com.google.firebase.Timestamp) horaFin).toDate().getTime());
                            }
                            s.setTotalEjercicios(doc.getLong("totalEjercicios") != null
                                    ? doc.getLong("totalEjercicios").intValue() : 0);
                            s.setCompletada(Boolean.TRUE.equals(doc.getBoolean("completada")));
                            sesiones.add(s);
                        }
                        callback.onResult(sesiones, null);
                    } else {
                        callback.onResult(null, task.getException());
                    }
                });
    }

    public void obtenerProgresoEjercicios(ProgressEjerciciosCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onResult(null, new Exception("Sesión de usuario no encontrada."));
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("usuarios").document(uid)
                .collection("progreso_ejercicios")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ProgresoEjercicio> progresoList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            ProgresoEjercicio p = new ProgresoEjercicio();
                            p.setNombreEjercicio(doc.getString("nombreEjercicio"));
                            p.setGrupoMuscular(doc.getString("grupoMuscular"));
                            p.setFecha(doc.getString("fecha"));
                            p.setPesoMaximo(doc.getDouble("pesoMaximo") != null ? doc.getDouble("pesoMaximo") : 0);
                            p.setVolumenTotal(doc.getDouble("volumenTotal") != null ? doc.getDouble("volumenTotal") : 0);
                            p.setRepeticionesTotales(doc.getLong("repeticionesTotales") != null
                                    ? doc.getLong("repeticionesTotales").intValue() : 0);
                            p.setSeriesCompletadas(doc.getLong("seriesCompletadas") != null
                                    ? doc.getLong("seriesCompletadas").intValue() : 0);
                            progresoList.add(p);
                        }
                        callback.onResult(progresoList, null);
                    } else {
                        callback.onResult(null, task.getException());
                    }
                });
    }
}
